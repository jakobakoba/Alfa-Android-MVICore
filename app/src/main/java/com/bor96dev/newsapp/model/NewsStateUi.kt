package com.bor96dev.newsapp.model

data class NewsStateUi (
    val items: List<NewsItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val query: String = ""
)

data class NewsItemUi (
    val title: String,
    val description: String,
    val imageUrl: String
)

sealed class UiEvent {
    data class SendQuery(val query: String) : UiEvent()
    object RefreshSwipe : UiEvent()
}