package ru.mtsteta.flixnet

import android.app.Application
import android.content.Context
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.mtsteta.flixnet.workmanager.RefreshMovieListWorker
import java.util.concurrent.TimeUnit

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        delayedInit()
    }

    companion object {
        lateinit var appContext: Context
    }

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    private fun delayedInit() {
        applicationScope.launch {
            setupRecuringWork()
        }
    }

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresCharging(true)
        .setRequiresBatteryNotLow(true)
        .setRequiresDeviceIdle(true)
        .build()

    private fun setupRecuringWork() {
        val repeatingRequest =
            PeriodicWorkRequestBuilder<RefreshMovieListWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshMovieListWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}