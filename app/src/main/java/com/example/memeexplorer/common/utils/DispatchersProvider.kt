package com.example.memeexplorer.common.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatchersProvider {
    fun io(): CoroutineDispatcher = Dispatchers.IO
}