package com.example.memeexplorer.common.utils.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.memeexplorer.MemeExplorerApplication
import com.example.memeexplorer.common.utils.tess.TessDataManager
import com.googlecode.tesseract.android.TessBaseAPI
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


private const val TAG = "OCRWorker"

class OCRWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return try {
            val paths = inputData.getStringArray(WorkerConstants.KEY_IMAGE_DB_PATHS)!!.toList()
            val resultsMap = mutableMapOf<String, String>()

            withContext(Dispatchers.Default) {
                for (path in paths) {
                    launch {
                        resultsMap[path] = detectText(path).toString()
                    }
                }
            }

            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(
                MutableMap::class.java,
                String::class.java,
                String::class.java
            )
            val adapter: JsonAdapter<Map<String, String>> = moshi.adapter(type)
            val mapString = adapter.toJson(resultsMap)

            val outputData = workDataOf(WorkerConstants.KEY_OCR_RESULT_MAP to mapString)
            Result.success(outputData)

        } catch (throwable: Throwable) {
            Log.e(TAG, "Error translating image files")
            throwable.printStackTrace()
            Result.failure()
        }
    }

    private fun detectText(imgPath: String?): String? {
        Log.d(TAG, "Initialization of TessBaseApi")
        TessDataManager.initTessTrainedData(MemeExplorerApplication.sAppContext)
        val tessBaseAPI = TessBaseAPI()
        val path = TessDataManager.getTesseractFolder()
        Log.d(TAG, "Tess folder: $path")
        tessBaseAPI.setDebug(true)
        tessBaseAPI.init(path, "eng")
        // 白名单
        tessBaseAPI.setVariable(
            TessBaseAPI.VAR_CHAR_WHITELIST,
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        )
        // 黑名单
        tessBaseAPI.setVariable(
            TessBaseAPI.VAR_CHAR_BLACKLIST,
            "!@#$%^&*()_+=-[]}{;:'\"\\|~`,./<>?"
        )
        //        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO); // only one
        tessBaseAPI.pageSegMode = TessBaseAPI.PageSegMode.PSM_AUTO_OSD // only one
        Log.d(TAG, "Ended initialization of TessEngine")
        Log.d(TAG, "Running inspection on bitmap")
        //        tessBaseAPI.setImage(bitmap);
        tessBaseAPI.setImage(File(imgPath))
        val inspection = tessBaseAPI.utF8Text
        Log.d(TAG, "Confidence values: " + tessBaseAPI.meanConfidence())
        Log.d(TAG, "text_0cr: $inspection")
        tessBaseAPI.recycle()
        System.gc()
        return inspection
    }
}