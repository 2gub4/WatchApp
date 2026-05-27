package com.jsdr.watchapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsdr.watchapp.BrandPurple

@Composable
fun RatingRow(
    title: String,
    rating: Int,
    onRatingChange: (Int) -> Unit
) {

    Column {

        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row {

            for (i in 1..5) {

                Text(
                    text = if (i <= rating) "★" else "☆",
                    color = BrandPurple,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .clickable {
                            onRatingChange(i)
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}