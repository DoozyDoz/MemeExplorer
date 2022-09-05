package com.example.memeexplorer.common.domain.model.pagination

import com.example.memeexplorer.common.domain.model.meme.Meme


data class PaginatedMemes(
    val memes: List<Meme>,
    val pagination: Pagination
)