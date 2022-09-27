package com.example.memeexplorer.common.utils.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.CodeBoy.MediaFacer.MediaFacer
import com.CodeBoy.MediaFacer.PictureGet
import com.example.memeexplorer.common.utils.workers.WorkerConstants.KEY_IMAGE_LOCAL_PATHS

private const val TAG = "BlurWorker"

class GetLocalImagesWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val appContext = applicationContext

        return try {
            val paths = MediaFacer.withPictureContex(appContext)
                .getAllPictureContents(PictureGet.externalContentUri)
                .map { it.picturePath } as ArrayList<String>
            val outputData = workDataOf(KEY_IMAGE_LOCAL_PATHS to paths)
            Result.success(outputData)
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error reading local image files")
            throwable.printStackTrace()
            Result.failure()
        }

    }
}