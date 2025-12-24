package com.bor96dev.newsapp

import com.badoo.mvicore.android.AndroidMainThreadFeatureScheduler
import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import com.bor96dev.newsapp.data.ApiService
import com.bor96dev.newsapp.model.Article
import io.reactivex.rxjava3.core.Observable
import jakarta.inject.Inject
import java.util.concurrent.TimeUnit

class NewsFeature @Inject constructor(
    apiService: ApiService
) : ActorReducerFeature<NewsFeature.Wish, NewsFeature.Effect, NewsFeature.State, Nothing>(
    initialState = State(),
    actor = ActorImpl(apiService),
    reducer = ReducerImpl(),
    featureScheduler = AndroidMainThreadFeatureScheduler
) {

    init {
        accept(Wish.Search(""))
    }
    data class State(
        val news: List<Article> = emptyList(),
        val isLoading: Boolean = false,
        val query: String = "",
    )

    sealed class Wish {
        data class Search(val text: String) : Wish()
    }

    sealed class Effect {
        object StartedLoading : Effect()
        data class FinishedWithSuccess(val news: List<Article>) : Effect()
        data class FinishedWithError(val throwable: Throwable): Effect()
        data class QueryChanged(val query: String) : Effect()
    }

    class ActorImpl(private val apiService: ApiService) : Actor<State, Wish, Effect> {
        override fun invoke(
            state: State,
            wish: Wish
        ): Observable<out Effect> = when (wish) {
            is Wish.Search -> {
                if (wish.text.isEmpty()) {
                    Observable.concat(
                        Observable.just(Effect.QueryChanged(wish.text)),
                        loadNews(wish.text)
                    )
                } else {
                    Observable.just(wish.text)
                        .debounce(600, TimeUnit.MILLISECONDS)
                        .distinctUntilChanged()
                        .switchMap { query ->
                            Observable.concat(
                                Observable.just(Effect.QueryChanged(query)),
                                loadNews(query)
                            )
                        }
                }
            }
        }

        private fun loadNews(query: String): Observable<Effect> {
            val apiCall = if (query == "") {
                apiService.searchArticles("айти")
            } else {
                apiService.searchArticles(query)
            }
            return apiCall
                .map { Effect.FinishedWithSuccess(it.articles) as Effect }
                .startWithItem(Effect.StartedLoading)
                .onErrorReturn { Effect.FinishedWithError(it) }
        }
    }

    class ReducerImpl : Reducer<State, Effect> {
        override fun invoke(
            state: State,
            effect: Effect
        ): State = when (effect){
            is Effect.StartedLoading -> state.copy(isLoading = true, news = emptyList())
            is Effect.QueryChanged -> state.copy(query = effect.query)
            is Effect.FinishedWithSuccess -> state.copy(
                isLoading = false,
                news = effect.news
            )

            is Effect.FinishedWithError -> state.copy(isLoading = false, news = emptyList())
        }
    }
}