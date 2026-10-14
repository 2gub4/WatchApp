package com.jsdr.watchapp.domain.models.profiles

import com.jsdr.watchapp.data.models.entities.Rating
import com.jsdr.watchapp.data.models.dtos.shared.CastMemberDto
import com.jsdr.watchapp.data.models.dtos.shared.CrewMemberDto
import com.jsdr.watchapp.data.models.dtos.movies.MovieDetailsDto

class MovieProfile(
    val movieDetails: MovieDetailsDto,
    override val rating: Rating? = null,
    override val containingLists: List<String> = emptyList()
) : IMediaProfile {
    fun getTop5Actors(): List<CastMemberDto> {
        return movieDetails.credits.castMembers.take(5)
    }

    fun getDirector(): CrewMemberDto {
        return movieDetails.credits.crewMembers.first { it.job == "Director" }
    }

    override fun getTopNActors(number: Int): List<CastMemberDto> {
        return movieDetails.credits.castMembers.take(number)
    }
}