package com.jsdr.watchapp.domain.models

import com.jsdr.watchapp.data.models.dtos.movies.MovieOverviewDto
import com.jsdr.watchapp.data.models.dtos.shows.TvSeriesOverviewDto

data class MediaOverview(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val isMovie: Boolean
)

fun MovieOverviewDto.toDomain(): MediaOverview {
    return MediaOverview(
        id = this.id,
        title = this.title,
        posterPath = this.posterPath,
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