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


    private fun loadMovieList(): List<MovieDto>?  {
        var localMovies = dataDao.getAllMovies()?.map { it.asDomainModel() }
        if (localMovies.isNullOrEmpty()) localMovies = fakeDataSource.getMovies()?.also { domainModelList ->
            dataDao.insertAllMovies(domainModelList.map { it.asDataBaseModel() })
        }
        return localMovies
    }

    suspend fun loadActorList(): List<ActorDto>? = withContext(Dispatchers.IO) {
        var localActors = dataDao.getAllActors()?.map { it.asDomainModel() }
        if (localActors == null) localActors = fakeDataSource.getActors()?.also { domainModelList ->
            dataDao.insertAllActors(domainModelList.map { it.asDataBaseModel() })
        }
        localActors
    }

    suspend fun loadGenres(): List<String> = withContext(Dispatchers.IO) {
        var localGenres = dataDao.getGenres()
        if (localGenres == null) localGenres = fakeDataSource.genreList.also { genres ->
            dataDao.insertAllGenres(genres.map { Genre(it) })
        }
        localGenres
    }


    suspend fun refreshMovie(): Flow<Pair<RefreshDataStatus, List<MovieDto>?>> {

        return flow {

            loadMovieList()

            val recentMovieList = dataDao.getAllMovies()?.map { it.asDomainModel() }

            if (recentMovieList?.any { it.title.isNullOrBlank() } == true) {
                emit(RefreshDataStatus.ERROR to null)
            } else {
                recentMovieList?.let {
                    emit(RefreshDataStatus.OK to it)
                } ?: emit(RefreshDataStatus.FAILURE to null)
            }
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()
    }
}