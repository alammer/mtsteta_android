package ru.mtsteta.flixnet.repo

import androidx.annotation.Keep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import ru.mtsteta.flixnet.database.Genre
import ru.mtsteta.flixnet.database.MovieDataBase
import ru.mtsteta.flixnet.database.MovieDataDao
import ru.mtsteta.flixnet.database.asDomainModel

@Keep
enum class RefreshDataStatus { FAILURE, ERROR, OK }

class MovieRepository() {

    private val fakeDataSource = MoviesDataSourceImpl()

    private val dataDao = MovieDataBase.instance.movieDataDao

    suspend fun loadActorList(): List<ActorDto>? = withContext(Dispatchers.IO) {
        var localActors = dataDao.getAllActors()?.map { it.asDomainModel() }
        if (localActors.isNullOrEmpty()) localActors =
            fakeDataSource.getActors()?.also { domainModelList ->
                dataDao.insertAllActors(domainModelList.map { it.asDataBaseModel() })
            }
        localActors
    }

    suspend fun loadGenres(): List<String> = withContext(Dispatchers.IO) {
        var localGenres = dataDao.getGenres()
        if (localGenres.isNullOrEmpty()) localGenres = fakeDataSource.genreList.also { genres ->
            dataDao.insertAllGenres(genres.map { Genre(it) })
        }
        localGenres
    }

    fun loadMovieList(): List<MovieDto>? {
        var localMovies = dataDao.getAllMovies()?.map { it.asDomainModel() }
        if (localMovies.isNullOrEmpty()) localMovies =
            fakeDataSource.getMovies()?.also { domainModelList ->
                dataDao.insertAllMovies(domainModelList.map { it.asDataBaseModel() })
            }
        return localMovies
    }

    suspend fun refreshMovie(): Pair<RefreshDataStatus, List<MovieDto>?> =
        withContext((Dispatchers.IO)) {

            loadMovieList()

            val recentMovieList = dataDao.getAllMovies()?.map { it.asDomainModel() }

            if (recentMovieList?.any { it.title.isNullOrEmpty() } == true) {
                RefreshDataStatus.ERROR to null
            } else {
                recentMovieList?.let {
                    RefreshDataStatus.OK to it
                } ?: RefreshDataStatus.FAILURE to null
            }
        }
}