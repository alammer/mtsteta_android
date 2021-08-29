package ru.mtsteta.flixnet.movies

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mtsteta.flixnet.database.MovieLocal
import ru.mtsteta.flixnet.repo.ActorDto
import ru.mtsteta.flixnet.repo.MovieDto
import ru.mtsteta.flixnet.repo.MovieRepository
import ru.mtsteta.flixnet.repo.RefreshDataStatus

class MovieListViewModel : ViewModel() {

    val state: StateFlow<UiState>

    val accept: (UiAction) -> Unit

    private var genres: MutableMap<Int, String>? = null

    private val repository = MovieRepository()

    val genreList: LiveData<Map<Int, String>> get() = _genreList
    private val _genreList = MutableLiveData<Map<Int, String>>()

    init {
        fetchInitialData()

        val initialQuery: String = DEFAULT_QUERY
        val lastQueryScrolled: String = DEFAULT_QUERY
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(query = initialQuery)) }
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }

        state = searches
            .flatMapLatest { search ->
                combine(
                    queriesScrolled,
                    searchRepo(queryString = search.query),
                    ::Pair
                )
                    // Each unique PagingData should be submitted once, take the latest from
                    // queriesScrolled
                    .distinctUntilChangedBy { it.second }
                    .map { (scroll, pagingData) ->
                        UiState(
                            query = search.query,
                            pagingData = pagingData,
                            lastQueryScrolled = scroll.currentQuery,
                            // If the search query matches the scroll query, the user has scrolled
                            hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
                        )
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }

    }
    override fun onCleared() {
        super.onCleared()
    }

    private fun searchRepo(queryString: String): Flow<PagingData<MovieLocal>> =
        repository.moviePagesFlowDb()
            .cachedIn(viewModelScope)

    private fun fetchInitialData() {
        viewModelScope.launch {
            repository.loadGenres()?.also {
                genres = HashMap(it)
                _genreList.postValue(it)}
        }
    }

    //fun fetchData(page: Int = 1, language: String = "ru-RU", region: String = "RU") {
    //    viewModelScope.launch {
    //        val (status, content) = repository.updateMoviesList(loadpages, language, region)
    //    }
    //}

    @ExperimentalPagingApi
    fun fetchMovieFlow(): Flow<PagingData<MovieLocal>> {
        return repository.moviePagesFlowDb()
            .cachedIn(viewModelScope)
    }
}

sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(val currentQuery: String) : UiAction()
}

data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false,
    val pagingData: PagingData<MovieLocal> = PagingData.empty()
)


private const val DEFAULT_QUERY = "Android"