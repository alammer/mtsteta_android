package ru.mtsteta.flixnet.repo

import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

enum class RefreshMovieStatus { FAILURE, ERROR, OK }

class MovieRepository () {

    private val fakeMovieSource = MoviesDataSourceImpl()

    val genreList = fakeMovieSource.genreList

    private suspend fun loadMovieList(): List<MovieDto>? = withContext(Dispatchers.IO) {
        fakeMovieSource.getMovies()
    }

    suspend fun refreshMovies(): Pair<RefreshMovieStatus, List<MovieDto>?> {

        var status =  RefreshMovieStatus.FAILURE

        val recentMovieList = loadMovieList()

        if (recentMovieList?.any { it.title.isNullOrBlank() } == true) {
            return RefreshMovieStatus.ERROR to null
        }

        recentMovieList?.let {
            return RefreshMovieStatus.OK to it
        } ?: return RefreshMovieStatus.FAILURE to null
    }
}