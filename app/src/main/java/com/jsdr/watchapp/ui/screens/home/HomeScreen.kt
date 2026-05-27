package com.jsdr.watchapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.saveable.rememberSaveable
import com.jsdr.watchapp.BrandPurple
import com.jsdr.watchapp.ui.components.MoviePlaceholder
import com.jsdr.watchapp.ui.components.SpotifyScrollbar
import com.jsdr.watchapp.ui.navigation.Screen

enum class HomeCategory {
    MOVIES,
    SERIES
}

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {

    val gridState = rememberLazyGridState()

    var selectedCategory by rememberSaveable {
        mutableStateOf(HomeCategory.MOVIES)
    }

    val itemCount =
        if (selectedCategory == HomeCategory.MOVIES) {
            64
        } else {
            32
        }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {

        Column {

            // ZAKŁADKI
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),

                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                CategoryButton(
                    title = "Filmy",
                    isSelected = selectedCategory == HomeCategory.MOVIES,
                    onClick = {
                        selectedCategory = HomeCategory.MOVIES
                    }
                )

                CategoryButton(
                    title = "Seriale",
                    isSelected = selectedCategory == HomeCategory.SERIES,
                    onClick = {
                        selectedCategory = HomeCategory.SERIES
                    }
                )
            }

            // GRID
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

                items(itemCount) { index ->

                    val title =
                        if (selectedCategory == HomeCategory.MOVIES) {
                            "Film $index"
                        } else {
                            "Serial $index"
                        }

                    MoviePlaceholder(
                        movieName = title,

                        onClick = {

                            navController.navigate(
                                Screen.MovieDetails.createRoute(title)
                            )
                        },

                        modifier = Modifier.aspectRatio(0.6f)
                    )
                }
            }
        }

        // SCROLLBAR
        SpotifyScrollbar(
            gridState = gridState,

            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight()
        )
    }
}

@Composable
fun CategoryButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))

            .background(
                if (isSelected)
                    BrandPurple
                else
                    Color.Transparent
            )

            .border(
                width = 2.dp,
                color = BrandPurple,
                shape = RoundedCornerShape(14.dp)
            )

            .clickable {
                onClick()
            }

            .padding(
                horizontal = 22.dp,
                vertical = 10.dp
            )
    ) {

        androidx.compose.material3.Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}