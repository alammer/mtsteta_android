package ru.mtsteta.flixnet.movies

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
import ru.mtsteta.flixnet.repo.RefreshMovieStatus

class MainScreenFragment : Fragment() {

    private val moviesViewModel: MoviesViewModel by viewModels()

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
        return inflater.inflate(R.layout.fragment_main_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        init(view)

        moviesViewModel.movieList.observe(viewLifecycleOwner, {
            it?.let { movieAdapter.submitList(it) } ?: Log.i(
                "MainScreenFragment",
                "Function called: Observe() but movieList is null"
            )
        })

        moviesViewModel.genreList.observe(viewLifecycleOwner, {
            genreAdapter.submitList(it)
        })

        moviesViewModel.refreshStatus.observe(viewLifecycleOwner, {
            Log.i("Movie", "Function called: changeStatus = ${moviesViewModel.changeStatus}")
            swipeRefresher.isRefreshing = false
            if (moviesViewModel.changeStatus) {
                when (it) {
                    RefreshMovieStatus.ERROR -> Toast.makeText(
                        context,
                        "Server error",
                        Toast.LENGTH_SHORT
                    ).show()
                    RefreshMovieStatus.FAILURE -> Toast.makeText(
                        context,
                        "Network connection failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    else -> Toast.makeText(context, "Movie list updated", Toast.LENGTH_SHORT).show()
                }
                moviesViewModel.changeStatus = false
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }

    private fun init(view: View) {

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
            val direction = MainScreenFragmentDirections.actionMainToDetail(movieItem)
            findNavController().navigate(direction)
        })

        movieAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        movieRecycler.adapter = movieAdapter

        movieRecycler.addItemDecoration(MovieSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.mainscreen_movie_top_spacing)))

        swipeRefresher.setOnRefreshListener {
            moviesViewModel.refreshMovieList()
        }
    }
}