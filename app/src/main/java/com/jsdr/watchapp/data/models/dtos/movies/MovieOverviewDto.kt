package com.jsdr.watchapp.data.models.dtos.movies

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieOverviewDto (
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("release_date") val releaseDate: String
)
