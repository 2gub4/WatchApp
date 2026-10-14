package com.jsdr.watchapp.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsdr.watchapp.BrandPurple
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import com.jsdr.watchapp.DarkBackground
import com.jsdr.watchapp.data.repository.WatchAppRepository
import com.jsdr.watchapp.domain.models.MediaOverview

@Composable
fun MediaTile(
    media: MediaOverview,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 2.dp,
                color = BrandPurple,
                shape = RoundedCornerShape(20.dp)
            )
            .background(DarkBackground)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (media.posterPath != null) {
            AsyncImage(
                model = "${WatchAppRepository.POSTERS_BASE_URL}${media.posterPath}",
                contentDescription = "Plakat ${media.title}",
                //contentScale = ContentScale.Inside,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
            // TU DODAĆ TYTUŁ NA ZACIENIONYM TLE POD PLAKATEM
            //Text(text = media.title, color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Justify, modifier = Modifier.padding(8.dp))
        } else {
            Text(
                text = media.title,
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}