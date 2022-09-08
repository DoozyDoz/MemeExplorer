package com.example.memeexplorer

import android.app.Application
import android.content.Context
import com.example.memeexplorer.utilities.AdController
import com.kh69.logging.Logger
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MemeExplorerApplication : Application() {

    companion object{
        lateinit var sAppContext: Context
    }


    // initiate analytics, crashlytics, etc

    override fun onCreate() {
        super.onCreate()
        initLogger()
        AdController.initAd(this)
        sAppContext = this


    }

    private fun initLogger() {
        Logger.init()
    }
}