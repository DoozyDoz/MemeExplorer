package com.example.memeexplorer.common.di

import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import com.example.memeexplorer.common.domain.repositories.MemeRepository
import com.example.memeexplorer.common.utils.workers.OCRWorker
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Singleton
import kotlin.reflect.KClass


@Module
@InstallIn(SingletonComponent::class)
class DataModule {
//    @Provides
//    @Singleton
//    fun provideMemeRepository(cache: Cache): MemeRepository = MemeLoaderMemeRepository(cache)

    @Provides
    @Singleton
    fun workerFactory(memeRepository: MemeRepository): WorkerFactory {
        return WorkManagerFactory(memeRepository)
    }
}

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

@Module
@InstallIn(SingletonComponent::class)
interface WorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(OCRWorker::class)
    fun bindHelloWorldWorker(factory: OCRWorker.Factory): ChildWorkerFactory
}
