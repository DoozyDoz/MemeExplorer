package com.example.memeexplorer.search.domain.usecases

import com.example.memeexplorer.common.domain.repositories.MemeRepository
import javax.inject.Inject


class GetMemes @Inject constructor(private val memeRepository: MemeRepository) {
    operator fun invoke() = memeRepository.getMemes()
        .filter { it.isNotEmpty() }
}