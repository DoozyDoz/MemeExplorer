package com.example.memeexplorer.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.memeexplorer.R

fun ImageView.setImage(url: String) {
    Glide.with(this.context)
        .load(url.ifEmpty { null })
        .error(R.drawable.pepe_smiling)
        .centerCrop()
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}