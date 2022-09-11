package com.example.memeexplorer.search.presentation.main

import com.example.memeexplorer.common.presentation.Event
import com.example.memeexplorer.common.presentation.model.UIMeme

data class SearchViewState(
    val loading: Boolean = true,
    val memes: List<UIMeme> = emptyList(),
    val noSearchQuery: Boolean = true,
    val searchResults: List<UIMeme> = emptyList(),
    val searchingMemes: Boolean = false,
    val noMemeResults: Boolean = false,
    val failure: Event<Throwable>? = null
) {
    fun updateToNoSearchQuery(): SearchViewState {
        return copy(
            noSearchQuery = true,
            searchResults = emptyList(),
            noMemeResults = false
        )
    }
    fun updateToSearching(): SearchViewState {
        return copy(
            noSearchQuery = false,
            searchingMemes = false,
            noMemeResults = false
        )
    }

    fun updateToSearchingRemotely(): SearchViewState {
        return copy(
            searchingMemes = true,
            searchResults = emptyList()
        )
    }

    fun updateToHasSearchResults(memes: List<UIMeme>): SearchViewState {
        return copy(
            noSearchQuery = false,
            searchResults = memes,
            searchingMemes = false,
            noMemeResults = false
        )
    }

    fun updateToNoResultsAvailable(): SearchViewState {
        return copy(searchingMemes = false, noMemeResults = true)
    }

    fun updateToHasFailure(throwable: Throwable): SearchViewState {
        return copy(failure = Event(throwable))
    }
}