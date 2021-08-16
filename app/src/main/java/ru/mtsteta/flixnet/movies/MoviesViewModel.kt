package ru.mtsteta.flixnet.movies

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mtsteta.flixnet.repo.MovieDto
import ru.mtsteta.flixnet.repo.MovieRepository
import ru.mtsteta.flixnet.repo.RefreshMovieStatus

class MoviesViewModel : ViewModel() {

    var changeStatus = false

    private val repository = MovieRepository()

    val refreshStatus: LiveData<RefreshMovieStatus> get() = _refreshStatus
    private val _refreshStatus = MutableLiveData<RefreshMovieStatus>()

    val movieList: LiveData<List<MovieDto>?> get() = _movieList
    private val _movieList = MutableLiveData<List<MovieDto>?>()

    val genreList: LiveData<List<String>> get() = _genreList
    private val _genreList = MutableLiveData<List<String>>()

    init {
        _genreList.value = repository.genreList
        refreshMovieList()
    }

    fun refreshMovieList() {

        viewModelScope.launch {

            changeStatus = true
            Log.i("MoviesViewModel", "Function called: changeStatus = $changeStatus")

            val (status, content) = repository.refreshMovies()

            when (status) {
                RefreshMovieStatus.OK -> {
                    _movieList.value = content
                    _refreshStatus.value = RefreshMovieStatus.OK
                }
                RefreshMovieStatus.FAILURE -> _refreshStatus.value = RefreshMovieStatus.FAILURE
                else -> _refreshStatus.value = RefreshMovieStatus.ERROR
            }
        }
    }
}