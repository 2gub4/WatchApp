package com.jsdr.watchapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jsdr.watchapp.BrandPurple
import com.jsdr.watchapp.ui.components.CategoryButton
import com.jsdr.watchapp.ui.components.MediaTile
import com.jsdr.watchapp.ui.components.SpotifyScrollbar
import com.jsdr.watchapp.ui.navigation.Screen

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val gridState = rememberLazyGridState()
    val state by viewModel.viewState.collectAsState()
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryButton(
                    title = "Filmy",
                    isSelected = state.areMoviesSelected,
                    onClick = {
                        viewModel.toggleMediaType(true)
                    }
                )
                CategoryButton(
                    title = "Seriale",
                    isSelected = !state.areMoviesSelected,
                    onClick = {
                        viewModel.toggleMediaType(false)
                    }
                )
            }
            val categories = if (state.areMoviesSelected) {
                mapOf(
                    "Popularne" to "popular",
                    "Teraz grane" to "now_playing",
                    "Najwyżej oceniane" to "top_rated",
                    "Nadchodzące" to "upcoming"
                )
            } else {
                mapOf(
                    "Popularne" to "popular",
                    "Teraz emitowane" to "on_air",
                    "Najwyżej oceniane" to "top_rated",
                )
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories.entries.toList()) { category ->

                    CategoryButton(
                        title = category.key,
                        // POPRAWKA: Prawidłowe porównanie z wartością z ViewModelu
                        isSelected = state.selectedTab == category.value,
                        onClick = {
                            // WYWOŁANIE: Aktualizujemy stan w ViewModelu za pomocą wartości (np. "top_rated")
                            viewModel.setSelectedTab(category.value)
                        },

                        horizontalPadding = 10,
                        verticalPadding = 4,
                        cornerSize = 30
                    )
                }
            }
            if (state.isLoadingFirstPage) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = BrandPurple
                    )
                }
            } else {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(
                        bottom = 20.dp
                    )
                ) {
                    itemsIndexed(state.mediaList) { index, media ->
                        if (
                            index >= state.mediaList.size - 4 &&
                            !state.isLoadingNextPage
                        ) {
                            LaunchedEffect(index) {
                                viewModel.loadNextPage()
                            }
                        }
                        MediaTile(
                            media = media,
                            onClick = {
                                navController.navigate(
                                    Screen.MovieDetails.createRoute(
                                        media.id,
                                        state.areMoviesSelected
                                    )
                                )
                            },
                            modifier = Modifier.aspectRatio(0.6f)
                        )
                    }
                    if (state.isLoadingNextPage) {
                        item(
                            span = {
                                GridItemSpan(maxLineSpan)
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = BrandPurple
                                )
                            }
                        }
                    }
                }
            }
        }
        SpotifyScrollbar(
            gridState = gridState,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight()
        )
    }
}