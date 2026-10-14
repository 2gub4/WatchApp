package com.jsdr.watchapp.data.models.dtos.shows

import com.jsdr.watchapp.data.models.dtos.shared.CreditsDto
import com.jsdr.watchapp.data.models.dtos.shared.GenreDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class TvSeriesDetailsDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val title: String,
    @SerialName("first_air_date") val firstAired: String,
    @SerialName("last_air_date") val lastAired: String,
    @SerialName("number_of_episodes") val numberOfEpisodes: Int,
    @SerialName("number_of_seasons") val numberOfSeasons: Int,
    @SerialName("overview") val overview: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("genres") val genres: List<GenreDto> = emptyList(),
    @SerialName("seasons") val seasons: List<SeasonDto> = emptyList(),
    @SerialName("created_by") val createdBy: List<CreatedByDto> = emptyList(),
    @SerialName("in_production") val inProduction: Boolean,
    @SerialName("tagline") val tagline: String,
    @SerialName("original_language") val originalLanguage: String,
    @SerialName("origin_country") val originCountry: List<String> = emptyList(),
    @SerialName("credits") val credits: CreditsDto,
)



