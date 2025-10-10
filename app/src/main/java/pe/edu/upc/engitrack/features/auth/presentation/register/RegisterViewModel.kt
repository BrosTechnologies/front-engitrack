package pe.edu.upc.engitrack.features.auth.presentation.register

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
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    
    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()
    
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    
    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()
    
    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()
    
    private val _selectedRole = MutableStateFlow("SUPERVISOR")
    val selectedRole: StateFlow<String> = _selectedRole.asStateFlow()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    
    val roleOptions = listOf("SUPERVISOR", "CONTRACTOR", "WORKER")

    fun updateFullName(value: String) {
        _fullName.value = value
        clearError()
    }

    fun updateEmail(value: String) {
        _email.value = value
        clearError()
    }
    
    fun updatePhone(value: String) {
        _phone.value = value
        clearError()
    }

    fun updatePassword(value: String) {
        _password.value = value
        clearError()
    }
    
    fun updateConfirmPassword(value: String) {
        _confirmPassword.value = value
        clearError()
    }
    
    fun updateSelectedRole(role: String) {
        _selectedRole.value = role
        clearError()
    }

    fun register() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            repository.register(
                email = email.value,
                fullName = fullName.value,
                phone = phone.value,
                role = selectedRole.value,
                password = password.value
            )
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = user,
                        isRegistrationSuccessful = true
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al registrar usuario"
                    )
                }
        }
    }
    
    private fun validateInputs(): Boolean {
        when {
            fullName.value.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "El nombre completo es requerido")
                return false
            }
            email.value.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "El email es requerido")
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Email inválido")
                return false
            }
            phone.value.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "El teléfono es requerido")
                return false
            }
            selectedRole.value.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Debe seleccionar un rol")
                return false
            }
            password.value.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "La contraseña es requerida")
                return false
            }
            password.value.length < 6 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres")
                return false
            }
            confirmPassword.value != password.value -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Las contraseñas no coinciden")
                return false
            }
        }
        return true
    }
    
    private fun clearError() {
        if (_uiState.value.errorMessage != null) {
            _uiState.value = _uiState.value.copy(errorMessage = null)
        }
    }
}

data class RegisterUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val isRegistrationSuccessful: Boolean = false,
    val errorMessage: String? = null
)

