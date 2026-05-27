package com.jsdr.watchapp.ui.screens.lists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jsdr.watchapp.BrandPurple
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.jsdr.watchapp.data.models.entities.UserList
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import com.jsdr.watchapp.DarkBackground
import com.jsdr.watchapp.ui.navigation.Screen
@Composable
fun ListDetailsScreen(
    movieList: UserList,
    navController: NavController
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        // GÓRA
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

        // OPIS LISTY
        Text(
            text = movieList.description ?: "no description",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        // FILMY W LIŚCIE
        repeat(5) { index ->

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        2.dp,
                        BrandPurple,
                        RoundedCornerShape(18.dp)
                    )
                    .clickable {

                        navController.navigate(
                            Screen.MovieDetails.createRoute(
                                "Film ${index + 1}"
                            )
                        )
                    }
                    .padding(20.dp)
            ) {

                Column {

                    Text(
                        text = "Film ${index + 1}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Krótki opis filmu",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}