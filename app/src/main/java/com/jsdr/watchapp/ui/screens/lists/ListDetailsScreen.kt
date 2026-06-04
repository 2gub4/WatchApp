package com.jsdr.watchapp.ui.screens.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jsdr.watchapp.BrandPurple
import com.jsdr.watchapp.DarkBackground
import com.jsdr.watchapp.data.models.entities.UserList
import com.jsdr.watchapp.ui.components.ListTile
import com.jsdr.watchapp.ui.navigation.Screen

@Composable
fun ListDetailsScreen(
    movieList: UserList,
    navController: NavController,
    viewModel: ListDetailsViewModel = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    LaunchedEffect(movieList.name) {
        viewModel.loadListDetails(movieList.name)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "←",
                        color = BrandPurple,
                        fontSize = 30.sp,
                        modifier = Modifier
                            .clickable {
                                navController.popBackStack()
                            }
                            .padding(end = 16.dp)
                    )
                    Text(
                        text = movieList.name,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movieList.description ?: "Brak opisu",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(30.dp))
            }
            if (viewState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandPurple)
                    }
                }
            }
            viewState.error?.let { errorMsg ->
                item {
                    Text(
                        text = "Wystąpił błąd: $errorMsg",
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (!viewState.isLoading && viewState.error == null) {
                if (viewState.mediaItems.isEmpty()) {
                    item {
                        Text(
                            text = "Ta lista jest obecnie pusta. Dodaj filmy lub seriale, aby je tutaj zobaczyć!",
                            color = Color.Gray,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp)
                        )
                    }
                } else {
                    items(viewState.mediaItems) { media ->
                        ListTile(
                            media = media,
                            onClick = {
                                navController.navigate(
                                    Screen.MovieDetails.createRoute(
                                        movieId = media.id,
                                        isMovie = media.isMovie
                                    )
                                )
                            },
                            onDeleteClick = {
                                viewModel.removeMediaFromList(media.id, media.isMovie)
                            }
                        )
                    }
                }
            }
        }
    }
}