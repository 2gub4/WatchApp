package com.jsdr.watchapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jsdr.watchapp.ui.components.CategoryButton
import com.jsdr.watchapp.ui.components.MediaTile
import com.jsdr.watchapp.ui.components.SpotifyScrollbar
import com.jsdr.watchapp.ui.navigation.Screen
import com.jsdr.watchapp.BrandPurple

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
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryButton(
                    title = "Filmy",
                    isSelected = state.isMoviesSelected,
                    onClick = {
                        viewModel.toggleMediaType(showMovies = true)
                    },
                )
                CategoryButton(
                    title = "Seriale",
                    isSelected = !state.isMoviesSelected,
                    onClick = {
                        viewModel.toggleMediaType(showMovies = false)
                    }
                )
            }
            if (state.isLoadingFirstPage) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandPurple)
                }
            } else {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    itemsIndexed(state.mediaList) { index, media ->
                        if (index >= state.mediaList.size - 4 && !state.isLoadingNextPage) {
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
                                        media.isMovie
                                        )
                                )
                            },
                            modifier = Modifier.aspectRatio(0.6f)
                        )
                    }
                    if (state.isLoadingNextPage) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = BrandPurple)
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

//resetować położenie scrollbara
//wywoływać profil w odpowiedni sposób
