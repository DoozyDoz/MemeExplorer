package com.example.memeexplorer.common.utils.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.CodeBoy.MediaFacer.MediaFacer
import com.CodeBoy.MediaFacer.PictureGet
import com.example.memeexplorer.activities.MainActivity.detectText
import com.example.memeexplorer.common.domain.usecases.UpdateMemes
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SyncDBWorker @Inject constructor(
    ctx: Context, params: WorkerParameters,
    private val updateMemes: UpdateMemes
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val appContext = applicationContext

        return try {
            val ocrResultMapString = inputData.getString(WorkerConstants.KEY_OCR_RESULT_MAP)

            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(
                MutableMap::class.java,
                String::class.java,
                String::class.java
            )
            val adapter: JsonAdapter<Map<String, String>> = moshi.adapter(type)
            val ocrResultMap: Map<String, String>? = adapter.fromJson(ocrResultMapString!!)

            withContext(Dispatchers.IO) {
                updateMemes(ocrResultMap!!)
            }

            Result.success()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            Result.failure()
        }

    }
}