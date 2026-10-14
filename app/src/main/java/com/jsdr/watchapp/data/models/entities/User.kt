package com.jsdr.watchapp.data.models.entities

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.time.LocalDate
import java.util.Date

data class User(
    @DocumentId val uid: String = "",
    val email: String? = null,
    val username: String? = null,
    @ServerTimestamp val registrationDate: Date? = null,
    val birthYear: Int? = LocalDate.now().year - 18,
    val gender: String? = "Pan",
    val pfpPath: String = "default_pfp.png",
    val watchedMoviesCount: Int = 0,
    val watchedTvSeriesCount: Int = 0,
    val favouritesCount: Int = 0,
    val ratingsCount: Int = 0,
    val totalListCount: Int = 0,
)