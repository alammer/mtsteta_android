package ru.mtsteta.flixnet.movies

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.database.toDomainModel
import ru.mtsteta.flixnet.genres.GenreClickListener
import ru.mtsteta.flixnet.genres.GenreListAdapter
import ru.mtsteta.flixnet.repo.MovieDto

class MoviesListFragment : Fragment() {

    private val moviesListViewModel: MovieListViewModel by viewModels()

    private lateinit var genreRecycler: RecyclerView
    private lateinit var movieRecycler: RecyclerView
    private lateinit var swipeRefresher: SwipeRefreshLayout
    private lateinit var genreAdapter: GenreListAdapter
    private lateinit var movieAdapter: MovieListAdapter

    private lateinit var btnUpdate: MaterialButton
    //private lateinit var pbLoadList: ProgressBar
    private lateinit var emptyList: TextView


    private var isLoading: Boolean = false
    private var visibleThreshold = 1
    private var lastVisibleItem = 0
    private var totalItemCount = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnUpdate = view.findViewById(R.id.btnUpdate)
        //pbLoadList = view.findViewById(R.id.pbListLoad)
        emptyList = view.findViewById(R.id.tvEmpty)

        initViews(view, moviesListViewModel)

        moviesListViewModel.genreList.observe(viewLifecycleOwner, {
            genreAdapter.submitList(it.values.toList())
        })
    }

    private fun initViews(view: View, viewModel: MovieListViewModel) {

        genreRecycler = view.findViewById(R.id.rvGenreList)
        movieRecycler = view.findViewById(R.id.rvMovieList)
        swipeRefresher = view.findViewById(R.id.swipeLayout)

        genreAdapter = GenreListAdapter(GenreClickListener {
            //TODO("We should implement logic for GenreClickListener later")
        })

        genreAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        genreRecycler.adapter = genreAdapter

        movieAdapter = MovieListAdapter(MovieClickListener { movieItem: MovieDto ->
            val direction = MoviesListFragmentDirections.actionMoviesToDetail(movieItem)
            findNavController().navigate(direction)
        })

        val header = MovieLoadingAdapter { movieAdapter.retry() }
        val footer = MovieLoadingAdapter { movieAdapter.retry() }

        //this cause initial blink viewholders
        movieRecycler.adapter = movieAdapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = footer
        )

        movieRecycler.layoutManager =
            when (resources.configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT ->
                    GridLayoutManager(view.context, GREED_SPAN_COUNT).apply {
                        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return if ((position == movieAdapter.itemCount) && footer.itemCount > 0) {
                                    spanCount
                                } else if (movieAdapter.itemCount == 0 && header.itemCount > 0) {
                                    spanCount
                                } else {
                                    1
                                }
                            }
                        }
                    }
                else ->
                    LinearLayoutManager(view.context).apply { orientation = LinearLayoutManager.HORIZONTAL }
            }

        movieRecycler.addItemDecoration(MovieSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.movieslist_rv_top_spacing)))

        initAdapterStateFlow(
            header = header,
            movieAdapter = movieAdapter,
            uiState = viewModel.state,
            onScrollChanged = viewModel.accept
        )


        swipeRefresher.setOnRefreshListener {
            movieAdapter.refresh() }
    }

    private fun initAdapterStateFlow(
        header: MovieLoadingAdapter,
        movieAdapter: MovieListAdapter,
        uiState: StateFlow<UiState>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ){
        btnUpdate.setOnClickListener { movieAdapter.retry() }
        movieRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = movieAdapter.loadStateFlow
            // Only emit when REFRESH LoadState for RemoteMediator changes.
            .distinctUntilChangedBy { it.refresh }
            // Only react to cases where Remote REFRESH completes i.e., NotLoading.
            .map { it.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        val pagingData = uiState
            .map { it.pagingData }
            .distinctUntilChanged()

        lifecycleScope.launch {
            combine(shouldScrollToTop, pagingData, ::Pair)
                .distinctUntilChangedBy { it.second }
                .collectLatest { (shouldScroll, pagingData) ->
                    movieAdapter.submitData(pagingData.map { it.toDomainModel() })
                    if (shouldScroll) movieRecycler.scrollToPosition(0)
                }
        }

        lifecycleScope.launch {
            movieAdapter.loadStateFlow.collect { loadState ->
                // Show a retry header if there was an error refreshing, and items were previously
                // cached OR default to the default prepend state
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && movieAdapter.itemCount > 0 }
                    ?: loadState.prepend

                val isListEmpty = loadState.refresh is LoadState.NotLoading && movieAdapter.itemCount == 0

                emptyList.isVisible = isListEmpty

                movieRecycler.isVisible =  loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading

                swipeRefresher.isRefreshing = loadState.mediator?.refresh is LoadState.Loading

                btnUpdate.isVisible = loadState.mediator?.refresh is LoadState.Error && movieAdapter.itemCount == 0

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        context,
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}

private const val GREED_SPAN_COUNT = 2

private fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val activeNetwork =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}