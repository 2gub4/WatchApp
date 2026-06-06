package com.jsdr.watchapp.data.models.dtos

data class RatingDto(
    val overallRating: Double = 0.0,
    val characters: Double = 0.0,
    val plot: Double = 0.0,
    val music: Double = 0.0,
    val sfx: Double = 0.0,
    val review: String = "",
)