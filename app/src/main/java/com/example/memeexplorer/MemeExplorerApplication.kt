package com.example.memeexplorer

import android.app.Application
import com.example.memeexplorer.utilities.AdController
import com.kh69.logging.Logger
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MemeExplorerApplication : Application() {

    // initiate analytics, crashlytics, etc

    override fun onCreate() {
        super.onCreate()
        initLogger()
        AdController.initAd(this)

    }

    private fun initLogger() {
        Logger.init()
    }
}