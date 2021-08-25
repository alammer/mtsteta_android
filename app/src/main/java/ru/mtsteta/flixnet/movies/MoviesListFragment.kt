package ru.mtsteta.flixnet.movies

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.genres.GenreClickListener
import ru.mtsteta.flixnet.genres.GenreListAdapter
import ru.mtsteta.flixnet.repo.MovieDto
import ru.mtsteta.flixnet.repo.RefreshDataStatus

class MoviesListFragment : Fragment() {

    private val moviesListViewModel: MoviesListViewModel by viewModels()

    private lateinit var genreRecycler: RecyclerView
    private lateinit var movieRecycler: RecyclerView
    private lateinit var swipeRefresher: SwipeRefreshLayout
    private lateinit var genreAdapter: GenreListAdapter
    private lateinit var movieAdapter: MovieListAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movies_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initViews(view)

        moviesListViewModel.movieList.observe(viewLifecycleOwner, {
            it?.let {
                Log.i("MainScreenFragment", "get updated list ${it.toString()}")
                movieAdapter.submitList(it) } ?: Log.i(
                "MainScreenFragment",
                "Function called: Observe() but movieList is null"
            )
        })

        moviesListViewModel.genreList.observe(viewLifecycleOwner, {
            genreAdapter.submitList(it.values.toList())
        })

        moviesListViewModel.refreshStatus.observe(viewLifecycleOwner, {
            Log.i("MovieLocal", "Function called: changeStatus = ${moviesListViewModel.changeStatus}")
            swipeRefresher.isRefreshing = false
            if (moviesListViewModel.changeStatus) {
                when (it) {
                    RefreshDataStatus.ERROR -> Toast.makeText(
                        context,
                        "Server error",
                        Toast.LENGTH_SHORT
                    ).show()
                    RefreshDataStatus.FAILURE -> Toast.makeText(
                        context,
                        "Network connection failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    else -> {
                        Toast.makeText(context, "MovieLocal list updated", Toast.LENGTH_SHORT).show()
                    }
                }
                moviesListViewModel.changeStatus = false
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViews(view: View) {

        genreRecycler = view.findViewById(R.id.rvGenreList)
        movieRecycler = view.findViewById(R.id.rvMovieList)
        swipeRefresher = view.findViewById(R.id.swipeLayout)

        genreAdapter = GenreListAdapter(GenreClickListener {
            //TODO("We should implement logic for GenreClickListener later")
        })

        genreAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        genreRecycler.adapter = genreAdapter

        movieAdapter = MovieListAdapter(MovieClickListener { movieItem: MovieDto ->
            val direction = MoviesListFragmentDirections.actionMoviesToDetail(movieItem)
            findNavController().navigate(direction)
        })

        movieAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        movieRecycler.adapter = movieAdapter

        movieRecycler.addItemDecoration(MovieSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.movieslist_rv_top_spacing)))

        swipeRefresher.setOnRefreshListener {
            moviesListViewModel.fetchData(page = 2, language = "en-EN", region = "US")
        }
    }
}

private fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val activeNetwork =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}