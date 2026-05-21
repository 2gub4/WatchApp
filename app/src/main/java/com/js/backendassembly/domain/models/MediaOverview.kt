package com.js.backendassembly.domain.models

data class MediaOverview(
    val id: Int,
    val title: String,
    val posterPath: String? = null
)