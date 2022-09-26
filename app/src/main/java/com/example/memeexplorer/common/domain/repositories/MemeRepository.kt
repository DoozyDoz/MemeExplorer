package com.example.memeexplorer.common.domain.repositories

import com.example.memeexplorer.common.domain.model.meme.Meme
import com.example.memeexplorer.common.domain.model.pagination.PaginatedMemes
import com.example.memeexplorer.search.domain.model.SearchParameters
import com.example.memeexplorer.search.domain.model.SearchResults
import io.reactivex.Flowable

interface MemeRepository {
    fun getMemes(): Flowable<List<Meme>>
    suspend fun requestMoreMemes(pageToLoad: Int, numberOfItems: Int): PaginatedMemes
    suspend fun storeMemes(memes: List<Meme>)
    suspend fun deleteMemes(memes: List<Meme>)
    fun searchCachedMemesBy(searchParameters: SearchParameters): Flowable<SearchResults>
}