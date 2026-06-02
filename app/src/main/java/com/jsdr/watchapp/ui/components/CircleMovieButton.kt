package com.jsdr.watchapp.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsdr.watchapp.BrandPurple

@Composable
fun CircleMovieButton(icon: String) {

    Box(
        modifier = Modifier
            .size(70.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 3.dp,
                color = BrandPurple,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { /*WYWOŁANIE DODANIA DO POWIĄZANEJ LISTY*/ },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            fontSize = 35.sp,
            color = BrandPurple
        )
    }
}