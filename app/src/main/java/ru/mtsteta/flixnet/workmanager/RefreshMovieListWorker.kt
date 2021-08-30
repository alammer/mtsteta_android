package ru.mtsteta.flixnet.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.mtsteta.flixnet.repo.MovieRepository

class RefreshMovieListWorker(
    appContex: Context,
    params: WorkerParameters
) : CoroutineWorker(appContex, params) {

    companion object {
        const val WORK_NAME = "RefreshMovieListWorker"
    }

    override suspend fun doWork(): Result {

        val repository = MovieRepository()

        return try {
            repository.refreshMovieList()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}