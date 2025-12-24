package com.bor96dev.newsapp.data

import com.bor96dev.newsapp.model.NewsResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("everything")
    fun searchArticles(
        @Query("q") query: String = "",
        @Query("sortBy") sortBy: String = "popularity",
        @Query("apiKey") apiKey: String = "65d6998ce19b48b299f5381d1efacb0a"
    ): Observable<NewsResponse>
}