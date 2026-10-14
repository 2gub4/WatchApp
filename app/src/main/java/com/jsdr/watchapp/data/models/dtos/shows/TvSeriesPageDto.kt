package com.jsdr.watchapp.data.models.dtos.shows

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TvSeriesPageDto(
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<TvSeriesOverviewDto>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)