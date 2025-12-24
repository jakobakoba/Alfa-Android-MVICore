package com.bor96dev.newsapp.data

import com.bor96dev.newsapp.NewsFeature
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object NewsFeatureModule {

    @Provides
    fun provideNewsFeature(
        apiService: ApiService
    ): NewsFeature = NewsFeature(apiService)
}