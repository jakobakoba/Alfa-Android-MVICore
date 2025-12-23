package com.bor96dev.newsapp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.badoo.binder.Binder
import com.badoo.binder.using
import com.bor96dev.newsapp.model.NewsStateUi
import com.bor96dev.newsapp.model.NewsTransformer
import com.bor96dev.newsapp.model.UiEvent
import com.bor96dev.newsapp.model.UiEventTransformer
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.functions.Consumer
import jakarta.inject.Inject
import androidx.compose.runtime.State

@HiltViewModel
class MainViewModel @Inject constructor(
    private val newsFeature: NewsFeature
) : ViewModel() {

    private val binder = Binder()

    private val _uiState = mutableStateOf(NewsStateUi())
    val uiState: State<NewsStateUi> = _uiState

    init {
        val uiConsumer = Consumer<NewsStateUi> {newState ->
            _uiState.value = newState
        }
        binder.bind(newsFeature to uiConsumer using NewsTransformer)

        newsFeature.accept(NewsFeature.Wish.RefreshSwiped)
    }

    fun onEvent(event: UiEvent){
        newsFeature.accept(UiEventTransformer(event))
    }


    override fun onCleared() {
        binder.dispose()
        newsFeature.dispose()
        super.onCleared()
    }
}