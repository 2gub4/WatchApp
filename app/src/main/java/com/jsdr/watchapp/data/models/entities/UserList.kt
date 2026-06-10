package com.jsdr.watchapp.data.models.entities

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserList(
    @DocumentId val id: String = "",
    val name: String = "",
    var description: String? = null,
    val movies: Map<String, Long> = emptyMap(),
    val series: Map<String, Long> = emptyMap(),
    @ServerTimestamp val creationDate: Date? = null,
)