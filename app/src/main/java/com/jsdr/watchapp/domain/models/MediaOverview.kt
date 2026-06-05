package com.jsdr.watchapp.domain.models

import com.jsdr.watchapp.data.models.dtos.movies.MovieDetailsDto
import com.jsdr.watchapp.data.models.dtos.movies.MovieOverviewDto
import com.jsdr.watchapp.data.models.dtos.shared.MultiOverviewDto
import com.jsdr.watchapp.data.models.dtos.shows.TvSeriesDetailsDto
import com.jsdr.watchapp.data.models.dtos.shows.TvSeriesOverviewDto

data class MediaOverview(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String? = null,
    val isMovie: Boolean
)

fun MovieOverviewDto.toDomain(): MediaOverview {
    return MediaOverview(
        id = this.id,
        title = this.title,
        posterPath = this.posterPath,
        releaseDate = this.releaseDate,
        isMovie = true
    )
}

fun TvSeriesOverviewDto.toDomain(): MediaOverview {
    return MediaOverview(
        id = this.id,
        title = this.name,
        posterPath = this.posterPath,
        isMovie = false
    )
}

fun MultiOverviewDto.toDomain(): MediaOverview? {
    return when (mediaType) {
        "movie" -> MediaOverview(
            id = this.id,
            title = this.title ?: "",
            posterPath = this.posterPath,
            releaseDate = this.releaseDate,
            isMovie = true
        )
        "tv" -> MediaOverview(
            id = this.id,
            title = this.name ?: "",
            posterPath = this.posterPath,
            releaseDate = this.firstAirDate,
            isMovie = false
        )
        else -> null
    }
}

fun MovieDetailsDto.toOverview() = MediaOverview(
    id = this.id,
    title = this.title,
    posterPath = this.posterPath,
    releaseDate = this.releaseDate,
    isMovie = true
)

fun TvSeriesDetailsDto.toOverview() = MediaOverview(
    id = this.id,
    title = this.title,
    posterPath = this.posterPath,
    isMovie = false
)