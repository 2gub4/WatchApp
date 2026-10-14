package com.jsdr.watchapp.data.models.dtos.shared

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreditsDto(
    @SerialName("cast") val castMembers: List<CastMemberDto> = emptyList(),
    @SerialName("crew") val crewMembers: List<CrewMemberDto> = emptyList()
)