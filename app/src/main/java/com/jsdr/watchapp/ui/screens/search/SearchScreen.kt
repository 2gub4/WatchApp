package com.jsdr.watchapp.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jsdr.watchapp.BrandPurple
import com.jsdr.watchapp.ui.components.SpotifyScrollbar
import com.jsdr.watchapp.ui.navigation.Screen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jsdr.watchapp.domain.models.MediaOverview
import com.jsdr.watchapp.ui.components.MediaTile
import androidx.compose.foundation.lazy.grid.GridItemSpan
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchScreenViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(
                top = 12.dp,
                bottom = 24.dp
            )
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = {
                        viewModel.onQueryChange(it)
                    },
                    placeholder = {
                        Text("Wyszukaj film lub serial")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPurple,
                        unfocusedBorderColor = BrandPurple,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = BrandPurple,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray
                    )
                )
            }
            items(uiState.results.size) { index ->
                val media = uiState.results[index]
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
        }
        SpotifyScrollbar(
            gridState = gridState,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight()
        )
    }
}