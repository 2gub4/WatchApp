package com.jsdr.watchapp.data.models.dtos.shared

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CrewMemberDto(
    @SerialName("name") val name: String,
    @SerialName("profile_path") val profilePath: String? = null,
    @SerialName("department") val department: String,
    @SerialName("job") val job: String
)