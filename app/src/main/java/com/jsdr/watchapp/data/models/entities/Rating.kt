package com.jsdr.watchapp.data.models.entities

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Rating(
    @DocumentId val movieId: String = "",
    val overallRating: Double = 0.0,
    val characters: Double = 0.0,
    val plot: Double = 0.0,
    val music: Double = 0.0,
    val sfx: Double = 0.0,
    val review: String = "",
    @ServerTimestamp val ratingDate: Date? = null,
)
