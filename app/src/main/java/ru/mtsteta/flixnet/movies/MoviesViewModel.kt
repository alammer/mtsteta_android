package ru.mtsteta.flixnet.movies

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.mtsteta.flixnet.repo.ActorDto
import ru.mtsteta.flixnet.repo.MovieDto
import ru.mtsteta.flixnet.repo.MovieRepository
import ru.mtsteta.flixnet.repo.RefreshDataStatus

class MoviesViewModel : ViewModel() {

    var changeStatus = false

    private val repository = MovieRepository()

    val refreshStatus: LiveData<RefreshDataStatus> get() = _refreshStatus
    private val _refreshStatus = MutableLiveData<RefreshDataStatus>()

    val movieList: LiveData<List<MovieDto>?> get() = _movieList
    private val _movieList = MutableLiveData<List<MovieDto>?>()

    val actorList: LiveData<List<ActorDto>?> get() = _actorList
    private val _actorList = MutableLiveData<List<ActorDto>?>()

    val genreList: LiveData<List<String>> get() = _genreList
    private val _genreList = MutableLiveData<List<String>>()

    init {
        fetchCommonData()
    }

    private fun fetchCommonData() {
        viewModelScope.launch {
            repository.loadActorList()?.also { _actorList.value = it }
            repository.loadGenres().also { _genreList.postValue(it) }
        }
        fetchData()
    }

    fun fetchData() {

        viewModelScope.launch {

            changeStatus = true
            Log.i("MoviesViewModel", "Function called: changeStatus = $changeStatus")

            repository.refreshMovie().collect {
                when (it.first) {
                    RefreshDataStatus.OK -> {
                        _movieList.value = it.second
                        _refreshStatus.value = RefreshDataStatus.OK
                    }
                    RefreshDataStatus.FAILURE -> _refreshStatus.value = RefreshDataStatus.FAILURE
                    else -> _refreshStatus.value = RefreshDataStatus.ERROR
                }
            }

        }
    }
}