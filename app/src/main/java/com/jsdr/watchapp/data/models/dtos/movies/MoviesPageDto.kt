package com.jsdr.watchapp.data.models.dtos.movies

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoviesPageDto(
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<MovieOverviewDto>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)