package pe.edu.upc.engitrack.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.engitrack.features.profile.data.remote.ProfileApiService
import pe.edu.upc.engitrack.features.profile.domain.models.UpdateProfileRequest
import pe.edu.upc.engitrack.features.profile.domain.models.UserProfile
import javax.inject.Inject

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val isUpdateSuccess: Boolean = false,
    val userProfile: UserProfile? = null,
    val error: String? = null,
    val updateError: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val profileApiService: ProfileApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()
    
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
    
    fun updateProfile(fullName: String, phone: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true, updateError = null)
            
            try {
                val request = UpdateProfileRequest(
                    fullName = fullName,
                    phone = phone
                )
                
                val response = profileApiService.updateUserProfile(request)
                
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        isUpdateSuccess = true,
                        updateError = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        updateError = "Error al actualizar perfil: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUpdating = false,
                    updateError = "Error de conexión: ${e.message}"
                )
            }
        }
    }
}