package com.bor96dev.newsapp.model

import android.util.Log
import com.bor96dev.newsapp.NewsFeature

object NewsTransformer: (NewsFeature.State) -> NewsStateUi {
    override fun invoke(state: NewsFeature.State): NewsStateUi {
        state.news.forEach { article ->
            Log.d("NewsURL", "Title: ${article.title}, ImageUrl: ${article.urlToImage}")
        }

        return NewsStateUi(
            items = state.news.map {
                NewsItemUi(
                    it.title ?: "",
                    it.description ?: "",
                    it.urlToImage ?: "",
                )
            },
            isLoading = state.isLoading,
            query = state.query
        )
    }
}