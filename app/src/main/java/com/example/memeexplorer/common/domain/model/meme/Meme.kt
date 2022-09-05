package com.example.memeexplorer.common.domain.model.meme

import java.util.*

data class Meme(
    val mId: UUID,
    val mLocation: String,
    val mTag: String
)