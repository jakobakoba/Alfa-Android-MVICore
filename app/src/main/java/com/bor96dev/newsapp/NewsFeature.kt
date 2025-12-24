package com.bor96dev.newsapp

import com.badoo.mvicore.android.AndroidMainThreadFeatureScheduler
import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import com.bor96dev.newsapp.data.ApiService
import com.bor96dev.newsapp.model.Article
import com.bor96dev.newsapp.model.UiError
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.rxjava3.core.Observable
import jakarta.inject.Inject
import java.util.concurrent.TimeUnit

@ActivityRetainedScoped
class NewsFeature @Inject constructor(
    apiService: ApiService,
) : ActorReducerFeature<
        NewsFeature.Wish,
        NewsFeature.Effect,
        NewsFeature.State,
        Nothing
        >(
    initialState = State(),
    actor = ActorImpl(apiService),
    reducer = ReducerImpl(),
    bootstrapper = BootstrapperImpl(),
    featureScheduler = AndroidMainThreadFeatureScheduler,
) {
    data class State(
        val news: List<Article> = emptyList(),
        val isLoading: Boolean = false,
        val query: String = "",
        val error: UiError? = null
    )

    sealed class Wish {
        data class SearchClicked(val query: String) : Wish()
    }

    sealed class Effect {
        object StartedLoading : Effect()
        data class FinishedWithSuccess(val news: List<Article>) : Effect()
        data class FinishedWithError(val error: UiError) : Effect()
        data class QueryChanged(val query: String) : Effect()
    }

    class ActorImpl(private val apiService: ApiService) : Actor<State, Wish, Effect> {
        override fun invoke(state: State, wish: Wish): Observable<out Effect> = when (wish) {
            is Wish.SearchClicked -> {
                Observable.concat(
                    Observable.just(Effect.QueryChanged(wish.query)),
                    loadNews(if (wish.query.isBlank()) "айти" else wish.query)
                        .delay(300, TimeUnit.MILLISECONDS)
                )
            }
        }

        private fun loadNews(query: String): Observable<Effect> =
            apiService.searchArticles(query)
                .map { response ->
                    if (response.articles.isEmpty()) {
                        Effect.FinishedWithError(UiError.EmptyResult)
                    } else {
                        Effect.FinishedWithSuccess(response.articles)
                    }
                }
                .startWithArray(Effect.QueryChanged(query), Effect.StartedLoading)
                .onErrorReturn { throwable ->
                    Effect.FinishedWithError(
                        when (throwable) {
                            is java.io.IOException -> UiError.NoInternet
                            else -> UiError.Unknown
                        }
                    )
                }
    }

    class ReducerImpl : Reducer<State, Effect> {
        override fun invoke(
            state: State,
            effect: Effect
        ): State = when (effect){
            is Effect.StartedLoading ->
                state.copy(isLoading = true, error = null)

            is Effect.QueryChanged ->
                state.copy(query = effect.query)

            is Effect.FinishedWithSuccess ->
                state.copy(
                    isLoading = false,
                    news = effect.news,
                    error = null
                )

            is Effect.FinishedWithError ->
                state.copy(
                    isLoading = false,
                    news = emptyList(),
                    error = effect.error
                )
        }
    }
}

class BootstrapperImpl : Bootstrapper<NewsFeature.Wish> {
    override fun invoke(): Observable<NewsFeature.Wish> =
        Observable.just(NewsFeature.Wish.SearchClicked("айти"))
}