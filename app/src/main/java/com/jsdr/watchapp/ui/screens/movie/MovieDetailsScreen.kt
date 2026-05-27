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

@Composable
fun MovieDetailsScreen(
    movieName: String,
    navController: NavController
) {

    var mainRating by remember { mutableStateOf(0) }

    var actorRating by remember { mutableStateOf(0) }
    var musicRating by remember { mutableStateOf(0) }
    var storyRating by remember { mutableStateOf(0) }

    var reviewText by remember { mutableStateOf("") }

    var showRatingsDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {

        // =====================
        // GÓRA
        // =====================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(2.dp, BrandPurple)
                .padding(12.dp)
        ) {

            Text(
                text = "←",
                color = Color.White,
                fontSize = 30.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clickable {
                        navController.popBackStack()
                    }
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp)
            ) {

                // PLAKAT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(BrandPurple),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Plakat filmu",
                        color = Color.White,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // PRZYCISKI
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                )
                {

                    CircleMovieButton("❤️")
                    CircleMovieButton("⏰")
                    CircleMovieButton("➕")
                }
            }
        }

        // =====================
        // ŚRODEK
        // =====================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .border(2.dp, BrandPurple)
                .padding(20.dp)
        ) {

            Text(
                text = "Tytuł",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Opis filmu",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Data wydania",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "..",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp
            )

        }

        // =====================
        // DÓŁ
        // =====================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(2.dp, BrandPurple)
                .padding(20.dp)
        ) {

            // GŁÓWNE GWIAZDKI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                for (i in 1..5) {

                    Text(
                        text = if (i <= mainRating) "★" else "☆",
                        color = BrandPurple,
                        fontSize = 40.sp,
                        modifier = Modifier.clickable {
                            mainRating = i
                            showRatingsDialog = true
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            Spacer(modifier = Modifier.height(20.dp))

            // OPINIA
            OutlinedTextField(
                value = reviewText,
                onValueChange = {
                    reviewText = it
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
                    text = "Dodatkowe oceny",
                    color = Color.White
                )
            },

            text = {

                Column {

                    RatingRow(
                        title = "Aktorzy",
                        rating = actorRating,
                        onRatingChange = {
                            actorRating = it
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
                        title = "Historia",
                        rating = storyRating,
                        onRatingChange = {
                            storyRating = it
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