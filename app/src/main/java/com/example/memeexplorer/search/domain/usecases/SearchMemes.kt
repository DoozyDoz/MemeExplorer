package com.example.memeexplorer.search.domain.usecases

import com.example.memeexplorer.common.domain.repositories.MemeRepository
import com.example.memeexplorer.search.domain.model.SearchParameters
import com.example.memeexplorer.search.domain.model.SearchResults
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import io.reactivex.functions.Function3
import io.reactivex.internal.operators.single.SingleInternalHelper.toFlowable

class SearchMemes @Inject constructor(private val memeRepository: MemeRepository) {

    private val combiningFunction: Function3<String, String, String, SearchParameters>
        get() = Function3 { query, _, _ ->
            SearchParameters(query)
        }


    operator fun invoke(
        querySubject: BehaviorSubject<String>,
        idSubject: BehaviorSubject<String>,
        pathSubject: BehaviorSubject<String>,
    ): Flowable<SearchResults> {
        val query = querySubject
            .debounce(500L, TimeUnit.MILLISECONDS)
            .map { it.trim() }
            .filter { it.length >= 2 }

        val id = idSubject.replaceUIEmptyValue()
        val path = pathSubject.replaceUIEmptyValue()


        return Observable.combineLatest(query, id, path, combiningFunction)
            .toFlowable(BackpressureStrategy.LATEST)
            .switchMap { parameters: SearchParameters ->
                memeRepository.searchCachedMemesBy(parameters)
            }
    }

    private fun BehaviorSubject<String>.replaceUIEmptyValue() = map {
        if (it == GetSearchFilters.NO_FILTER_SELECTED) "" else it
    }

}
