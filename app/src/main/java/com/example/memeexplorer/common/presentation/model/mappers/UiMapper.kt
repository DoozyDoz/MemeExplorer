package com.example.memeexplorer.common.presentation.model.mappers

interface UiMapper<E, V> {

    fun mapToView(input: E): V
}