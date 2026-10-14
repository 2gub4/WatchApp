package com.jsdr.watchapp.ui.screens.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsdr.watchapp.data.models.entities.UserList
import com.jsdr.watchapp.data.repository.WatchAppRepository
import com.jsdr.watchapp.domain.models.MediaOverview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ListDetailsViewState(
    val isLoading: Boolean = false,
    val listInfo: UserList? = null,
    val mediaItems: List<MediaOverview> = emptyList(),
    val watchedMovieIds: Set<Int> = emptySet(),
    val watchedSeriesIds: Set<Int> = emptySet(),
    val error: String? = null
)

class ListDetailsViewModel : ViewModel() {
    private val _viewState = MutableStateFlow(ListDetailsViewState())
    val viewState: StateFlow<ListDetailsViewState> = _viewState.asStateFlow()

    fun loadListDetails(listName: String) {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(isLoading = true, error = null)
            try {
                val allLists = WatchAppRepository.Lists.getUserLists()
                val fullList = allLists.find { it.name.equals(listName, ignoreCase = true) }
                val watchedList = allLists.find { it.id == "watched" }

                if (fullList != null) {
                    val mediaItems = WatchAppRepository.Lists.getMediaOverviewsForList(fullList)

                    _viewState.value = _viewState.value.copy(
                        isLoading = false,
                        listInfo = fullList,
                        mediaItems = mediaItems,
                        watchedMovieIds = watchedList?.movies?.keys?.map { it.toInt() }.orEmpty().toSet(),
                        watchedSeriesIds = watchedList?.series?.keys?.map { it.toInt() }.orEmpty().toSet()
                    )
                } else {
                    _viewState.value = _viewState.value.copy(isLoading = false, error = "Brak listy")
                }
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun toggleWatchedStatus(mediaId: Int, isMovie: Boolean) {
        val isWatched = if (isMovie) _viewState.value.watchedMovieIds.contains(mediaId)
        else _viewState.value.watchedSeriesIds.contains(mediaId)

        viewModelScope.launch {
            try {
                if (isWatched) {
                    WatchAppRepository.Lists.removeMediaFromList("watched", mediaId, isMovie)
                    if (isMovie) _viewState.value = _viewState.value.copy(watchedMovieIds = _viewState.value.watchedMovieIds - mediaId)
                    else _viewState.value = _viewState.value.copy(watchedSeriesIds = _viewState.value.watchedSeriesIds - mediaId)
                } else {
                    WatchAppRepository.addMediaToList("watched", mediaId, isMovie)
                    if (isMovie) _viewState.value = _viewState.value.copy(watchedMovieIds = _viewState.value.watchedMovieIds + mediaId)
                    else _viewState.value = _viewState.value.copy(watchedSeriesIds = _viewState.value.watchedSeriesIds + mediaId)
                }
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(error = e.message)
            }
        }
    }

    fun removeMediaFromList(mediaId: Int, isMovie: Boolean) {
        val currentList = _viewState.value.listInfo ?: return
        viewModelScope.launch {
            try {
                WatchAppRepository.Lists.removeMediaFromList(currentList.id, mediaId, isMovie)
                val updatedMedia = _viewState.value.mediaItems.filterNot { it.id == mediaId && it.isMovie == isMovie }
                _viewState.value = _viewState.value.copy(mediaItems = updatedMedia)
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(error = e.message)
            }
        }
    }
}