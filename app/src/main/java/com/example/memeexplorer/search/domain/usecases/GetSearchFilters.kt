package com.example.memeexplorer.search.domain.usecases

import com.example.memeexplorer.common.domain.repositories.MemeRepository
import com.example.memeexplorer.common.utils.DispatchersProvider
import com.example.memeexplorer.search.domain.model.SearchFilters
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class GetSearchFilters @Inject constructor(
    private val memeRepository: MemeRepository,
    private val dispatchersProvider: DispatchersProvider
) {

    companion object {
        const val NO_FILTER_SELECTED = "Any"
    }

    suspend operator fun invoke(): SearchFilters {
        return withContext(dispatchersProvider.io()) {
            val unknown = "UNKNOWN"
            val ids = listOf(NO_FILTER_SELECTED) //+ memeRepository.getAnimalTypes()

            val paths = listOf(NO_FILTER_SELECTED)


            return@withContext SearchFilters(ids, paths)
        }
    }
}