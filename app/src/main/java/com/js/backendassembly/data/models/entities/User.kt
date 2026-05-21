package com.js.backendassembly.data.models.entities

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

import java.util.Date

data class User(
    @DocumentId val uid: String = "", // później będzie pobierane z firebase auth
    val email: String? = null,
    val username: String? = null,
    @ServerTimestamp val registrationDate: Date? = null,
    val pfpPath: String = "default_pfp.png",
    val watchedMovies: List<Int> = emptyList(),
    val watchedMoviesCount: Int = 0,
    val watchedSeriesCount: Int = 0,
    val favouritesCount: Int = 0,
    val ratingsCount: Int = 0,
)