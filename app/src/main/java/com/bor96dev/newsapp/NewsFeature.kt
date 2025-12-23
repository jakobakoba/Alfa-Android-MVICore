package com.bor96dev.newsapp

import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ReducerFeature

class NewsFeature : ReducerFeature<NewsFeature.Wish, State, Nothing>(
    initialState = State(),
    reducer = ReducerImpl()
) {
    data class State(
        val news: List<News> = mutableListOf(),
        val isLoading = false,
        val query = "",
    )

    sealed class Wish {
        object RefreshSwiped : Wish()
        data class Search(val text: String) : Wish()
    }

    class ReducerImpl: Reducer<State, Wish>{
        override fun invoke(
            state: State,
            wish: Wish
        ): State {
            when (wish) {
                Wish.RefreshSwiped -> state.copy (

                )
                is Wish.Search -> state.copy (

                )
            }
        }

    }
}