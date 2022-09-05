package com.example.memeexplorer.common.data.di

import android.content.Context
import androidx.room.Room
import com.example.memeexplorer.common.data.cache.Cache
import com.example.memeexplorer.common.data.cache.MemeExplorerDatabase
import com.example.memeexplorer.common.data.cache.RoomCache
import com.example.memeexplorer.common.data.cache.daos.MemesDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CacheModule {

    @Binds
    abstract fun bindCache(cache: RoomCache): Cache

    companion object {

        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context
        ): MemeExplorerDatabase {
            return Room.databaseBuilder(
                context,
                MemeExplorerDatabase::class.java,
                "memeExplorer.db"
            )
                .build()
        }

        @Provides
        fun provideMemesDao(
            memeExplorerDatabase: MemeExplorerDatabase
        ): MemesDao = memeExplorerDatabase.memesDao()


    }
}