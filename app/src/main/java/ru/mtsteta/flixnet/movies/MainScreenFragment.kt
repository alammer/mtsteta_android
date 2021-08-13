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
import ru.mtsteta.flixnet.repo.RefreshDataStatus

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

        val genreAdapter = GenreListAdapter(GenreClickListener {
            //TODO("We should implement logic for GenreClickListener later")
        })

        genreAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        genreRecycler.adapter = genreAdapter

        val movieAdapter = MovieListAdapter(MovieClickListener { movieItem: MovieDto ->
            val direction = MainScreenFragmentDirections.actionMainScreenFragmentToDetailFragment(movieItem)
            findNavController().navigate(direction)
        })

        movieAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        movieRecycler.adapter = movieAdapter

        movieRecycler.addItemDecoration(MovieSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.mainscreen_movie_top_spacing)))

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
                    else -> Toast.makeText(context, "Movie list updated", Toast.LENGTH_SHORT).show()
                }
                moviesViewModel.changeStatus = false
            }
        })

        swipeRefresher.setOnRefreshListener {
            moviesViewModel.fetchData()
        }

        super.onViewCreated(view, savedInstanceState)
    }

}