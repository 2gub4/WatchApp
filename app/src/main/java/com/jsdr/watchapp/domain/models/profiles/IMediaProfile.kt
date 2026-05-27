package com.jsdr.watchapp.domain.models.profiles

import com.jsdr.watchapp.data.models.entities.Rating
import com.jsdr.watchapp.data.models.dtos.shared.CastMemberDto

interface IMediaProfile {
    val rating: Rating?
    val containingLists: List<String>
    fun getTopNActors(number: Int): List<CastMemberDto>
}

