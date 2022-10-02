package com.example.memeexplorer.common.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters


interface ChildWorkerFactory {
    fun create(appContext: Context, params: WorkerParameters): ListenableWorker
}