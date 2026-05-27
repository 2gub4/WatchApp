package com.jsdr.watchapp.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun StatisticsScreen(
    navController: NavController
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(20.dp)
    ) {


        Row(
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

        Spacer(modifier = Modifier.height(40.dp))


        StatisticCard(
            title = "Obejrzane filmy",
            value = "0"
        )

        Spacer(modifier = Modifier.height(20.dp))

        StatisticCard(
            title = "Obejrzane seriale",
            value = "0"
        )

        Spacer(modifier = Modifier.height(20.dp))

        StatisticCard(
            title = "Polubione",
            value = "0"
        )

        Spacer(modifier = Modifier.height(20.dp))

        StatisticCard(
            title = "Wystawione oceny",
            value = "0"
        )
    }
}

@Composable
fun StatisticCard(
    title: String,
    value: String
) {

    Column {

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