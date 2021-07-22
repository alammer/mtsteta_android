package ru.mtsteta.flixnet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mtsteta.flixnet.fakeRepo.MoviesDataSourceImpl
import ru.mtsteta.flixnet.genres.GenreClickListener
import ru.mtsteta.flixnet.genres.GenreListAdapter
import ru.mtsteta.flixnet.movies.MovieClickListener
import ru.mtsteta.flixnet.movies.MovieDto
import ru.mtsteta.flixnet.movies.MovieListAdapter
import ru.mtsteta.flixnet.movies.MovieSpaceItemDecoration

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main_screen)

        var fakeMovieData = MoviesDataSourceImpl()

        val genreRecycler = findViewById<RecyclerView>(R.id.rvGenreList)
        val genres = fakeMovieData.genreList
        val genreAdapter = GenreListAdapter(genres, GenreClickListener{ genre: String ->
            showToast(genre)
        })
        genreRecycler.adapter = genreAdapter
        //genreRecycler.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val movieRecycler = findViewById<RecyclerView>(R.id.rvMovieList)
        val movies = fakeMovieData.getMovies()
        val movieAdapter = MovieListAdapter(MovieClickListener{ movieItem: MovieDto ->
            showToast(movieItem.title)
        })
        movieRecycler.adapter = movieAdapter
        //movieRecycler.layoutManager = GridLayoutManager(this, 2)
        movieRecycler.addItemDecoration(MovieSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.item_top_spacing)))
        movieAdapter.submitList(movies)
    }

    private fun showToast(message: String?) {
        when {
            message.isNullOrEmpty() -> { showToast(getString(R.string.movie_item_empty_message)) }
            else -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
