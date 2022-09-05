package com.example.memeexplorer.search.domain.model

import com.example.memeexplorer.common.domain.model.meme.Meme

data class SearchResults(
    val animals: List<Meme>,
    val searchParameters: SearchParameters
)
