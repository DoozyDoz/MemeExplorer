package com.example.memeexplorer.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memeexplorer.R
import com.example.memeexplorer.extensions.openActivity
import kotlinx.android.synthetic.main.activity_dashboard.*


class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        et_search.setOnClickListener {
            openActivity(DebugActivity::class.java)
        }
    }
}