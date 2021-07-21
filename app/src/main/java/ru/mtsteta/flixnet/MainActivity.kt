package ru.mtsteta.flixnet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mtsteta.flixnet.fakeRepo.MoviesDataSourceImpl
import ru.mtsteta.flixnet.genres.GenreListAdapter
import ru.mtsteta.flixnet.movies.MovieListAdapter
import ru.mtsteta.flixnet.movies.MovieSpaceItemDecoration

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main_screen)

        val fakeMovieData = MoviesDataSourceImpl()

        val genreRecycler = findViewById<RecyclerView>(R.id.rvGenreList)
        val genres = fakeMovieData.genreList
        val genreAdapter = GenreListAdapter(genres)
        genreRecycler.adapter = genreAdapter
        genreRecycler.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val movieRecycler = findViewById<RecyclerView>(R.id.rvMovieList)
        val movies = fakeMovieData.getMovies()
        val movieAdapter = MovieListAdapter()
        movieRecycler.adapter = movieAdapter
        movieRecycler.layoutManager = GridLayoutManager(this, 2)
        movieRecycler.addItemDecoration(MovieSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.item_top_spacing)))
        movieAdapter.submitList(movies)
    }
}
