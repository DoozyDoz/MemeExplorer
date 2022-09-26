package com.example.memeexplorer.search.domain.usecases

import com.example.memeexplorer.common.domain.repositories.MemeRepository
import com.example.memeexplorer.common.utils.DispatchersProvider
import com.example.memeexplorer.common.utils.toMeme
import kotlinx.coroutines.withContext
import javax.inject.Inject


class DeleteMemes @Inject constructor(
    private val memeRepository: MemeRepository,
    private val dispatchersProvider: DispatchersProvider
) {
    suspend operator fun invoke(
        imagePaths: List<String>
    ) {
        return withContext(dispatchersProvider.io()) {
            memeRepository(imagePaths.map { it.toMeme() })
        }
    }
}