package com.jsdr.watchapp.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsdr.watchapp.data.firebase.WatchAppFirestore
import com.jsdr.watchapp.data.repository.WatchAppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class AuthUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val birthYear: String = "",
    val gender: String = "Pan",
    val isLoading: Boolean = false,
    val usernameError: String? = null,
    val emailError: String? = null,
    val birthYearError: String? = null,
    val passwordError: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun updateUsername(value: String) {

        _uiState.value =
            _uiState.value.copy(
                username = value
            )

        viewModelScope.launch {

            val usernames =
                WatchAppFirestore.Users.getUsernames()

            val duplicate =
                usernames.any {
                    it.equals(value.trim(), ignoreCase = true)
                }

            _uiState.value =
                _uiState.value.copy(
                    usernameError =
                        if (duplicate)
                            "Użytkownik o takim nicku już istnieje"
                        else
                            null
                )
        }
    }
    fun updateEmail(value: String) {

        val isValidEmail =
            android.util.Patterns.EMAIL_ADDRESS
                .matcher(value)
                .matches()

        _uiState.value =
            _uiState.value.copy(
                email = value,
                emailError =
                    when {
                        value.isBlank() ->
                            null

                        !isValidEmail ->
                            "Nieprawidłowy adres email"

                        else ->
                            null
                    }
            )
    }

    fun updatePassword(value: String) {

        _uiState.value =
            _uiState.value.copy(
                password = value,
                passwordError =
                    if (
                        _uiState.value.repeatPassword.isNotEmpty() &&
                        value != _uiState.value.repeatPassword
                    )
                        "Hasła nie są takie same"
                    else
                        null
            )
    }

    fun updateRepeatPassword(value: String) {

        _uiState.value =
            _uiState.value.copy(
                repeatPassword = value,
                passwordError =
                    if (
                        _uiState.value.password.isNotEmpty() &&
                        value != _uiState.value.password
                    )
                        "Hasła nie są takie same"
                    else
                        null
            )
    }

    fun updateBirthYear(value: String) {

        val year = value.toIntOrNull()

        _uiState.value =
            _uiState.value.copy(
                birthYear = value,
                birthYearError =
                    when {
                        year == null && value.isNotEmpty() ->
                            "Nieprawidłowy rok"

                        year != null &&
                                Calendar.getInstance().get(Calendar.YEAR) - year < 18 ->
                            "Musisz mieć ukończone 18 lat"

                        else -> null
                    }
            )
    }

    fun updateGender(value: String) {
        _uiState.value = _uiState.value.copy(gender = value)
    }
    fun login() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    emailError = null,
                    passwordError = null
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
                    emailError = result.exceptionOrNull()?.message
                )
        }
    }
    fun register() {

        viewModelScope.launch {

            val state = _uiState.value
            val existingUsernames =
                WatchAppFirestore.Users.getUsernames()

            val isUsernameDuplicate =
                existingUsernames.any {
                    it.equals(state.username.trim(), ignoreCase = true)
                }

            if (isUsernameDuplicate) {

                _uiState.value =
                    state.copy(
                        usernameError = "Użytkownik o takim nicku już istnieje"
                    )

                return@launch
            }

            if (state.password != state.repeatPassword) {

                _uiState.value =
                    state.copy(
                        passwordError = "Hasła nie są takie same"
                    )

                return@launch
            }

            val birthYear =
                state.birthYear.toIntOrNull()

            if (birthYear == null) {

                _uiState.value =
                    state.copy(
                        birthYearError = "Musisz mieć ukończone 18 lat"
                    )

                return@launch
            }

            val currentYear =
                Calendar.getInstance().get(Calendar.YEAR)

            if (currentYear - birthYear < 18) {

                _uiState.value =
                    state.copy(
                        birthYearError = "Musisz mieć ukończone 18 lat"
                    )

                return@launch
            }

            _uiState.value =
                state.copy(
                    isLoading = true,
                    usernameError = null,
                    emailError = null,
                    birthYearError = null,
                    passwordError = null
                )

            val result =
                WatchAppRepository.Auth.registerUser(
                    email = state.email,
                    password = state.password,
                    username = state.username,
                    birthYear = birthYear,
                    gender = state.gender
                )

            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    isSuccess = result.isSuccess,
                    emailError = result.exceptionOrNull()?.message
                )
        }
    }
}