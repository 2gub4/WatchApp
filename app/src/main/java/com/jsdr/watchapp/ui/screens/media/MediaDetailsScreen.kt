package com.jsdr.watchapp.ui.screens.media

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.HistoryToggleOff
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.jsdr.watchapp.BrandPurple
import com.jsdr.watchapp.DarkBackground
import com.jsdr.watchapp.R
import com.jsdr.watchapp.data.repository.WatchAppRepository
import com.jsdr.watchapp.ui.components.RatingRow

@Composable
fun MediaDetailsScreen(
    mediaId: Int,
    isMovie: Boolean,
    navController: NavController,
    viewModel: MediaDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val interactionState by viewModel.interactionState.collectAsState()
    val userRating by viewModel.userRating.collectAsState()

    LaunchedEffect(mediaId, isMovie) {
        viewModel.loadProfile(mediaId, isMovie)
    }

    var overallRating by remember { mutableIntStateOf(0) }
    var characterRating by remember { mutableIntStateOf(0) }
    var musicRating by remember { mutableIntStateOf(0) }
    var plotRating by remember { mutableIntStateOf(0) }
    var sfxRating by remember { mutableIntStateOf(0) }
    var reviewContent by remember { mutableStateOf("") }

    LaunchedEffect(userRating) {

        userRating?.let { rating ->

            overallRating = rating.overallRating.toInt()
            characterRating = rating.characters.toInt()
            plotRating = rating.plot.toInt()
            musicRating = rating.music.toInt()
            sfxRating = rating.sfx.toInt()

            reviewContent = rating.review
        }
    }

    var showRatingsDialog by remember { mutableStateOf(false) }
    var showCustomListsDialog by remember { mutableStateOf(false) }

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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = 12.dp,
                bottom = 120.dp
            )
        ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = (-8).dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Powrót",
                            tint = Color.White,

                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    navController.popBackStack()
                                }
                        )
                    }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(420.dp)
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
                            painter = painterResource(
                                id = if (interactionState.isFavorite) R.drawable.heart_minus else R.drawable.heart_check
                            ),
                            contentDescription = "Ulubione",
                            tint = BrandPurple,
                            modifier = Modifier.clickable {
                                viewModel.toggleListStatus(
                                    "favourites",
                                    interactionState.isFavorite,
                                    mediaId,
                                    isMovie
                                )
                            }
                        )

                        val isBucketlistEnabled = !interactionState.isWatched
                        Icon(
                            imageVector = if (interactionState.isInBucketlist) Icons.Default.HistoryToggleOff else Icons.Default.MoreTime,
                            contentDescription = "Do obejrzenia (Bucketlist)",
                            tint = if (isBucketlistEnabled) BrandPurple else Color.DarkGray,
                            modifier = Modifier.clickable(enabled = isBucketlistEnabled) {
                                viewModel.toggleListStatus(
                                    "bucketlist",
                                    interactionState.isInBucketlist,
                                    mediaId,
                                    isMovie
                                )
                            }
                        )

                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Dodaj do wybranej listy",
                            tint = BrandPurple,
                            modifier = Modifier.clickable {
                                showCustomListsDialog = true
                            }
                        )

                        Icon(
                            imageVector = if (interactionState.isWatched) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Obejrzane",
                            tint = BrandPurple,
                            modifier = Modifier.clickable {
                                viewModel.toggleListStatus(
                                    "watched",
                                    interactionState.isWatched,
                                    mediaId,
                                    isMovie
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (userRating == null) {

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
                item {

                    userRating?.let { rating ->

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .border(
                                    2.dp,
                                    BrandPurple,
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(16.dp)
                        ) {

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier.clickable {
                                    showRatingsDialog = true
                                }
                            ) {
                                StarDisplay(
                                    rating = rating.overallRating,
                                    size = 60
                                )
                            }

                            Text(
                                text = "Fabuła",
                                color = Color.White
                            )

                            StarDisplay(
                                rating = rating.plot,
                                size = 24
                            )

                            Text(
                                text = "Bohaterowie",
                                color = Color.White
                            )

                            StarDisplay(
                                rating = rating.characters,
                                size = 24
                            )

                            Text(
                                text = "Muzyka",
                                color = Color.White
                            )

                            StarDisplay(
                                rating = rating.music,
                                size = 24
                            )

                            Text(
                                text = "Efekty specjalne",
                                color = Color.White
                            )
                            StarDisplay(
                                rating = rating.sfx,
                                size = 24
                            )
                            Text(
                                text = rating.review,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            val formattedDate =
                                rating.ratingDate?.let {
                                    java.text.SimpleDateFormat(
                                        "dd.MM.yyyy",
                                        java.util.Locale.getDefault()
                                    ).format(it)
                                } ?: ""

                            Text(
                                text = formattedDate,
                                color = Color.Gray,
                                fontSize = 10.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }


        if (showCustomListsDialog) {
            AlertDialog(
                onDismissRequest = { showCustomListsDialog = false },
                containerColor = DarkBackground,
                title = {
                    Text(
                        text = "Dodaj do swoich list",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (interactionState.customLists.isEmpty()) {
                            Text(
                                text = "Brak zdefiniowanych list niestandardowych.",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 16.sp
                            )
                        } else {
                            interactionState.customLists.forEach { customList ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.toggleListStatus(
                                                listId = customList.listId,
                                                currentStatus = customList.containsMedia,
                                                mediaId = mediaId,
                                                isMovie = isMovie
                                            )
                                        }
                                        .padding(vertical = 6.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = customList.name,
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                    Checkbox(
                                        checked = customList.containsMedia,
                                        onCheckedChange = {
                                            viewModel.toggleListStatus(
                                                listId = customList.listId,
                                                currentStatus = customList.containsMedia,
                                                mediaId = mediaId,
                                                isMovie = isMovie
                                            )
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = BrandPurple,
                                            uncheckedColor = Color.White.copy(alpha = 0.5f),
                                            checkmarkColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showCustomListsDialog = false }
                    ) {
                        Text(
                            text = "Gotowe",
                            color = BrandPurple,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }

        if (showRatingsDialog) {
            AlertDialog(
                onDismissRequest = { showRatingsDialog = false },
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
                            title = "Ocena ogólna",
                            rating = overallRating,
                            onRatingChange = { overallRating = it }
                        )


                        RatingRow(
                            title = "Fabuła",
                            rating = plotRating,
                            onRatingChange = { plotRating = it }
                        )

                        RatingRow(
                            title = "Bohaterowie",
                            rating = characterRating,
                            onRatingChange = { characterRating = it }
                        )

                        RatingRow(
                            title = "Muzyka",
                            rating = musicRating,
                            onRatingChange = { musicRating = it }
                        )

                        RatingRow(
                            title = "Efekty specjalne",
                            rating = sfxRating,
                            onRatingChange = { sfxRating = it }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = reviewContent,
                            onValueChange = { reviewContent = it },
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

                            viewModel.saveRating(
                                mediaId = mediaId,
                                isMovie = isMovie,
                                newRating = com.jsdr.watchapp.data.models.entities.Rating(
                                    movieId = mediaId.toString(),
                                    overallRating = overallRating.toDouble(),
                                    characters = characterRating.toDouble(),
                                    plot = plotRating.toDouble(),
                                    music = musicRating.toDouble(),
                                    sfx = sfxRating.toDouble(),
                                    review = reviewContent
                                )
                            )

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
    }
}
@Composable
fun StarDisplay(
    rating: Double,
    size: Int = 20
) {
    Row {
        for (i in 1..5) {
            Text(
                text = if (i <= rating.toInt()) "★" else "☆",
                color = BrandPurple,
                fontSize = size.sp
            )
        }
    }
}