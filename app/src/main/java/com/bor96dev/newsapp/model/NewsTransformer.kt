package com.bor96dev.newsapp.model

import com.bor96dev.newsapp.NewsFeature

object NewsTransformer: (NewsFeature.State) -> NewsStateUi {
    override fun invoke(state: NewsFeature.State): NewsStateUi = NewsStateUi(
        items = state.news.map {
            NewsItemUi(
                it.title,
                it.description,
                it.urlToImage
            )
        },
        isLoading = state.isLoading,
        query = state.query
    )
}

object UiEventTransformer: (UiEvent) -> NewsFeature.Wish{
    override fun invoke(event: UiEvent): NewsFeature.Wish = when (event){
        is UiEvent.SendQuery -> NewsFeature.Wish.Search(event.query)
        is UiEvent.RefreshSwipe -> NewsFeature.Wish.RefreshSwiped
    }
}