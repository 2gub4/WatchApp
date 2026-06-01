package com.jsdr.watchapp.ui.screens.media

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.jsdr.watchapp.BrandPurple
import com.jsdr.watchapp.DarkBackground
import com.jsdr.watchapp.data.repository.WatchAppRepository
import com.jsdr.watchapp.ui.components.CircleMovieButton
import com.jsdr.watchapp.ui.components.RatingRow

@Composable
fun MediaDetailsScreen(
    mediaId: Int,
    isMovie: Boolean,
    navController: NavController,
    viewModel: MediaDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mediaId, isMovie) {
        viewModel.loadProfile(mediaId, isMovie)
    }

    var overallRating by remember { mutableIntStateOf(0) }
    var characterRating by remember { mutableIntStateOf(0) }
    var musicRating by remember { mutableIntStateOf(0) }
    var plotRating by remember { mutableIntStateOf(0) }
    var sfxRating by remember { mutableIntStateOf(0) }
    var reviewContent by remember { mutableStateOf("") }
    var showRatingsDialog by remember { mutableStateOf(false) }

    val title = when (val state = uiState) {
        is MediaDetailsUiState.MovieSuccess -> state.profile.movieDetails.title
        is MediaDetailsUiState.TvSuccess -> state.profile.seriesDetails.title
        else -> "Ładowanie..."
    }
    val overview = when (val state = uiState) {
        is MediaDetailsUiState.MovieSuccess -> state.profile.movieDetails.overview
        is MediaDetailsUiState.TvSuccess -> state.profile.seriesDetails.overview
        else -> ""
    }
    val dates = when (val state = uiState) {
        is MediaDetailsUiState.MovieSuccess ->
            listOf("Premiera: ${state.profile.movieDetails.releaseDate}", "")
        is MediaDetailsUiState.TvSuccess ->
            listOf(
                "Premierowy odcinek: ${state.profile.seriesDetails.firstAired}",
                "Ostatni odcinek: ${state.profile.seriesDetails.lastAired}"
            )
        else -> listOf("Premiera:", "")
    }
    val seasons = when (val state = uiState) {
        is MediaDetailsUiState.TvSuccess ->
            "Liczba sezonów: ${state.profile.seriesDetails.numberOfSeasons}"
        else -> "Brak informacji o sezonach"
    }
    val episodes = when (val state = uiState) {
        is MediaDetailsUiState.TvSuccess ->
            "Liczba odcinków: ${state.profile.seriesDetails.numberOfEpisodes}"
        else -> "Brak informacji o odcinkach"
    }
    val cast = when (val state = uiState) {
        is MediaDetailsUiState.MovieSuccess -> state.profile.getTop5Actors().joinToString { it.name }
        is MediaDetailsUiState.TvSuccess -> state.profile.getTop10Actors().joinToString { it.name }
        else -> ""
    }
    val creatorLabel = when (val state = uiState) {
        is MediaDetailsUiState.MovieSuccess -> "Reżyser: ${state.profile.getDirector().name}"
        is MediaDetailsUiState.TvSuccess -> "Twórca: ${state.profile.seriesDetails.createdBy.firstOrNull()?.name ?: "Brak"}"
        else -> "Reżyser:"
    }
    val posterPath = when (val state = uiState) {
        is MediaDetailsUiState.MovieSuccess -> state.profile.movieDetails.posterPath
        is MediaDetailsUiState.TvSuccess -> state.profile.seriesDetails.posterPath
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, BrandPurple)
                .padding(12.dp)
        ) {
            Text(
                text = "←",
                color = Color.White,
                fontSize = 30.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clickable { navController.popBackStack() }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(BrandPurple),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState is MediaDetailsUiState.Loading) {
                        CircularProgressIndicator(color = Color.White)
                    } else if (posterPath != null) {
                        AsyncImage(
                            model = "${WatchAppRepository.POSTERS_BASE_URL}$posterPath",
                            contentDescription = "Plakat",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = "Brak plakatu",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                )
                {
                    CircleMovieButton("<3")
                    CircleMovieButton("⏰")
                    CircleMovieButton("+")
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(2.dp, BrandPurple)
                .padding(20.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            if (overview.isNotBlank()) {
                Text(
                    text = overview,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
            if (isMovie) {
                Text(
                    text = dates[0],
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
            } else {
                Text(
                    text = dates[0],
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dates[1],
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            if (!isMovie) {
                Text(
                    text = seasons,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = episodes,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
            Text(
                text = "Obsada: ${cast}, ...",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = creatorLabel,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(2.dp, BrandPurple)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                for (i in 1..5) {
                    Text(
                        text = if (i <= overallRating) "★" else "☆",
                        color = BrandPurple,
                        fontSize = 40.sp,
                        modifier = Modifier.clickable {
                            overallRating = i
                            showRatingsDialog = true
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = reviewContent,
                onValueChange = {
                    reviewContent = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                placeholder = {
                    Text("Napisz opinię...")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPurple,
                    unfocusedBorderColor = BrandPurple,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = BrandPurple
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
    if (showRatingsDialog) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = DarkBackground,
            title = {
                Text(
                    text = "Aspekty filmu",
                    color = Color.White
                )
            },
            text = {
                Column {
                    RatingRow(
                        title = "Fabuła",
                        rating = plotRating,
                        onRatingChange = {
                            plotRating = it
                        }
                    )
                    RatingRow(
                        title = "Bohaterowie",
                        rating = characterRating,
                        onRatingChange = {
                            characterRating = it
                        }
                    )
                    RatingRow(
                        title = "Muzyka",
                        rating = musicRating,
                        onRatingChange = {
                            musicRating = it
                        }
                    )
                    RatingRow(
                        title = "Efekty specjalne",
                        rating = sfxRating,
                        onRatingChange = {
                            sfxRating = it
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {}
                ) {
                    Text(
                        text = "Gotowe",
                        color = BrandPurple
                    )
                }
            }
        )
    }
}