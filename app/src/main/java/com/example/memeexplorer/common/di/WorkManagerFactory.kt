package com.example.memeexplorer.common.di

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.memeexplorer.common.domain.repositories.MemeRepository
import com.example.memeexplorer.common.utils.workers.OCRWorker


class WorkManagerFactory(private val memeRepository: MemeRepository) : WorkerFactory() {

    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {

        val workerKlass = Class.forName(workerClassName).asSubclass(CoroutineWorker::class.java)
        val constructor = workerKlass.getDeclaredConstructor(Context::class.java, WorkerParameters::class.java)
        val instance = constructor.newInstance(appContext, workerParameters)

        when (instance) {
            is OCRWorker -> {
                instance.memeRepository = memeRepository
            }
            // optionally, handle other workers
        }

        return instance
    }
}