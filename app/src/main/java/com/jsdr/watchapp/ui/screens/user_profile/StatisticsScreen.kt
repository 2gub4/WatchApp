package com.jsdr.watchapp.ui.screens.user_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jsdr.watchapp.BrandPurple
import com.jsdr.watchapp.DarkBackground
import com.jsdr.watchapp.ui.components.SpotifyScrollbar

@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: UserProfileViewModel = viewModel()
) {
    val gridState = rememberLazyGridState()
    val statsState by viewModel.statsState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(
                top = 20.dp,
                bottom = 120.dp,
                end = 20.dp
            )
        ) {
            item {
                androidx.compose.foundation.layout.Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "←",
                        color = BrandPurple,
                        fontSize = 32.sp,
                        modifier = Modifier
                            .clickable {
                                navController.popBackStack()
                            }
                            .padding(end = 16.dp)
                    )

                    Text(
                        text = "Statystyki",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            item {
                StatisticCard(
                    title = "Obejrzane filmy",
                    value = statsState.watchedMovies.toString()
                )
            }
            item {
                StatisticCard(
                    title = "Obejrzane seriale",
                    value = statsState.watchedSeries.toString()
                )
            }
            item {
                StatisticCard(
                    title = "Polubione",
                    value = statsState.totalFavourites.toString()
                )
            }
            item {
                StatisticCard(
                    title = "Wystawione oceny",
                    value = statsState.totalRatings.toString()
                )
            }
            item {
                StatisticCard(
                    title = "Średnia ocen",
                    value = String.format("%.1f", statsState.averageRating)
                )
            }
            item {
                StatisticCard(
                    title = "Utworzone listy",
                    value = statsState.totalLists.toString()
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

@Composable
fun StatisticCard(
    title: String,
    value: String
) {
    androidx.compose.foundation.layout.Column {
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BrandPurple),

            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}