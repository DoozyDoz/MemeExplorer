package com.example.memeexplorer.search.presentation.main


sealed class SearchEvent {
    object RequestInitialMemesList: SearchEvent()
    object PrepareForSearch : SearchEvent()
    class IsLoadingMemes(val isLoadingMemes: Boolean) : SearchEvent()
    object StoreMemes : SearchEvent()
    data class QueryInput(val input: String): SearchEvent()
}