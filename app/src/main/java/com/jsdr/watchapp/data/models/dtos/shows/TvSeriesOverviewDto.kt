package com.jsdr.watchapp.data.models.dtos.shows

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TvSeriesOverviewDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("poster_path") val posterPath: String? = null
)