package com.example.memeexplorer.common.data.cache

import com.example.memeexplorer.common.data.cache.daos.MemesDao
import com.example.memeexplorer.common.data.cache.model.CachedMeme
import io.reactivex.Flowable
import javax.inject.Inject

class RoomCache @Inject constructor(
    private val memesDao: MemesDao
) : Cache {

    override fun getMemes(): Flowable<List<CachedMeme>> {
        return memesDao.getAllMemes()
    }

    override suspend fun storeMemes(memes: List<CachedMeme>) {
        memesDao.insertMemes(memes)
    }

    override suspend fun updateMemes(memeMap: Map<String, String>) {
        memesDao.updateMemes(memeMap)
    }

    override suspend fun deleteMemes(memes: List<CachedMeme>) {
        memesDao.deleteMemes(memes)
    }

    override fun searchMemesBy(
        tag: String
    ): Flowable<List<CachedMeme>> {
        return memesDao.searchMemesBy(tag)
    }
}