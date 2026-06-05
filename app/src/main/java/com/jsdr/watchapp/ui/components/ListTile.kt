package com.jsdr.watchapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.jsdr.watchapp.domain.models.MediaOverview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.jsdr.watchapp.data.repository.WatchAppRepository
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun ListTile(
    media: MediaOverview,
    isWatched: Boolean,
    onClick: () -> Unit,
    onToggleWatched: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF2C2C2C))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = "${WatchAppRepository.POSTERS_BASE_URL}${media.posterPath}",
            contentDescription = "Plakat ${media.title}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(70.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
        )
        Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.Center) {
            Text(
                text = media.title,
                color = if (isWatched) Color.Gray else Color.White,
                textDecoration = if (isWatched) TextDecoration.LineThrough else null,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            media.releaseDate?.let { date ->
                Text(text = date.take(4), color = Color.Gray, fontSize = 12.sp)
            }
        }
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Opcje", tint = Color.Gray)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color(0xFF212121))
            ) {
                DropdownMenuItem(
                    text = { Text(if (isWatched) "Usuń z obejrzanych" else "Oznacz jako obejrzane", color = Color.White) },
                    onClick = {
                        expanded = false
                        onToggleWatched()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Usuń z listy", color = Color.Red) },
                    onClick = {
                        expanded = false
                        onDeleteClick()
                    }
                )
            }
        }
    }
}