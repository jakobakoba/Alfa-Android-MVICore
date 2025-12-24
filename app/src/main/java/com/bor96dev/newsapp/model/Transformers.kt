package com.bor96dev.newsapp.model

import com.bor96dev.newsapp.NewsFeature

object NewsTransformer : (NewsFeature.State) -> NewsStateUi {
    override fun invoke(state: NewsFeature.State): NewsStateUi {
        return NewsStateUi(
            items = state.news.map {
                NewsItemUi(
                    it.title ?: "",
                    it.description ?: "",
                    it.urlToImage ?: "",
                )
            },
            isLoading = state.isLoading,
            query = state.query,
            errorMessage = when (state.error){
                UiError.NoInternet -> "Нет интернета / Превышен API лимит"
                UiError.EmptyResult -> "Ничего не найдено"
                UiError.Unknown -> "Неизвестная ошибка"
                null -> null
            }
        )
    }
}