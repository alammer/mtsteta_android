package ru.mtsteta.flixnet.repo

import android.util.Log
import androidx.annotation.Keep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.mtsteta.flixnet.BuildConfig
import ru.mtsteta.flixnet.database.MovieDataBase
import ru.mtsteta.flixnet.database.toDomainModel
import ru.mtsteta.flixnet.network.MovieRemoteService
import ru.mtsteta.flixnet.network.toDataBaseModel

@Keep
enum class RefreshDataStatus { FAILURE, ERROR, OK }

class MovieRepository() {

    private val networkData = MovieRemoteService

    private val dataDao = MovieDataBase.instance.movieDataDao

    /*suspend fun loadActorList(): List<ActorDto>? = withContext(Dispatchers.IO) {
        var localActors = dataDao.getAllActors()?.map { it.toDomainModel() }
        if (localActors.isNullOrEmpty()) {
            localActors = fakeDataSource.getActors()?.also { domainModelList ->
                    dataDao.insertAllActors(domainModelList.map { it.toDataBaseModel() })
                }
        }
        localActors
    }*/

    private suspend fun fetchGenres() {
        try {
            val responseGenres = networkData.retrofitService.getGenres(BuildConfig.TMDB_API_KEY)

            if (responseGenres.isSuccessful) {
                responseGenres.body()?.genreResponseList?.map { it.toDataBaseModel() }?.let {
                    dataDao.insertAllGenres(it)
                }
                Log.i("Success", "Function called: getStockList()")
            } else {
                responseGenres.errorBody()?.let {
                    Log.i("fetchGenres", "errorBody: ${responseGenres.code()}")
                }
            }
        } catch (e: Exception) {
            Log.i("fetchGenres", "Exception -  ${e.message}")
        }
    }

    suspend fun loadGenres(): Map<Int, String>? = withContext(Dispatchers.IO) {
        var localGenres = dataDao.getGenres()
        if (localGenres.isNullOrEmpty()) {
            fetchGenres()
            localGenres = dataDao.getGenres()
        }
        localGenres?.map { it.genreId to it.genre }?.toMap()
    }

    private suspend fun fetchMovies(page: Int = 1, language: String = "ru-RU", region: String = "RU") {
        val params: MutableMap<String, String> = HashMap()
        params["api_key"] = BuildConfig.TMDB_API_KEY
        params["page"] = page.toString()
        params["language"] = language
        params["region"] = region
        try {
            val responseMovies = networkData.retrofitService.getPopMovieList(params)

            if (responseMovies.isSuccessful) {
                responseMovies.body()?.responseMoviesList?.map { it.toDataBaseModel() }?.let {
                    dataDao.insertAllMovies(it)
                }
                Log.i("fetchMovies", "Success: ${responseMovies.body()?.responseMoviesList.toString()}")
            } else {
                responseMovies.errorBody()?.let {
                    Log.i("fetchMovies", "errorBody: ${responseMovies.code()}")
                }
            }
        } catch (e: Exception) {
            Log.i("fetchMovies", "Exception -  ${e.message}")
        }
    }

    suspend fun loadMovieList(page: Int = 1, language: String = "ru-RU", region: String = "RU"): List<MovieDto>? = withContext(Dispatchers.IO){
        var localMovies = dataDao.getAllMovies()?.map { it.toDomainModel() }
        if (localMovies.isNullOrEmpty()) {
            fetchMovies(page, language, region)
            localMovies = dataDao.getAllMovies()?.map { it.toDomainModel() }
            }
        localMovies
    }

    suspend fun refreshMovie(page: Int = 1, language: String = "ru-RU", region: String = "RU"): Pair<RefreshDataStatus, List<MovieDto>?> =
        withContext((Dispatchers.IO)) {

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
}



