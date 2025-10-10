package pe.edu.upc.engitrack.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.engitrack.features.auth.domain.models.User
import pe.edu.upc.engitrack.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(value: String) {
        _email.value = value
        clearError()
    }

    fun updatePassword(value: String) {
        _password.value = value
        clearError()
    }

    fun login() {
        if (email.value.isBlank() || password.value.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Por favor completa todos los campos"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            repository.login(email.value, password.value)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = user,
                        isLoginSuccessful = true
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al iniciar sesión"
                    )
                }
        }
    }
    
    private fun clearError() {
        if (_uiState.value.errorMessage != null) {
            _uiState.value = _uiState.value.copy(errorMessage = null)
        }
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val isLoginSuccessful: Boolean = false,
    val errorMessage: String? = null
)