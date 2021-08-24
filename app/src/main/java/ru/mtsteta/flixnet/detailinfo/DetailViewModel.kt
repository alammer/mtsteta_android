package ru.mtsteta.flixnet.detailinfo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mtsteta.flixnet.BuildConfig
import ru.mtsteta.flixnet.database.MovieDataBase
import ru.mtsteta.flixnet.network.MovieCrew
import ru.mtsteta.flixnet.network.MovieRemoteService
import ru.mtsteta.flixnet.network.toDataBaseModel
import ru.mtsteta.flixnet.repo.ActorDto

class DetailViewModel : ViewModel() {

    private val networkData = MovieRemoteService

    private val dataDao = MovieDataBase.instance.movieDataDao

    val actorList: LiveData<List<MovieCrew>?> get() = _actorList
    private val _actorList = MutableLiveData<List<MovieCrew>?>()

    fun fetchActors (movieId: Int) {
        viewModelScope.launch {
            try {
                val actorsResponce = networkData.retrofitService.getMovieCrew(movieId, BuildConfig.TMDB_API_KEY)
                if (actorsResponce.isSuccessful) {
                    actorsResponce.body()?.cast?.filter { it.department == "Acting" }?.let {
                        _actorList.value = it
                    }
                    Log.i("Success", "Function called: getStockList()")
                } else {
                    actorsResponce.errorBody()?.let {
                        Log.i("fetchGenres", "errorBody: ${actorsResponce.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.i("fetchGenres", "Exception -  ${e.message}")
            }
        }
    }

}