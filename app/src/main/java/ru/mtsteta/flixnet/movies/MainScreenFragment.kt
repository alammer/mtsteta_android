package ru.mtsteta.flixnet.movies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.*
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.detailinfo.DetailFragment
import ru.mtsteta.flixnet.repo.MoviesDataSourceImpl
import ru.mtsteta.flixnet.genres.GenreClickListener
import ru.mtsteta.flixnet.genres.GenreListAdapter
import ru.mtsteta.flixnet.repo.MovieDto
import java.lang.IllegalArgumentException

class MainScreenFragment : Fragment() {

    private val moviesViewModel: MoviesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val genreRecycler = view.findViewById<RecyclerView>(R.id.rvGenreList)

        val movieRecycler = view.findViewById<RecyclerView>(R.id.rvMovieList)

        val swipeRefresher = view.findViewById<SwipeRefreshLayout>(R.id.swipeLayout)

        val genres = MoviesDataSourceImpl().genreList
        val genreAdapter = GenreListAdapter(GenreClickListener {
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

        moviesViewModel.movieList.observe(viewLifecycleOwner, Observer {
            it?.let { movieAdapter.submitList(it) } ?: Log.i("MainScreenFragment", "Function called: Observe() but movieList is null")
        } )

        moviesViewModel.genreList.observe(viewLifecycleOwner, Observer {
            genreAdapter.submitList(it)
        })

        moviesViewModel.refreshStatus.observe(viewLifecycleOwner, Observer {
            //TODO("change status of refresh")
        })

        swipeRefresher.setOnRefreshListener {
            moviesViewModel.refreshMovieList()
            //swipeRefresher.isRefreshing = false
        }

        super.onViewCreated(view, savedInstanceState)
    }

}