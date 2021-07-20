package ru.mtsteta.flixnet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mtsteta.flixnet.fakeRepo.MoviesDataSourceImpl
import ru.mtsteta.flixnet.movies.MovieListAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main_screen)

        val movieRecycler = findViewById<RecyclerView>(R.id.rvMovieList)
        val movies = MoviesDataSourceImpl().getMovies()
        val adapter = MovieListAdapter(this)
        movieRecycler.adapter = adapter
        movieRecycler.layoutManager = GridLayoutManager(this, 2)
        adapter.submitList(movies)
    }
}
