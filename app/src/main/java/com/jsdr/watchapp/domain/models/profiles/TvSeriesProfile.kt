package com.jsdr.watchapp.domain.models.profiles

import com.jsdr.watchapp.data.models.entities.Rating
import com.jsdr.watchapp.data.models.dtos.shared.CastMemberDto
import com.jsdr.watchapp.data.models.dtos.shows.CreatedByDto
import com.jsdr.watchapp.data.models.dtos.shows.TvSeriesDetailsDto

class TvSeriesProfile(
    val seriesDetails: TvSeriesDetailsDto,
    override val rating: Rating? = null,
    override val containingLists: List<String> = emptyList()
) : IMediaProfile {

    fun getTop10Actors(): List<CastMemberDto> {
        return seriesDetails.credits.castMembers.take(10)
    }

    fun getCreator(): CreatedByDto {
        return seriesDetails.createdBy.first()
    }

    override fun getTopNActors(number: Int): List<CastMemberDto> {
        return seriesDetails.credits.castMembers.take(number)
    }
}