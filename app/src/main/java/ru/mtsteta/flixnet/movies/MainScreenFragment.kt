package ru.mtsteta.flixnet.movies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.*
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.detail.DetailFragment
import ru.mtsteta.flixnet.fakeRepo.MoviesDataSourceImpl
import ru.mtsteta.flixnet.genres.GenreClickListener
import ru.mtsteta.flixnet.genres.GenreListAdapter
import java.lang.IllegalArgumentException

class MainScreenFragment : Fragment() {

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        Log.i(
            "MainScreenFragment",
            "Catch exception $exception from coroutine with context $coroutineContext"
        )
        Toast.makeText(context, "Uknown server error", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val swipeRefresher = view.findViewById<SwipeRefreshLayout>(R.id.swipeLayout)

        val movieRecycler = view.findViewById<RecyclerView>(R.id.rvMovieList)

        val genreRecycler = view.findViewById<RecyclerView>(R.id.rvGenreList)

        val genres = MoviesDataSourceImpl().genreList
        val genreAdapter = GenreListAdapter(genres, GenreClickListener {
            TODO("We should implement logic for GenreClickListener later")
        })

        genreAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        genreRecycler?.adapter = genreAdapter

        val movieAdapter = MovieListAdapter(MovieClickListener { movieItem: MovieDto ->
            this.activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.main_container, DetailFragment.newInstance(movieItem))
                ?.addToBackStack(null)
                ?.commit()
        })

        movieAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        movieRecycler?.adapter = movieAdapter

        movieRecycler?.addItemDecoration(MovieSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.mainscreen_movie_top_spacing)))

        refresMovieList(movieAdapter)

        swipeRefresher.setOnRefreshListener {
            refresMovieList(movieAdapter)
            swipeRefresher.isRefreshing = false
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun refresMovieList(movieListAdapter: MovieListAdapter) {

        lifecycleScope.launch(exceptionHandler) {
            val recentMovieList = loadMovieList()

            if (recentMovieList?.any { it.title.isNullOrBlank() } == true) {
                throw IllegalArgumentException("Title Not Found!")
            }

            recentMovieList?.let {
                movieListAdapter.submitList(it)
            } ?: Toast.makeText(context, "Network connection failed", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private suspend fun loadMovieList(): List<MovieDto>? = withContext(Dispatchers.IO) {
        delay(1500L)
        MoviesDataSourceImpl().getMovies()
    }


}