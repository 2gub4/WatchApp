package com.jsdr.watchapp.ui.screens.media

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsdr.watchapp.data.repository.WatchAppRepository
import com.jsdr.watchapp.domain.models.profiles.MovieProfile
import com.jsdr.watchapp.domain.models.profiles.TvSeriesProfile
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

sealed interface MediaDetailsUiState {
    object Loading : MediaDetailsUiState
    data class MovieSuccess(val profile: MovieProfile) : MediaDetailsUiState
    data class TvSuccess(val profile: TvSeriesProfile) : MediaDetailsUiState
    data class Error(val message: String) : MediaDetailsUiState
}

class MediaDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MediaDetailsUiState>(MediaDetailsUiState.Loading)
    val uiState: StateFlow<MediaDetailsUiState> = _uiState.asStateFlow()

    fun loadProfile(mediaId: Int, isMovie: Boolean) {
        viewModelScope.launch {
            _uiState.value = MediaDetailsUiState.Loading
            try {
                withTimeout(10_000L) {
                    if (isMovie) {
                        Log.d("MediaDetails", "trying to get details of movie with id: $mediaId")
                        val profile = WatchAppRepository.Movies.getMovieProfile(mediaId)
                        if (profile != null) {
                            Log.d("MediaDetails", "successfully received movie data: ${profile.movieDetails.title}")
                            _uiState.value = MediaDetailsUiState.MovieSuccess(profile)
                        } else {
                            Log.e("MediaDetails", "no data for movie.")
                            _uiState.value = MediaDetailsUiState.Error("Nie udało się pobrać filmu.")
                        }
                    } else {
                        Log.d("MediaDetails", "trying to get details of tv show with id: $mediaId")
                        val profile = WatchAppRepository.TvSeries.getTvSeriesProfile(mediaId)
                        if (profile != null) {
                            Log.d("MediaDetails", "successfully received movie data: ${profile.seriesDetails.title}")
                            _uiState.value = MediaDetailsUiState.TvSuccess(profile)
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
}