package com.example.memeexplorer.search.domain.usecases

import com.example.memeexplorer.common.domain.model.NoMoreMemesException
import com.example.memeexplorer.common.domain.model.pagination.Pagination
import com.example.memeexplorer.common.domain.repositories.MemeRepository
import com.example.memeexplorer.common.utils.DispatchersProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject


class RequestNextPageOfMemes @Inject constructor(
    private val memeRepository: MemeRepository,
    private val dispatchersProvider: DispatchersProvider
) {
    suspend operator fun invoke(
        pageToLoad: Int,
        pageSize: Int = Pagination.DEFAULT_PAGE_SIZE
    ): Pagination {
        return withContext(dispatchersProvider.io()) {
            val (memes, pagination) = memeRepository.requestMoreMemes(pageToLoad, pageSize)

            if (memes.isEmpty()) {
                throw NoMoreMemesException("No memes available :(")
            }

            memeRepository.storeMemes(memes)

            return@withContext pagination
        }
    }
}