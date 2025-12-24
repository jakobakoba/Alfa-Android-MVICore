package com.bor96dev.newsapp.model

import com.bor96dev.newsapp.NewsFeature

object NewsTransformer: (NewsFeature.State) -> NewsStateUi {
    override fun invoke(state: NewsFeature.State): NewsStateUi = NewsStateUi(
        items = state.news.map {
            NewsItemUi(
                it.title ?: "",
                it.description ?: "",
                it.urlToImage ?: ""
            )
        },
        isLoading = state.isLoading,
        query = state.query
    )
}