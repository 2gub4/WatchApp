package com.jsdr.watchapp.data.models.dtos

data class UserDto(
    val email: String,
    val username: String,
    val birthYear: Int? = null,
    val gender: String,
    val pfpPath: String = "default_pfp.png",
    val watchedMoviesCount: Int = 0,
    val watchedTvSeriesCount: Int = 0,
    val favouritesCount: Int = 0,
    val ratingsCount: Int = 0,
    val totalListCount: Int = 0,
)