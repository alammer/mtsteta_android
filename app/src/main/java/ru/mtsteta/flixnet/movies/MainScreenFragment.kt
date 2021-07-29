package ru.mtsteta.flixnet.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.detail.DetailFragment
import ru.mtsteta.flixnet.fakeRepo.MoviesDataSourceImpl
import ru.mtsteta.flixnet.genres.GenreClickListener
import ru.mtsteta.flixnet.genres.GenreListAdapter

class MainScreenFragment : Fragment() {

    private var fakeMovieData = MoviesDataSourceImpl()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_screen, container, false)

        val genreRecycler = view?.findViewById<RecyclerView>(R.id.rvGenreList)
        val genres = fakeMovieData.genreList
        val genreAdapter = GenreListAdapter(genres, GenreClickListener { genre ->
            TODO()
        })
        genreAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        genreRecycler?.adapter = genreAdapter

        val movieRecycler = view?.findViewById<RecyclerView>(R.id.rvMovieList)
        val movies = fakeMovieData.getMovies()
        val movieAdapter = MovieListAdapter(MovieClickListener { movieItem: MovieDto ->
            this.activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.main_container, DetailFragment.newInstance(movieItem))
                ?.addToBackStack(null)
                ?.commit()
        })
        movieAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        movieRecycler?.adapter = movieAdapter
        movieRecycler?.addItemDecoration(MovieSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.mainscreen_movie_top_spacing)))
        movieAdapter.submitList(movies)

        return  view
    }
}