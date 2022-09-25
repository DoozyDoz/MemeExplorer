package com.example.memeexplorer.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memeexplorer.extensions.openActivity

class SplashActivityKT : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        openActivity(DebugActivity::class.java)
        finish()
    }
}