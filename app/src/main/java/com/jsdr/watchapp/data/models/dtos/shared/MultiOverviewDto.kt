package com.jsdr.watchapp.data.models.dtos.shared

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MultiOverviewDto(
    @SerialName("id") val id: Int,
    @SerialName("media_type") val mediaType: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("first_air_date") val firstAirDate: String? = null
)