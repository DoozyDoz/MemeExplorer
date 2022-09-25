package com.example.memeexplorer.extensions

import android.content.Context
import android.content.Intent

fun <T> Context.openActivity(it: Class<T>) {
    val intent = Intent(this, it)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}