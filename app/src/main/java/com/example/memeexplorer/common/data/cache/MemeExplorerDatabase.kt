package com.example.memeexplorer.common.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.memeexplorer.common.data.cache.daos.MemesDao
import com.example.memeexplorer.common.data.cache.model.CachedMeme

@Database(
    entities = [
        CachedMeme::class
    ],
    version = 1
)
abstract class MemeExplorerDatabase : RoomDatabase() {
    abstract fun memesDao(): MemesDao
}