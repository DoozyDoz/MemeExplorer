package com.example.memeexplorer.common.data.cache

import com.example.memeexplorer.common.data.cache.model.CachedMeme
import io.reactivex.Flowable

interface Cache {
    fun getMemes(): Flowable<List<CachedMeme>>
    suspend fun storeMemes(memes: List<CachedMeme>)
    suspend fun deleteMemes(memes: List<CachedMeme>)
    fun searchMemesBy(tag: String): Flowable<List<CachedMeme>>
}