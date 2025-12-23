package com.bor96dev.newsapp

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v2/everything")
    fun getArticles(
        @Query("q") query: String = "",
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("apiKey") apiKey: String = "d7bd9da88ca940e5bd57a0d4cf2a00b1"
    ): Observable<NewsResponse>
}