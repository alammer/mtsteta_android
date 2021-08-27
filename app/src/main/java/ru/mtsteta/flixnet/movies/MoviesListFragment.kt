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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.database.toDomainModel
import ru.mtsteta.flixnet.genres.GenreClickListener
import ru.mtsteta.flixnet.genres.GenreListAdapter
import ru.mtsteta.flixnet.repo.MovieDto
import ru.mtsteta.flixnet.repo.RefreshDataStatus

class MoviesListFragment : Fragment() {

    private val moviesListViewModel: MoviesListViewModel by viewModels()

    private lateinit var genreRecycler: RecyclerView
    private lateinit var movieRecycler: RecyclerView
    //private lateinit var swipeRefresher: SwipeRefreshLayout
    private lateinit var genreAdapter: GenreListAdapter
    private lateinit var movieAdapter: MovieListAdapter

    private var isLoading: Boolean = false
    private var visibleThreshold = 1
    private var lastVisibleItem = 0
    private var totalItemCount = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movies_list, container, false)
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        //moviesListViewModel.movieList.observe(viewLifecycleOwner, {
        //    it?.let {
        //    }
        //    isLoading = false
        //})

        moviesListViewModel.genreList.observe(viewLifecycleOwner, {
            genreAdapter.submitList(it.values.toList())
        })

        moviesListViewModel.refreshStatus.observe(viewLifecycleOwner, {
            Log.i(
                "MovieLocal",
                "Function called: changeStatus = ${moviesListViewModel.changeStatus}"
            )
            //swipeRefresher.isRefreshing = false
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
                        Toast.makeText(context, "MovieLocal list updated", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                moviesListViewModel.changeStatus = false
            }
        })

        fetchMoviePages()
    }

    private fun initViews(view: View) {

        genreRecycler = view.findViewById(R.id.rvGenreList)
        movieRecycler = view.findViewById(R.id.rvMovieList)
        //swipeRefresher = view.findViewById(R.id.swipeLayout)

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

        movieRecycler.adapter = movieAdapter

        movieRecycler.addItemDecoration(MovieSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.movieslist_rv_top_spacing)))

        //swipeRefresher.setOnRefreshListener {
        //    moviesListViewModel.fetchData(0)
        //}
    }

    @ExperimentalPagingApi
    private fun fetchMoviePages() {
        lifecycleScope.launch {
            moviesListViewModel.fetchMovieFlow().distinctUntilChanged().collectLatest { page ->
                movieAdapter.submitData(page.map { it.toDomainModel() })
            }
        }
    }

    fun addScrollerListener() {
        val moviesLayoutManager = movieRecycler.layoutManager

        movieRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!isLoading) {

                    moviesLayoutManager?.run {

                        totalItemCount = itemCount

                        lastVisibleItem = when (this) {
                            is GridLayoutManager -> findLastVisibleItemPosition()
                            is LinearLayoutManager -> findLastVisibleItemPosition()
                            else -> throw IllegalArgumentException("Uknown layoutmanager type")
                        }

                        Log.i(
                            "Fetch_AddScroll",
                            "totalitem: $totalItemCount, lastvisible: $lastVisibleItem"
                        )

                        if (totalItemCount <= lastVisibleItem + visibleThreshold) {
                            moviesListViewModel.fetchData()
                            isLoading = true
                        }
                    }
                }
            }
        })
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