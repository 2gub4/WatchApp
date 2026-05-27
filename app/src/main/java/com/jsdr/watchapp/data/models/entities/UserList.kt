package com.jsdr.watchapp.data.models.entities

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserList(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String? = null,
    val movies: List<Int> = emptyList(),
    val series: List<Int> = emptyList(),
    @ServerTimestamp val creationDate: Date? = null,
)