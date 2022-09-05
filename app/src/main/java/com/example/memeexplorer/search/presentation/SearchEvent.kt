package com.example.memeexplorer.search.presentation


sealed class SearchEvent {
    data class QueryInput(val input: String): SearchEvent()
}