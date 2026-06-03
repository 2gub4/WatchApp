package com.jsdr.watchapp.ui.screens.user_profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsdr.watchapp.data.firebase.WatchAppFirestore
import com.jsdr.watchapp.data.models.entities.User
import com.jsdr.watchapp.data.repository.WatchAppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserStatsState(
    val watchedMovies: Int = 0,
    val watchedSeries: Int = 0,
    val totalFavourites: Int = 0,
    val totalRatings: Int = 0,
    val totalLists: Int = 0,
    val averageRating: Double = 0.0,
    val isLoading: Boolean = true
)

class UserProfileViewModel : ViewModel() {

    private val _statsState = MutableStateFlow(UserStatsState())
    val statsState: StateFlow<UserStatsState> = _statsState.asStateFlow()

    private val _userProfileState = MutableStateFlow(User())
    val userProfileState: StateFlow<User> = _userProfileState.asStateFlow()

    init {
        fetchUserProfile()
        fetchUserStats()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                val user = WatchAppRepository.User.getUser()
                if (user != null) {
                    _userProfileState.value = user
                }
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Błąd podczas pobierania profilu", e)
            }
        }
    }

    fun fetchUserStats() {
        viewModelScope.launch {
            _statsState.update { it.copy(isLoading = true) }
            try {
                val statsMap = WatchAppRepository.User.getUserStats()

                _statsState.update {
                    it.copy(
                        watchedMovies = statsMap["watchedMovies"]?.toInt() ?: 0,
                        watchedSeries = statsMap["watchedSeries"]?.toInt() ?: 0,
                        totalFavourites = statsMap["totalFavourites"]?.toInt() ?: 0,
                        totalRatings = statsMap["totalRatings"]?.toInt() ?: 0,
                        totalLists = statsMap["totalLists"]?.toInt()  ?: 0,
                        averageRating = statsMap["averageRating"] ?: 0.0,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Błąd podczas pobierania statystyk", e)
                _statsState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateUserField(subject: String, newValue: String) {
        viewModelScope.launch {
            try {
                WatchAppRepository.User.update(subject, newValue)

                _userProfileState.update { currentUser ->
                    when (subject) {
                        "username" -> currentUser.copy(username = newValue)
                        "email" -> currentUser.copy(email = newValue)
                        "gender" -> currentUser.copy(gender = newValue)
                        "birthYear" -> currentUser.copy(birthYear = newValue.toIntOrNull() ?: currentUser.birthYear)
                        else -> currentUser
                    }
                }
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Błąd podczas aktualizacji pola: $subject", e)
            }
        }
    }
}