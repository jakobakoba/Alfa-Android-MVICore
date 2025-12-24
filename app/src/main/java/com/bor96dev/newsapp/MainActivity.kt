package com.bor96dev.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.badoo.binder.Binder
import com.badoo.mvicore.feature.Feature
import com.bor96dev.newsapp.model.NewsItemUi
import com.bor96dev.newsapp.model.NewsStateUi
import com.bor96dev.newsapp.model.NewsTransformer
import com.bor96dev.newsapp.ui.theme.NewsAppTheme
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.functions.Consumer
import jakarta.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var feature: NewsFeature

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (feature.state.query.isEmpty() && feature.state.news.isEmpty()) {
            feature.accept(NewsFeature.Wish.SearchClicked("айти"))
        }
        setContent {
            NewsAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        feature = feature
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    feature: NewsFeature
) {
    val state by rememberFeatureState(
        feature = feature,
        transformer = NewsTransformer,
        initial = NewsStateUi()
    )

    var text by rememberSaveable { mutableStateOf(state.query) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                placeholder = { Text("Поиск новостей по теме...") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(8.dp),
                onClick = { feature.accept(NewsFeature.Wish.SearchClicked(text)) }) {
                Text("Поиск")
            }
        }
        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        when {
            state.isLoading -> {
                CenterState("Загрузка", "Поиск новостей...")
            }

            state.errorMessage != null -> {
                CenterState("Ошибка", state.errorMessage.toString())
            }

            state.items.isEmpty() -> {
                CenterState("Пусто", "Ничего не найдено")
            }

            else -> {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(
                        state.items,
                        key = { it.title + it.imageUrl }
                    ) { item ->
                        ArticleItem(item)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleItem(item: NewsItemUi) {
    Card(elevation = CardDefaults.cardElevation(4.dp)) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl.toUri())
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.placeholder)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3
                )
            }
        }
    }
}

@Composable
fun <UiState : Any> rememberFeatureState(
    feature: Feature<*, NewsFeature.State, *>,
    transformer: (NewsFeature.State) -> UiState,
    initial: UiState
): androidx.compose.runtime.State<UiState> {
    val uiState = remember { mutableStateOf(initial) }
    val binder = remember { Binder() }

    DisposableEffect(feature) {
        val consumer = Consumer<NewsFeature.State> { state ->
            uiState.value = transformer(state)
        }

        binder.bind(feature to consumer)
        onDispose { binder.dispose() }
    }
    return uiState
}

@Composable
fun CenterState(title: String, subtitle: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}