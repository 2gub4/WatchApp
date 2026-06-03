package com.jsdr.watchapp.ui.screens.media
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import com.jsdr.watchapp.ui.components.SpotifyScrollbar
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
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
import com.jsdr.watchapp.data.firebase.WatchAppFirestore
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
    val runtime = when (val state = uiState) {
        is MediaDetailsUiState.MovieSuccess -> state.profile.movieDetails.runtime
        else -> "N/A"
    }
    val cast = when (val state = uiState) {
        is MediaDetailsUiState.MovieSuccess -> state.profile.getTop5Actors()
            .joinToString { it.name }

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
    val gridState = rememberLazyGridState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = 120.dp,
                end = 20.dp
            )
        ) {

            item {
                Text(
                    text = "←",
                    color = Color.White,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .clickable { navController.popBackStack() }
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .clip(RoundedCornerShape(24.dp))
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
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            width = 2.dp,
                            color = BrandPurple,
                            shape = RoundedCornerShape(24.dp)
                        )
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
                    if (isMovie) {
                        Text(
                            text = "Czas trwania: $runtime min",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
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
                    Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Ulubione",
                                tint = BrandPurple
                            )

                            Icon(
                                imageVector = Icons.Default.MoreTime,
                                contentDescription = "Do obejrzenia",
                                tint = BrandPurple
                            )
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = "Dodaj do wybranej listy",
                                tint = BrandPurple
                            )
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Obejrzane",
                                tint = BrandPurple
                            )
                        }


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
                }
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
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedTextField(
                            value = reviewContent,
                            onValueChange = {
                                reviewContent = it
                                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
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
                            shape = RoundedCornerShape(24.dp)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showRatingsDialog = false
                        }
                    ) {
                        Text(
                            text = "Gotowe",
                            color = BrandPurple
                        )
                    }
                }
            )
        }
        SpotifyScrollbar(
            gridState = gridState,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight()
        )
    }
}

