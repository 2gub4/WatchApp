package com.jsdr.watchapp.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsdr.watchapp.data.repository.WatchAppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val birthYear: String = "",
    val gender: String = "Pan",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun updatePassword(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun updateRepeatPassword(value: String) {
        _uiState.value = _uiState.value.copy(repeatPassword = value)
    }

    fun updateBirthYear(value: String) {
        _uiState.value = _uiState.value.copy(birthYear = value)
    }

    fun updateGender(value: String) {
        _uiState.value = _uiState.value.copy(gender = value)
    }
    fun login() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

            val result =
                WatchAppRepository.Auth.signIn(
                    _uiState.value.email,
                    _uiState.value.password
                )

            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    isSuccess = result.isSuccess,
                    errorMessage = result.exceptionOrNull()?.message
                )
        }
    }
    fun register() {

        viewModelScope.launch {

            val state = _uiState.value

            if (state.password != state.repeatPassword) {

                _uiState.value =
                    state.copy(
                        errorMessage = "Hasła nie są takie same"
                    )

                return@launch
            }

            val birthYear =
                state.birthYear.toIntOrNull()

            if (birthYear == null) {

                _uiState.value =
                    state.copy(
                        errorMessage = "Nieprawidłowy rok urodzenia"
                    )

                return@launch
            }

            val currentYear =
                Calendar.getInstance().get(Calendar.YEAR)

            if (currentYear - birthYear < 18) {

                _uiState.value =
                    state.copy(
                        errorMessage = "Musisz mieć ukończone 18 lat"
                    )

                return@launch
            }

            _uiState.value =
                state.copy(
                    isLoading = true,
                    errorMessage = null
                )

            val result =
                WatchAppRepository.Auth.registerUser(
                    email = state.email,
                    password = state.password,
                    username = state.email.substringBefore("@")
                )

            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    isSuccess = result.isSuccess,
                    errorMessage = result.exceptionOrNull()?.message
                )
        }
    }
}