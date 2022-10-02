package com.example.memeexplorer

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.example.memeexplorer.utilities.AdController
import com.kh69.logging.Logger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class MemeExplorerApplication : Application() {
    @Inject
    lateinit var workerFactory: WorkerFactory

    companion object {
        lateinit var sAppContext: Context
    }


    // initiate analytics, crashlytics, etc

    override fun onCreate() {
        super.onCreate()
        initLogger()
        AdController.initAd(this)
        sAppContext = this
        WorkManager.initialize(
            this,
            Configuration.Builder().setWorkerFactory(workerFactory).build()
        )


    }

    private fun initLogger() {
        Logger.init()
    }
}