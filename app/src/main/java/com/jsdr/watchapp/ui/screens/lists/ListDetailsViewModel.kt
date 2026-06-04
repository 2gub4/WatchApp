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
    val error: String? = null
)

class ListDetailsViewModel : ViewModel() {
    private val _viewState = MutableStateFlow(ListDetailsViewState())
    val viewState: StateFlow<ListDetailsViewState> = _viewState.asStateFlow()

    fun loadListDetails(listName: String) {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(isLoading = true, error = null)
            try {
                val fullList = WatchAppRepository.Lists.getListByName(listName)
                if (fullList != null) {
                    val media = WatchAppRepository.Lists.getMediaOverviewsForList(fullList)
                    _viewState.value = _viewState.value.copy(
                        isLoading = false,
                        listInfo = fullList,
                        mediaItems = media
                    )
                } else {
                    _viewState.value = _viewState.value.copy(
                        isLoading = false,
                        error = "Nie odnaleziono listy o nazwie: $listName"
                    )
                }
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(isLoading = false, error = e.message)
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