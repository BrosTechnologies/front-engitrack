package pe.edu.upc.engitrack.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.engitrack.features.profile.data.remote.ProfileApiService
import pe.edu.upc.engitrack.core.auth.AuthManager
import pe.edu.upc.engitrack.features.profile.domain.models.UserProfile
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileApiService: ProfileApiService,
    private val authManager: AuthManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val response = profileApiService.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    val userProfile = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userProfile = userProfile,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al cargar el perfil: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authManager.logout()
        }
    }
}