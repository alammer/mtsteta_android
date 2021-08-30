package ru.mtsteta.flixnet.repo

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.mtsteta.flixnet.BuildConfig
import ru.mtsteta.flixnet.database.MovieDataBase
import ru.mtsteta.flixnet.database.MovieDataDao
import ru.mtsteta.flixnet.database.MovieLocal
import ru.mtsteta.flixnet.database.PagingKeys
import ru.mtsteta.flixnet.network.MovieNetworkAPI
import ru.mtsteta.flixnet.network.toDataBaseModel
import java.io.IOException
import java.io.InvalidObjectException

private const val TMDB_STARTING_PAGE_INDEX = 1



@OptIn(ExperimentalPagingApi::class)
class MoviePagedMediator(private val networkAPI: MovieNetworkAPI, private val dataBase: MovieDataBase) :
    RemoteMediator<Int, MovieLocal>() {

    private val dataDao = dataBase.movieDataDao
    private val keysDao = dataBase.pagingKeysDao

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, MovieLocal>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: TMDB_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val reqParams: MutableMap<String, String> = HashMap()
            reqParams["api_key"] = BuildConfig.TMDB_API_KEY
            reqParams["page"] = page.toString()
            reqParams["language"] = "ru-RU"
            reqParams["region"] = "RU"

            val response = networkAPI.getPopList(reqParams)
            val movies = response.responseMoviesList
            val isEndOfList = movies.isEmpty()
            dataBase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    keysDao.clearPagingKeys()
                    dataDao.clearMovies()
                }
                val prevKey = if (page == TMDB_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = movies.map {
                    PagingKeys(movieId = it.movieId, prevKey = prevKey, nextKey = nextKey)
                }
                keys.let { keysDao.insertAll(keys) }

                fetchAgeLimit(movies.map { it.toDataBaseModel() }, "RU")
                    .also { dataDao.insertAllMovies(it) }

            }
            return MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun fetchAgeLimit(
        movieList: List<MovieLocal>,
        region: String
    ): List<MovieLocal> {
        movieList.forEach { movieLocal ->
            try {
                val response = networkAPI.getMovieDistributionInfo(
                    movieLocal.movieId,
                    BuildConfig.TMDB_API_KEY
                )

                if (response.isSuccessful) {
                    movieLocal.ageLimit =
                        response.body()?.results?.firstOrNull { it.countryCode == region }?.releaseDates?.firstOrNull { it.type > 2 }?.ageLimit
                } else {
                    response.errorBody()?.let {
                        Log.i("fetchAgeLimits", "errorBody: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.i("fetchAgeLimits", "Exception -  ${e.message}")
            }
        }
        return movieList
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MovieLocal>): PagingKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { movie ->
                keysDao.remoteKeysMovieId(movie.movieId)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MovieLocal>): PagingKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { movie ->
                keysDao.remoteKeysMovieId(movie.movieId)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MovieLocal>
    ): PagingKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.movieId?.let { movieId ->
                keysDao.remoteKeysMovieId(movieId)
            }
        }
    }
}