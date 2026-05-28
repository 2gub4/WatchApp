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

@Composable
fun SearchScreen(
    navController: NavController
) {

    var searchText by remember {
        mutableStateOf("")
    }
    /// testy kubuś nie wkurwiaj sie
    val movies = listOf(
        "Interstellar",
        "Breaking Bad",
        "Fight Club",
        "The Batman",
        "The Office",
        "Dark",
        "Joker",
        "Inception",
        "Avatar",
        "Shrek",
        "Stranger Things",
        "Peaky Blinders",
        "The Matrix"
    )

    val filteredMovies = movies.filter {
        it.contains(searchText, ignoreCase = true)
    }

    val gridState = rememberLazyGridState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            state = gridState,

            modifier = Modifier
                .fillMaxSize()
                .padding(end = 18.dp),

            verticalArrangement = Arrangement.spacedBy(14.dp),

            contentPadding = PaddingValues(
                top = 12.dp,
                bottom = 24.dp
            )
        ) {

            item {

                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
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

            items(filteredMovies.size) { index ->

                val movie = filteredMovies[index].length * 123 + index

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(BrandPurple)
                        .clickable {

                            navController.navigate(
                                Screen.MovieDetails.createRoute(movie, true)
                            )
                        }
                        .padding(16.dp)
                ) {

                    Column {

                        Text(
                            text = movie.toString(),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Kliknij aby otworzyć profil",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
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