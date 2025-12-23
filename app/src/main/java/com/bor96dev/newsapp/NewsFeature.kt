package com.bor96dev.newsapp

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import com.badoo.mvicore.feature.ReducerFeature
import io.reactivex.rxjava3.core.Observable
import kotlin.contracts.Effect
import kotlin.contracts.ExperimentalContracts

class NewsFeature : ActorReducerFeature<NewsFeature.Wish, Effect, State, Nothing>(
    initialState = State(),
    actor = ActorImpl(),
    reducer = ReducerImpl()
) {
    data class State(
        val news: List<News> = mutableListOf(),
        val isLoading: Boolean = false,
        val query : String = "",
    )

    sealed class Wish {
        object RefreshSwiped : Wish()
        data class Search(val text: String) : Wish()
        object ClearSearch : Wish()
    }

    sealed class Effect {
        object StartedLoading : Effect()
        data class FinishedWithSuccess(val news: List<News>) : Effect()
        data class FinishedWithError(val throwable: Throwable): Effect()
    }

    class ActorImpl: Actor<State, Wish, Effect> {
        private val service : Observable<String> = TODO()
        override fun invoke(
            state: State,
            wish: Wish
        ): Observable<out Effect>  = when(wish){
            is Wish.RefreshSwiped -> loadNews()
            is Wish.Search -> loadNews()
            is Wish.ClearSearch -> loadNews()

        }


    }

    class ReducerImpl: Reducer<State, Effect>{
        override fun invoke(
            state: State,
            effect: NewsFeature.Effect
        ): State = when (effect){
            is NewsFeature.Effect.StartedLoading -> state.copy(isLoading = true)
            is NewsFeature.Effect.FinishedWithSuccess -> state.copy(
                isLoading = false,
                news = effect.news
            )
            is NewsFeature.Effect.FinishedWithError -> state.copy(
                isLoading = false
            )
        }


    }
}