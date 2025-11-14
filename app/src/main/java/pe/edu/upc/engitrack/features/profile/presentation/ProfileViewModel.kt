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
    val error: String? = null,
    val projectsCount: Int = 0,
    val tasksCount: Int = 0,
    val completedTasksCount: Int = 0,
    val isLoadingStats: Boolean = false,
    val statsError: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileApiService: ProfileApiService,
    private val authManager: AuthManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadUserProfile()
    }
    
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
    
    fun loadUserStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingStats = true, statsError = null)
            
            try {
                val response = profileApiService.getUserStats()
                if (response.isSuccessful && response.body() != null) {
                    val stats = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        isLoadingStats = false,
                        projectsCount = stats.projectsCount,
                        tasksCount = stats.tasksCount,
                        completedTasksCount = stats.completedTasksCount,
                        statsError = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoadingStats = false,
                        statsError = "Error al cargar estadísticas: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingStats = false,
                    statsError = "Error al cargar estadísticas: ${e.message}"
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