package com.example.memeexplorer.common.data

import com.bumptech.glide.load.HttpException
import com.example.memeexplorer.common.data.cache.Cache
import com.example.memeexplorer.common.data.cache.model.CachedMeme.Companion.fromDomain
import com.example.memeexplorer.common.domain.model.NetworkException
import com.example.memeexplorer.common.domain.model.meme.Meme
import com.example.memeexplorer.common.domain.model.pagination.PaginatedMemes
import com.example.memeexplorer.common.domain.repositories.MemeRepository
import com.example.memeexplorer.search.domain.model.SearchParameters
import com.example.memeexplorer.search.domain.model.SearchResults
import io.reactivex.Flowable
import javax.inject.Inject


class MemeLoaderMemeRepository @Inject constructor(
    private val cache: Cache
) : MemeRepository {

    // fetch these from shared preferences, after storing them in onboarding screen
    private val postcode = "07097"
    private val maxDistanceMiles = 100

    override fun getMemes(): Flowable<List<Meme>> {
        return cache.getMemes()
            .distinctUntilChanged()
            .map { memeList ->
                memeList.map { it.toDomain() }
            }
    }

    override suspend fun requestMoreMemes(pageToLoad: Int, numberOfItems: Int): PaginatedMemes {
        try {
            val (apiAnimals, apiPagination) = api.getNearbyAnimals(
                pageToLoad,
                numberOfItems,
                postcode,
                maxDistanceMiles
            )

            return PaginatedMemes(
                apiAnimals?.map { apiAnimalMapper.mapToDomain(it) }.orEmpty(),
                apiPaginationMapper.mapToDomain(apiPagination)
            )
        } catch (exception: HttpException) {
            throw NetworkException(exception.message ?: "Code ${exception.code()}")
        }
    }

    override suspend fun storeMemes(memes: List<Meme>) {
        cache.storeMemes(memes.map { fromDomain(it) })
    }


    override fun searchCachedMemesBy(searchParameters: SearchParameters): Flowable<SearchResults> {
        val (tag) = searchParameters

        return cache.searchMemesBy(tag)
            .distinctUntilChanged()
            .map { memeList ->
                memeList.map {
                    it.toDomain()
                }
            }
            .map { SearchResults(it, searchParameters) }
    }

}