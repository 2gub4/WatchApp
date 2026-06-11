package com.jsdr.watchapp.data.models.entities

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.jsdr.watchapp.BrandPurple
import java.util.Date
import androidx.core.graphics.toColorInt
import com.jsdr.watchapp.data.models.toHex


data class UserList(
    @DocumentId val id: String = "",
    val name: String = "",
    var description: String? = null,
    val color: String = BrandPurple.toHex(),
    val movies: Map<String, Long> = emptyMap(),
    val series: Map<String, Long> = emptyMap(),
    @ServerTimestamp val creationDate: Date? = null,
)