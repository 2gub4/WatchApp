package com.jsdr.watchapp.data.models.dtos.shows

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeasonDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("overview") val overview: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("air_date") val airDate: String? = null,
    @SerialName("episode_count") val episodeCount: Int,
)