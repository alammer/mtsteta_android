package ru.mtsteta.flixnet.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mtsteta.flixnet.database.MovieLocal
import ru.mtsteta.flixnet.repo.MovieRepository

@ExperimentalCoroutinesApi
class MovieListViewModel : ViewModel() {

    val state: StateFlow<UiState>

    val accept: (UiAction) -> Unit

    private var genres: MutableMap<Int, String>? = null

    private val repository = MovieRepository()

    val genreList: LiveData<Map<Int, String>> get() = _genreList
    private val _genreList = MutableLiveData<Map<Int, String>>()

    init {
        fetchGenres()


        val initialQuery: String = DEFAULT_QUERY
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val fetches = actionStateFlow
            .filterIsInstance<UiAction.Fetch>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Fetch(query = initialQuery)) }
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
            .onStart { emit(UiAction.Scroll(currentQuery = initialQuery)) }

        state = fetches
            .flatMapLatest { fetch ->
                combine(
                    queriesScrolled,
                    fetchMovieFlow(queryString = fetch.query),
                    ::Pair
                )
                    // Each unique PagingData should be submitted once, take the latest from
                    // queriesScrolled
                    .distinctUntilChangedBy { it.second }
                    .map { (_, pagingData) ->
                        UiState(
                            query = fetch.query,
                            pagingData = pagingData
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

    private fun fetchMovieFlow(queryString: String): Flow<PagingData<MovieLocal>> =
        repository.moviePagesFlowDb()
            .cachedIn(viewModelScope)

    private fun fetchGenres() {
        viewModelScope.launch {
            repository.loadGenres()?.also {
                genres = HashMap(it)
                _genreList.postValue(it)
            }
        }
    }
}

sealed class UiAction {
    data class Fetch(val query: String) : UiAction()
    data class Scroll(val currentQuery: String) : UiAction()
}

data class UiState(
    val query: String = DEFAULT_QUERY,
    val pagingData: PagingData<MovieLocal> = PagingData.empty()
)

private const val DEFAULT_QUERY = "Android"