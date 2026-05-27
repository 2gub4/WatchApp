package com.jsdr.watchapp.ui.screens.movie
import com.jsdr.watchapp.ui.components.CircleMovieButton
import com.jsdr.watchapp.ui.components.RatingRow
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jsdr.watchapp.BrandPurple
import com.jsdr.watchapp.DarkBackground
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableIntStateOf

//import androidx.compose.runtime.mutableStateListOf
//import com.jsdr.watchapp.domain.models.profiles.MovieProfile

@Composable
fun MovieDetailsScreen(
    movieName: String, //Change to movieId and call getMovieProfile then apply profile to screen
    navController: NavController
) {
    var overallRating by remember { mutableIntStateOf(0) }
    var characterRating by remember { mutableIntStateOf(0) }
    var musicRating by remember { mutableIntStateOf(0) }
    var plotRating by remember { mutableIntStateOf(0) }
    var sfxRating by remember { mutableIntStateOf(0) }
    var reviewContent by remember { mutableStateOf("") }
    //val movieDetails = remember { mutableStateListOf<MovieProfile>() }
    var showRatingsDialog by remember { mutableStateOf(false) }

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
                    .fillMaxSize()
                    .padding(top = 40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(BrandPurple),
                    contentAlignment = Alignment.Center
                ) {
                    //change to async image and display it on top of the screen
                    //AsyncImage() {}
                    Text(
                        text = "Plakat filmu",
                        color = Color.White,
                        fontSize = 22.sp
                    ) //placeholder
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                )
                {
                    CircleMovieButton("<3")
                    CircleMovieButton("⏰")
                    //skreślone oko jako oznaczenie obejrzanego
                    CircleMovieButton("+")
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .border(2.dp, BrandPurple)
                .padding(20.dp)
        ) {
            Text(
                text = "Tytuł: ", //tytuł z profilu
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Synopsis:", //opis z profilu
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Premiera:", // data z profilu
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Obsada:", //obsada z profilu
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Reżyser:", //reżyseria z profilu
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(2.dp, BrandPurple)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                for (i in 1..10 /*or 5*/) {
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
            onDismissRequest = {
                showRatingsDialog = false
            },
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
}