package com.example.memeexplorer.search.presentation


sealed class SearchEvent {
    object RequestInitialMemesList: SearchEvent()
    object PrepareForSearch : SearchEvent()
    data class QueryInput(val input: String): SearchEvent()
}