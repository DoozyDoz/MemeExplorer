package com.example.memeexplorer.common.domain.repositories

import android.app.appsearch.SearchResults
import com.example.memeexplorer.common.domain.model.meme.Meme
import com.example.memeexplorer.common.domain.model.pagination.PaginatedMemes
import com.example.memeexplorer.search.domain.model.SearchParameters
import io.reactivex.Flowable

interface MemeRepository {
    fun getMemes(): Flowable<List<Meme>>
    suspend fun requestMoreMemes(pageToLoad: Int, numberOfItems: Int): PaginatedMemes
    suspend fun storeMemes(memes: List<Meme>)
    fun searchCachedMemesBy(searchParameters: SearchParameters): Flowable<SearchResults>
}