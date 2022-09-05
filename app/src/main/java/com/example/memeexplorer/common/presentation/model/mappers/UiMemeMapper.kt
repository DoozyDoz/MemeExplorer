package com.example.memeexplorer.common.presentation.model.mappers

import com.example.memeexplorer.common.domain.model.meme.Meme
import com.example.memeexplorer.common.presentation.model.UIMeme
import javax.inject.Inject


class UiMemeMapper @Inject constructor() : UiMapper<Meme, UIMeme> {

    override fun mapToView(input: Meme): UIMeme {
        return UIMeme(
            id = input.mId.toString(),
            location = input.mLocation
        )
    }
}