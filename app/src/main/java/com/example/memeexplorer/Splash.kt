package com.example.memeexplorer

import android.Manifest
import android.app.ActivityManager
import android.app.AppOpsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.memeexplorer.activities.DebugActivity
import java.util.*

class Splash : AppCompatActivity() {

    private val REQUEST_PERMISSIONS = 1234
    private val PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val MANAGE_EXTERNAL_STORAGE_PERMISSION = "android:manage_external_storage"

    private var progress_bar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        progress_bar = findViewById(R.id.progress_bar)
        startProgressBar()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {

//             If Android 11 Request for Manage File Access Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, REQUEST_PERMISSIONS)
                return
            }
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS)
        }
    }

    private fun startProgressBar() {
        var progress = progress_bar!!.progress
        progress = progress + 2
        if (progress > progress_bar!!.max) progress = 0
        progress_bar!!.progress = progress
        Handler().postDelayed({ startProgressBar() }, 50)
    }

    override fun onResume() {
        super.onResume()
        if (!arePermissionDenied()) {
            next()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    fun checkStorageApi30(): Boolean {
        val appOps = applicationContext.getSystemService(
            AppOpsManager::class.java
        )
        val mode = appOps.unsafeCheckOpNoThrow(
            MANAGE_EXTERNAL_STORAGE_PERMISSION,
            applicationContext.applicationInfo.uid,
            applicationContext.packageName
        )
        return mode != AppOpsManager.MODE_ALLOWED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS && grantResults.size > 0) {
            if (arePermissionDenied()) {
                // Clear Data of Application, So that it can request for permissions again
                (Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE)) as ActivityManager).clearApplicationUserData()
                recreate()
            } else {
                next()
            }
        }
    }

    private operator fun next() {
        // Show splash screen for 2 seconds
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                startActivity(Intent(this@Splash, DebugActivity::class.java))
                finish() // kill current activity
            }
        }
        Timer().schedule(task, 1000)
    }


    private fun arePermissionDenied(): Boolean {
        if (Build.VERSION.SDK_INT >= 30) {
            return checkStorageApi30()
        }
        for (permissions in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    permissions
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
        }
        return false
    }
}