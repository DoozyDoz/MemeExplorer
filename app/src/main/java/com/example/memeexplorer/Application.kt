package com.example.memeexplorer

import android.app.Application
import android.content.Context
import com.example.memeexplorer.utilities.AdController

class App : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: Application? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = applicationContext()
        AdController.initAd(this)
    }
}
