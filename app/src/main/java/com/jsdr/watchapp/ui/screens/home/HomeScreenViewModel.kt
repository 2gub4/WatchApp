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

data class HomeViewState(
    val currentUser: String = CURRENT_USER,
    val language: String = "pl-PL",
    val areMoviesSelected: Boolean = true,
    val selectedTab: String = "popular",
    val pageNumber: Int = 1,
    val isLoadingFirstPage: Boolean = true,
    val isLoadingNextPage: Boolean = false,
    val mediaList: List<MediaOverview> = emptyList(),
    val selectedMediaId: Int? = null,
    val error: String? = null
)

class HomeViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState: StateFlow<HomeViewState> = _viewState.asStateFlow()

    init { loadInitialData() }

    private fun loadInitialData() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoadingFirstPage = true, error = null) }
            val state = _viewState.value
            try {
                val newItems = fetchMediaPage(isMovies = state.areMoviesSelected, pageNumber = 1, state.selectedTab)
                _viewState.update {
                    it.copy(
                        mediaList = newItems,
                        pageNumber = 1,
                        isLoadingFirstPage = false
                    )
                }
            } catch (_: Exception) {
                _viewState.update { it.copy(isLoadingFirstPage = false, error = "data fetching error") }
            }
        }
    }

    fun loadNextPage() {
        val state = _viewState.value
        if (state.isLoadingNextPage || state.isLoadingFirstPage) return
        viewModelScope.launch {
            _viewState.update { it.copy(isLoadingNextPage = true) }
            val nextPage = state.pageNumber + 1
            try {
                val newItems = fetchMediaPage(isMovies = state.areMoviesSelected, pageNumber = nextPage, state.selectedTab)
                _viewState.update {
                    it.copy(
                        mediaList = it.mediaList + newItems,
                        pageNumber = nextPage,
                        isLoadingNextPage = false
                    )
                }
            } catch (_: Exception) {
                _viewState.update { it.copy(isLoadingNextPage = false, error = "unable to load more data") }
            }
        }
    }

    fun toggleMediaType(showMovies: Boolean) {
        if (_viewState.value.areMoviesSelected == showMovies) return
        _viewState.update {
            it.copy(
                areMoviesSelected = showMovies,
                selectedTab = "popular", // ZABEZPIECZENIE: Resetujemy zakładkę na domyślną przy zmianie trybu
                pageNumber = 1,
                mediaList = emptyList(),
                isLoadingFirstPage = true
            )
        }
        loadInitialData()
    }

    // NOWA METODA: Obsługa wyboru zakładki
    fun setSelectedTab(tabValue: String) {
        if (_viewState.value.selectedTab == tabValue) return // Ignorujemy kliknięcie w już aktywną zakładkę

        _viewState.update {
            it.copy(
                selectedTab = tabValue,
                pageNumber = 1, // Reset paginacji
                mediaList = emptyList(), // Czyszczenie widoku przed załadowaniem nowych danych
                isLoadingFirstPage = true
            )
        }
        loadInitialData()
    }

    private suspend fun fetchMediaPage(isMovies: Boolean, pageNumber: Int, pageType: String): List<MediaOverview> {
        return if (isMovies) {
            val response = WatchAppRepository.Movies.getMoviePage(pageNumber = pageNumber, listType = pageType)
            response?.results?.map { it.toDomain() } ?: emptyList()
        } else {
            val response = WatchAppRepository.TvSeries.getTvSeriesPage(pageNumber = pageNumber, listType = pageType)
            response?.results?.map { it.toDomain() } ?: emptyList()
        }
    }
}