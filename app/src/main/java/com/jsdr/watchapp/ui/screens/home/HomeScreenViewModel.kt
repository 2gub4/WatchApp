package com.jsdr.watchapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsdr.watchapp.data.repository.CURRENT_USER
import com.jsdr.watchapp.data.repository.WatchAppRepository
import com.jsdr.watchapp.domain.models.MediaOverview
import com.jsdr.watchapp.domain.models.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. Zdefiniowanie jedynego, spójnego stanu dla ekranu
data class HomeUiState(
    val currentUser: String = CURRENT_USER, 
    val language: String = "pl-PL",
    val isMoviesSelected: Boolean = true, // true - movie, false - tv series
    val pageNumber: Int = 1,
    val isLoadingFirstPage: Boolean = true,
    val isLoadingNextPage: Boolean = false,
    val mediaList: List<MediaOverview> = emptyList(),
    val selectedMediaId: Int? = null,
    val error: String? = null
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadInitialData() }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingFirstPage = true, error = null) }
            val state = _uiState.value
            try {
                val newItems = fetchMediaPage(isMovies = state.isMoviesSelected, pageNumber = 1)
                _uiState.update {
                    it.copy(
                        mediaList = newItems,
                        pageNumber = 1,
                        isLoadingFirstPage = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoadingFirstPage = false, error = "data fetching error") }
            }
        }
    }
    
    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoadingNextPage || state.isLoadingFirstPage) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingNextPage = true) }
            val nextPage = state.pageNumber + 1
            try {
                val newItems = fetchMediaPage(isMovies = state.isMoviesSelected, pageNumber = nextPage)
                _uiState.update {
                    it.copy(
                        mediaList = it.mediaList + newItems,
                        pageNumber = nextPage,
                        isLoadingNextPage = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoadingNextPage = false, error = "unable to load more data") }
            }
        }
    }
    
    fun toggleMediaType(showMovies: Boolean) {
        if (_uiState.value.isMoviesSelected == showMovies) return
        _uiState.update {
            it.copy(
                isMoviesSelected = showMovies,
                pageNumber = 1,
                mediaList = emptyList(),
                isLoadingFirstPage = true
            )
        }
        loadInitialData()
    }
    
    private suspend fun fetchMediaPage(isMovies: Boolean, pageNumber: Int): List<MediaOverview> {
        return if (isMovies) {
            val response = WatchAppRepository.Movies.getMoviePage(pageNumber = pageNumber, listType = "popular")
            response?.results?.map { it.toDomain() } ?: emptyList()
        } else {
            val response = WatchAppRepository.TvSeries.getTvSeriesPage(pageNumber = pageNumber, listType = "popular")
            response?.results?.map { it.toDomain() } ?: emptyList()
        }
    }

    fun openProfile(id: Int) {
        _uiState.update { it.copy(selectedMediaId = id) }
    }

    fun closeProfile() {
        _uiState.update { it.copy(selectedMediaId = null) }
    }
}