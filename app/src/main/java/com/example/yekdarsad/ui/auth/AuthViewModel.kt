package com.example.yekdarsad.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yekdarsad.data.sync.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val message: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(
            isLoggedIn = repository.isLoggedIn()
        )
    )

    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(
        email: String,
        password: String
    ) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "ایمیل و رمز عبور را وارد کنید.",
                message = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                message = null
            )

            try {
                repository.signIn(email, password)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = repository.isLoggedIn(),
                    errorMessage = null,
                    message = "ورود با موفقیت انجام شد."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = false,
                    errorMessage = e.message ?: "ورود ناموفق بود.",
                    message = null
                )
            }
        }
    }

    fun signUp(
        email: String,
        password: String
    ) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "ایمیل و رمز عبور را وارد کنید.",
                message = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                message = null
            )

            try {
                repository.signUp(email, password)

                val loggedIn = repository.isLoggedIn()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = loggedIn,
                    errorMessage = null,
                    message = if (loggedIn) {
                        "ثبت‌نام با موفقیت انجام شد."
                    } else {
                        "ثبت‌نام انجام شد. ایمیل خود را برای تأیید بررسی کنید."
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = false,
                    errorMessage = e.message ?: "ثبت‌نام ناموفق بود.",
                    message = null
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                repository.signOut()

                _uiState.value = AuthUiState(
                    isLoading = false,
                    isLoggedIn = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "خروج ناموفق بود."
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            message = null
        )
    }
}