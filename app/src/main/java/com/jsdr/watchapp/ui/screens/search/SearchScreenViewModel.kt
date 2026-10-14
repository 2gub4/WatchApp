package com.jsdr.watchapp.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsdr.watchapp.data.repository.WatchAppRepository
import com.jsdr.watchapp.domain.models.MediaOverview
import com.jsdr.watchapp.domain.models.toDomain
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchScreenUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<MediaOverview> = emptyList()
)

class SearchScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        SearchScreenUiState()
    )
    val uiState: StateFlow<SearchScreenUiState> =
        _uiState.asStateFlow()

    private var searchJob: Job? = null
    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(
            query = query
        )
        searchJob?.cancel()
        if (query.length < 2) {
            _uiState.value = _uiState.value.copy(
                results = emptyList()
            )
            return
        }
        searchJob = viewModelScope.launch {
            delay(500)
            _uiState.value =
                _uiState.value.copy(
                    isLoading = true
                )
            try {
                val movies =
                    WatchAppRepository.Movies
                        .searchForMovie(query, 1)
                        ?.results?.map { it.toDomain() }
                        ?: emptyList()
                val series =
                    WatchAppRepository.TvSeries
                        .searchForTvSeries(query, 1)
                        ?.results ?.map { it.toDomain() }
                        ?: emptyList()
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        results = movies + series
                    )
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        results = emptyList()
                    )
            }
        }
    }
}