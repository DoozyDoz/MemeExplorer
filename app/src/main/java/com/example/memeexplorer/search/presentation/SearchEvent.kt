package com.example.memeexplorer.search.presentation


sealed class SearchEvent {
    object RequestInitialMemesList: SearchEvent()
    object PrepareForSearch : SearchEvent()
    object StoreMemes : SearchEvent()
    data class QueryInput(val input: String): SearchEvent()
}