package com.example.memeexplorer.common.di

import com.example.memeexplorer.common.data.MemeLoaderMemeRepository
import com.example.memeexplorer.common.domain.repositories.MemeRepository
import com.example.memeexplorer.common.utils.CoroutineDispatchersProvider
import com.example.memeexplorer.common.utils.DispatchersProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.disposables.CompositeDisposable

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityRetainedModule {

    @Binds
    @ActivityRetainedScoped
    abstract fun bindMemeRepository(repository: MemeLoaderMemeRepository): MemeRepository

    @Binds
    abstract fun bindDispatchersProvider(dispatchersProvider: CoroutineDispatchersProvider):
            DispatchersProvider

    companion object {
        @Provides
        fun provideCompositeDisposable() = CompositeDisposable()
    }
}