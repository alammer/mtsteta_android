package ru.mtsteta.flixnet.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mtsteta.flixnet.repo.MovieDto
import ru.mtsteta.flixnet.repo.MovieRepository
import ru.mtsteta.flixnet.repo.RefreshMovieStatus

class MoviesViewModel : ViewModel() {

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

            val responce = repository.refreshMovies()

            when (responce.first) {
                RefreshMovieStatus.OK -> {
                    _movieList.value = responce.second
                    _refreshStatus.value = RefreshMovieStatus.OK
                }
                RefreshMovieStatus.FAILURE -> _refreshStatus.value = RefreshMovieStatus.FAILURE
                else -> _refreshStatus.value = RefreshMovieStatus.ERROR

            }
        }
    }
}