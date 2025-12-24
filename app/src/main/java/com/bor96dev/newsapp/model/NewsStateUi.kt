package com.bor96dev.newsapp.model

data class NewsStateUi (
    val items: List<NewsItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val query: String = "",
    val errorMessage: String? = null
)

data class NewsItemUi (
    val title: String,
    val description: String,
    val imageUrl: String
)

sealed class UiError {
    object NoInternet : UiError()
    object EmptyResult : UiError()
    object Unknown : UiError()
}
