package ru.mtsteta.flixnet.repo

import android.util.Log
import androidx.annotation.Keep
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.mtsteta.flixnet.BuildConfig
import ru.mtsteta.flixnet.database.MovieDataBase
import ru.mtsteta.flixnet.database.MovieLocal
import ru.mtsteta.flixnet.database.toDomainModel
import ru.mtsteta.flixnet.network.MovieRemoteService
import ru.mtsteta.flixnet.network.toDataBaseModel

@Keep
enum class RefreshDataStatus { FAILURE, ERROR, OK }

class MovieRepository {

    val networkAPI = MovieRemoteService.retrofitService

    val dataDao = MovieDataBase.instance.movieDataDao

    /*suspend fun loadActorList(): List<ActorDto>? = withContext(Dispatchers.IO) {
        var localActors = dataDao.getAllActors()?.map { it.toDomainModel() }
        if (localActors.isNullOrEmpty()) {
            localActors = fakeDataSource.getActors()?.also { domainModelList ->
                    dataDao.insertAllActors(domainModelList.map { it.toDataBaseModel() })
                }
        }
        localActors
    }*/

    suspend fun loadGenres(): Map<Int, String>? = withContext(Dispatchers.IO) {
        var localGenres = dataDao.getGenres()
        if (localGenres.isNullOrEmpty()) {
            fetchGenres()
            localGenres = dataDao.getGenres()
        }
        localGenres?.map { it.genreId to it.genre }?.toMap()
    }

    private suspend fun fetchGenres() {
        try {
            val responseGenres = networkAPI.getGenres(BuildConfig.TMDB_API_KEY)

            if (responseGenres.isSuccessful) {
                responseGenres.body()?.genreResponseList?.map { it.toDataBaseModel() }?.let {
                    dataDao.insertAllGenres(it)
                }
            } else {
                responseGenres.errorBody()?.let {
                    Log.i("fetchGenres", "errorBody: ${responseGenres.code()}")
                }
            }
        } catch (e: Exception) {
            Log.i("fetchGenres", "Exception -  ${e.message}")
        }
    }

    suspend fun updateMoviesList(
        page: Int = 1,
        language: String = "ru-RU",
        region: String = "RU"
    ): Pair<RefreshDataStatus, List<MovieDto>?> =
        withContext((Dispatchers.IO)) {

            Log.i("Fetch_UpdateMoviesList", "page: $page")

            fetchMovies(page, language, region)

            val recentMovieList = dataDao.getAllMovies()?.map { it.toDomainModel() }

            if (recentMovieList?.any { it.title.isNullOrEmpty() } == true) {
                RefreshDataStatus.ERROR to null
            } else {
                recentMovieList?.let {
                    RefreshDataStatus.OK to it
                } ?: RefreshDataStatus.FAILURE to null
            }
        }

    private suspend fun fetchMovies(
        page: Int = 1,
        language: String = "ru-RU",
        region: String = "RU"
    ) {
        val params: MutableMap<String, String> = HashMap()
        params["api_key"] = BuildConfig.TMDB_API_KEY
        params["page"] = page.toString()
        params["language"] = language
        params["region"] = region
        try {
            val responseMovies = networkAPI.getPopMovieList(params)

            if (responseMovies.isSuccessful) {
                responseMovies.body()?.responseMoviesList
                    ?.map { it.toDataBaseModel() }
                    ?.let { fetchAgeLimit(it, region) }
                    ?.let {
                        Log.i("Fetch_fetchMovies", "page: $page")
                        if (page == 1) dataDao.clearMovies()
                        dataDao.insertAllMovies(it)
                    }
            } else {
                responseMovies.errorBody()?.let {
                    Log.i("fetchMovies", "errorBody: ${responseMovies.code()}")
                }
            }
        } catch (e: Exception) {
            Log.i("fetchMovies", "Exception -  ${e.message}")
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

    suspend fun loadMovieList(
        page: Int = 1,
        language: String = "ru-RU",
        region: String = "RU"
    ): List<MovieDto>? = withContext(Dispatchers.IO) {
        var localMovies = dataDao.getAllMovies()?.map { it.toDomainModel() }
        Log.i("Fetch first", "Function called: loadMovieList()")
        Log.i("Fetch_loadMovieList", "empty DB return: $localMovies")
        if (localMovies.isNullOrEmpty()) {
            fetchMovies(page, language, region)
            localMovies = dataDao.getAllMovies()?.map { it.toDomainModel() }
        }
        localMovies
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 20,
            enablePlaceholders = false,
            initialLoadSize = 40)
    }

    fun moviePagesFlowDb(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<MovieLocal>> {
        val pagingSourceFactory = { dataDao.getMovies() }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = MoviePagedMediator(networkAPI, MovieDataBase.instance)
        ).flow
    }
}



