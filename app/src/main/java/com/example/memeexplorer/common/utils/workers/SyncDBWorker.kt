package com.example.memeexplorer.common.utils.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.CodeBoy.MediaFacer.MediaFacer
import com.CodeBoy.MediaFacer.PictureGet

class SyncDBWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val appContext = applicationContext

        return try {
            val newPaths = inputData.getString(WorkerConstants.KEY_IMAGE_LOCAL_PATHS)
            val dbPaths = inputData.getString(WorkerConstants.KEY_IMAGE_DB_PATHS)

            val newImages = newPaths?.toSet()?.minus(dbPaths!!.toList().toSet())
            val deletedImages = dbPaths?.toSet()?.minus(newPaths!!.toList().toSet())


            val paths = MediaFacer.withPictureContex(appContext)
                .getAllPictureContents(PictureGet.externalContentUri)
                .map { it.picturePath } as ArrayList<String>
            val outputData = workDataOf(WorkerConstants.KEY_IMAGE_LOCAL_PATHS to paths)
            Result.success(outputData)
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error reading local image files")
            throwable.printStackTrace()
            Result.failure()
        }

    }
}