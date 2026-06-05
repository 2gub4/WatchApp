package com.jsdr.watchapp.ui.screens.search

import com.jsdr.watchapp.domain.models.MediaOverview

data class SearchScreenUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<MediaOverview> = emptyList()
)