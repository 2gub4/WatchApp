package com.jsdr.watchapp.ui.screens.media

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsdr.watchapp.data.models.entities.Rating
import com.jsdr.watchapp.data.models.entities.UserList
import com.jsdr.watchapp.data.repository.WatchAppRepository
import com.jsdr.watchapp.domain.models.profiles.MovieProfile
import com.jsdr.watchapp.domain.models.profiles.TvSeriesProfile
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

sealed interface MediaDetailsUiState {
    object Loading : MediaDetailsUiState
    data class MovieSuccess(val profile: MovieProfile) : MediaDetailsUiState
    data class TvSuccess(val profile: TvSeriesProfile) : MediaDetailsUiState
    data class Error(val message: String) : MediaDetailsUiState
}

data class SelectableList(
    val listId: String,
    val name: String,
    val containsMedia: Boolean
)

data class UserMediaInteractionState(
    val isFavorite: Boolean = false,
    val isWatched: Boolean = false,
    val isInBucketlist: Boolean = false,
    val customLists: List<SelectableList> = emptyList()
)

data class RatingInteractionState(
    val review: String = "",
    // dokończ dominiś
)

class MediaDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MediaDetailsUiState>(MediaDetailsUiState.Loading)
    val uiState: StateFlow<MediaDetailsUiState> = _uiState.asStateFlow()

    private val _interactionState = MutableStateFlow(UserMediaInteractionState())
    val interactionState: StateFlow<UserMediaInteractionState> = _interactionState.asStateFlow()

    private val _userRating = MutableStateFlow<Rating?>(null)
    val userRating: StateFlow<Rating?> = _userRating.asStateFlow()

    fun loadProfile(mediaId: Int, isMovie: Boolean, showLoadingLayout: Boolean = true) {
        viewModelScope.launch {
            if (showLoadingLayout) {
                _uiState.value = MediaDetailsUiState.Loading
            }
            try {
                withTimeout(10_000L) {
                    val allUserLists = WatchAppRepository.Lists.getUserLists()

                    _userRating.value =
                        WatchAppRepository.Ratings.getRating(
                            mediaId,
                            isMovie
                        )

                    if (isMovie) {
                        Log.d("MediaDetails", "trying to get details of movie with id: $mediaId")
                        val profile = WatchAppRepository.Movies.getMovieProfile(mediaId)
                        if (profile != null) {
                            _uiState.value = MediaDetailsUiState.MovieSuccess(profile)
                            updateInteractionState(profile.containingLists, allUserLists)
                        } else {
                            Log.e("MediaDetails", "no data for movie.")
                            _uiState.value = MediaDetailsUiState.Error("Nie udało się pobrać filmu.")
                        }
                    } else {
                        Log.d("MediaDetails", "trying to get details of tv show with id: $mediaId")
                        val profile = WatchAppRepository.TvSeries.getTvSeriesProfile(mediaId)
                        if (profile != null) {
                            _uiState.value = MediaDetailsUiState.TvSuccess(profile)
                            updateInteractionState(profile.containingLists, allUserLists)
                        } else {
                            Log.e("MediaDetails", "no data for tv series.")
                            _uiState.value = MediaDetailsUiState.Error("Nie udało się pobrać serialu.")
                        }
                    }
                }
            } catch (_: TimeoutCancellationException) {
                Log.e("MediaDetails", "Timeout! ")
                _uiState.value = MediaDetailsUiState.Error("Przekroczono czas oczekiwania")
            } catch (e: Exception) {
                Log.e("MediaDetails", "Critical error: ${e.message}", e)
                _uiState.value = MediaDetailsUiState.Error("Błąd: ${e.localizedMessage}")
            }
        }
    }

    private fun updateInteractionState(
        containingListsIds: List<String>,
        allUserLists: List<UserList>
    ) {
        val safeIds = containingListsIds.map { it.trim().lowercase() }
        val isFav = safeIds.any { it.contains("favourites") }
        val isWat = safeIds.any { it.contains("watched") }
        val isBuc = safeIds.any { it.contains("bucketlist") }
        val systemIdsOrNames = setOf("favourites", "watched", "bucketlist")
        val customListsMapped = allUserLists.filter {
            it.id.lowercase() !in systemIdsOrNames && it.name.lowercase() !in systemIdsOrNames
        }.map { userList ->
            val cleanListId = userList.id.trim().lowercase()
            SelectableList(
                listId = userList.id,
                name = userList.name,
                containsMedia = safeIds.any { it.contains(cleanListId) }
            )
        }
        _interactionState.value = UserMediaInteractionState(
            isFavorite = isFav,
            isWatched = isWat,
            isInBucketlist = isBuc,
            customLists = customListsMapped
        )
    }

    fun toggleListStatus(listId: String, currentStatus: Boolean, mediaId: Int, isMovie: Boolean) {
        val newStatus = !currentStatus

        _interactionState.value = _interactionState.value.let { currentState ->
            val updatedCustomLists = currentState.customLists.map { customList ->
                if (customList.listId == listId) {
                    customList.copy(containsMedia = newStatus)
                } else { customList }
            }
            val isAddingToWatched = listId.equals("watched", ignoreCase = true) && newStatus
            currentState.copy(
                isFavorite = if (listId.equals("favourites", ignoreCase = true)) newStatus else currentState.isFavorite,
                isWatched = if (listId.equals("watched", ignoreCase = true)) newStatus else currentState.isWatched,
                isInBucketlist = if (isAddingToWatched) false
                else if (listId.equals("bucketlist", ignoreCase = true)) newStatus
                else currentState.isInBucketlist,
                customLists = updatedCustomLists
            )
        }
        viewModelScope.launch {
            try {
                if (currentStatus) {
                    WatchAppRepository.Lists.removeMediaFromList(listId, mediaId, isMovie)
                } else {
                    WatchAppRepository.addMediaToList(listId, mediaId, isMovie)
                }
            } catch (e: Exception) {
                Log.e("MediaDetailsViewModel", "could not update list status witch listID: $listId", e)
            }
        }
    }

    fun saveRating(
        mediaId: Int,
        isMovie: Boolean,
        newRating: com.jsdr.watchapp.data.models.entities.Rating
    ) {
        viewModelScope.launch {
            try {
                WatchAppRepository.Ratings.saveRating(
                    mediaId,
                    isMovie,
                    newRating
                )

                _userRating.value = newRating

            } catch (e: Exception) {
                Log.e("MediaDetails", "Could not save rating", e)
            }
        }
    }
}