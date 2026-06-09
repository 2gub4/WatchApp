package com.jsdr.watchapp.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsdr.watchapp.data.repository.WatchAppRepository
import com.jsdr.watchapp.domain.models.MediaOverview
import com.jsdr.watchapp.domain.models.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate


data class HomeViewState(
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
    private val todaysDate = LocalDate.now()
    val viewState: StateFlow<HomeViewState> = _viewState.asStateFlow()

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoadingFirstPage = true, error = null) }
            val state = _viewState.value
            try {
                var newItems = fetchMediaPage(isMovies = state.areMoviesSelected, pageNumber = 1, state.selectedTab)
                Log.d("HomeViewModelDebug", "Pobrano z API elementów: ${newItems.size}")
                if (state.selectedTab == "upcoming") {
                    newItems = newItems.filter { media ->
                        if (media.releaseDate.isNullOrEmpty()) {
                            false
                        } else {
                            try {
                                val parsedDate = LocalDate.parse(media.releaseDate)
                                val isFuture = parsedDate.isAfter(todaysDate)
                                isFuture /*&& media.originCountry in listOf("pl", "us", "gb")*/
                            } catch (_: Exception) {
                                false
                            }
                        }
                    }
                }
                _viewState.update {
                    it.copy(
                        mediaList = newItems,
                        pageNumber = 1,
                        isLoadingFirstPage = false
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModelDebug", "Data fetching issue", e)
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
                var newItems = fetchMediaPage(isMovies = state.areMoviesSelected, pageNumber = nextPage, state.selectedTab)
                if (state.selectedTab == "upcoming") {
                    newItems = newItems.filter { media ->
                        if (media.releaseDate.isNullOrEmpty()) {
                            false
                        } else {
                            try {
                                val parsedDate = LocalDate.parse(media.releaseDate)
                                parsedDate.isAfter(todaysDate)  /*&& media.originCountry in listOf("pl", "us", "gb")*/
                            } catch (_: Exception) {
                                false
                            }
                        }
                    }
                }
                _viewState.update {
                    it.copy(
                        mediaList = it.mediaList + newItems,
                        pageNumber = nextPage,
                        isLoadingNextPage = false
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching next page", e)
                _viewState.update { it.copy(isLoadingNextPage = false, error = "unable to load more data") }
            }
        }
    }

    fun toggleMediaType(showMovies: Boolean) {
        if (_viewState.value.areMoviesSelected == showMovies) return
        _viewState.update {
            it.copy(
                areMoviesSelected = showMovies,
                selectedTab = "popular",
                pageNumber = 1,
                mediaList = emptyList(),
                isLoadingFirstPage = true
            )
        }
        loadData()
    }
    fun setSelectedTab(tabValue: String) {
        if (_viewState.value.selectedTab == tabValue) return
        _viewState.update {
            it.copy(
                selectedTab = tabValue,
                pageNumber = 1,
                mediaList = emptyList(),
                isLoadingFirstPage = true
            )
        }
        loadData()
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