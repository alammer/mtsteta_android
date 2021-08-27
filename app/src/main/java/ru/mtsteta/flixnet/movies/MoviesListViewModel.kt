package ru.mtsteta.flixnet.movies

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import ru.mtsteta.flixnet.database.MovieLocal
import ru.mtsteta.flixnet.repo.ActorDto
import ru.mtsteta.flixnet.repo.MovieDto
import ru.mtsteta.flixnet.repo.MovieRepository
import ru.mtsteta.flixnet.repo.RefreshDataStatus

class MoviesListViewModel : ViewModel() {

    var changeStatus = false

    private var loadpages = 1

    private var recentList: MutableList<MovieDto> = mutableListOf<MovieDto>()

    private var genres: MutableMap<Int, String>? = null

    private val repository = MovieRepository()

    val refreshStatus: LiveData<RefreshDataStatus> get() = _refreshStatus
    private val _refreshStatus = MutableLiveData<RefreshDataStatus>()

    val movieList: LiveData<List<MovieDto>?> get() = _movieList
    private val _movieList = MutableLiveData<List<MovieDto>?>()

    val actorList: LiveData<List<ActorDto>?> get() = _actorList
    private val _actorList = MutableLiveData<List<ActorDto>?>()

    val genreList: LiveData<Map<Int, String>> get() = _genreList
    private val _genreList = MutableLiveData<Map<Int, String>>()

    init {
        fetchInitialData()
    }

    private fun fetchInitialData() {
        viewModelScope.launch {
            //repository.loadActorList()?.also { _actorList.value = it }
            repository.loadGenres()?.also {
                genres = HashMap(it)
                _genreList.postValue(it)}
            repository.loadMovieList()?.also { _movieList.postValue(it) }
        }
        fetchData()
    }

    fun fetchData(page: Int = 1, language: String = "ru-RU", region: String = "RU") {

        viewModelScope.launch {

            changeStatus = true

            if (page == 0) loadpages = 1

            Log.i("Fetch_fetchData", "page: $page")

            val (status, content) = repository.updateMoviesList(loadpages, language, region)

            when (status) {
                RefreshDataStatus.OK -> {
                    loadpages ++
                    Log.i("Fetch_ViewModel", "recent: ${recentList.toString()}")
                    _movieList.value = content
                    _refreshStatus.value = RefreshDataStatus.OK
                }
                RefreshDataStatus.FAILURE -> _refreshStatus.value = RefreshDataStatus.FAILURE
                else -> _refreshStatus.value = RefreshDataStatus.ERROR
            }
        }
    }

    @ExperimentalPagingApi
    fun fetchMovieFlow(): Flow<PagingData<MovieLocal>> {
        return repository.MoviePagesFlowDb()
            .cachedIn(viewModelScope)
    }
}