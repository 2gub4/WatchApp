package com.jsdr.watchapp.data.models.dtos.movies

import com.jsdr.watchapp.data.models.dtos.shared.CreditsDto
import com.jsdr.watchapp.data.models.dtos.shared.GenreDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailsDto(
    @SerialName("id") val id: Int,
    @SerialName("imdb_id") val imdbId: String? = null,
    @SerialName("title") val title: String,
    @SerialName("overview") val overview: String,
    @SerialName("original_language") val originalLanguage: String,
    @SerialName("origin_country") val originalCountry: List<String> = emptyList(),
    @SerialName("release_date") val releaseDate: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("genres") val genres: List<GenreDto> = emptyList(),
    @SerialName("credits") val credits: CreditsDto,
)