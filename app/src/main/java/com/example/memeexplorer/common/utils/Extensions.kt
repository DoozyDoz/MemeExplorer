package com.example.memeexplorer.common.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.memeexplorer.R
import com.example.memeexplorer.common.domain.model.meme.Meme
import com.kh69.logging.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*


fun ImageView.setImage(url: String) {
    Glide.with(this.context)
        .load(url.ifEmpty { null })
        .error(R.drawable.pepe_placeholder)
        .centerCrop()
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun String.toMeme() = Meme(UUID.randomUUID(),this)

inline fun CoroutineScope.createExceptionHandler(
    message: String,
    crossinline action: (throwable: Throwable) -> Unit
) = CoroutineExceptionHandler { _, throwable ->
    Logger.e(throwable, message)
    throwable.printStackTrace()

    /**
     * A [CoroutineExceptionHandler] can be called from any thread. So, if [action] is supposed to
     * run in the main thread, you need to be careful and call this function on the a scope that
     * runs in the main thread, such as a [viewModelScope].
     */

    /**
     * A [CoroutineExceptionHandler] can be called from any thread. So, if [action] is supposed to
     * run in the main thread, you need to be careful and call this function on the a scope that
     * runs in the main thread, such as a [viewModelScope].
     */
    launch {
        action(throwable)
    }
}

inline fun <reified T> toArray(list: List<*>): Array<T> {
    return (list as List<T>).toTypedArray()
}