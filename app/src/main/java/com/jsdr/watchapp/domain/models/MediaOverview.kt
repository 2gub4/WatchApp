package com.jsdr.watchapp.domain.models

import com.jsdr.watchapp.data.models.dtos.movies.MovieOverviewDto
import com.jsdr.watchapp.data.models.dtos.shows.TvSeriesOverviewDto

//data class MediaOverview(
//    val id: Int,
//    val title: String,
//    val posterPath: String? = null
//)

data class MediaOverview(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val isTvSeries: Boolean
)

fun MovieOverviewDto.toDomain(): MediaOverview {
    return MediaOverview(
        id = this.id,
        title = this.title,
        posterPath = this.posterPath,
        isTvSeries = false
    )
}

fun TvSeriesOverviewDto.toDomain(): MediaOverview {
    return MediaOverview(
        id = this.id,
        title = this.name,
        posterPath = this.posterPath,
        isTvSeries = true
    )
}