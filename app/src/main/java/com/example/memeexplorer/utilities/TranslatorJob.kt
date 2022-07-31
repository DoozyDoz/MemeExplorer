package com.example.memeexplorer.utilities

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.os.ResultReceiver
import android.util.Log
import com.example.memeexplorer.activities.DebugActivity
import com.example.memeexplorer.activities.DebugActivity.Companion.convertPathToBitmap
import com.google.android.gms.vision.Frame
import java.io.File
import java.net.URLConnection

class TranslatorJob(name: String?) : IntentService(name) {
    private val TAG = "TranslatorService"
    var progress = 0

    companion object{
        fun newIntent(context: Context?): Intent? {
            return Intent(context, TranslatorJob::class.java)
        }
    }



    @SuppressLint("RestrictedApi")
    override fun onHandleIntent(intent: Intent?) {
        val receiver = intent!!.getParcelableExtra<ResultReceiver>("receiver")
        val pathsArray = ArrayListSaverInterface(applicationContext).pathsArray
        if (pathsArray != null) {
            Log.d(TAG, "onHandleIntent: " + pathsArray[0])
            for (i in pathsArray.indices) {
                progress = i * 100 / pathsArray.size
                val staf = Bundle()
                staf.putInt("progress", progress)
                receiver!!.send(Constants.NEW_PROGRESS, staf)
                Log.i("wired", pathsArray.size.toString())
                val filepath = pathsArray[i]
                var outputFrame: Frame?
                val file = File(filepath)
                if (file.exists() && isImageFile(filepath)) {
                    val btm = convertPathToBitmap(filepath)
                    if (btm != null) {
                        outputFrame = Frame.Builder().setBitmap(btm).build()
                        DebugActivity().detectText(applicationContext, outputFrame, filepath)
                    }
                }
            }
        }
    }

    private fun isImageFile(path: String?): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.startsWith("image")
    }

}