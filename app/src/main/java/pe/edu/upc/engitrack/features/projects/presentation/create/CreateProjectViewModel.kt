package pe.edu.upc.engitrack.features.projects.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.engitrack.features.projects.data.remote.ProjectApiService
import pe.edu.upc.engitrack.features.projects.domain.models.CreateProjectRequest
import pe.edu.upc.engitrack.features.projects.domain.models.CreateTaskRequest
import javax.inject.Inject

data class CreateProjectUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CreateProjectViewModel @Inject constructor(
    private val projectApiService: ProjectApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CreateProjectUiState())
    val uiState: StateFlow<CreateProjectUiState> = _uiState.asStateFlow()
    
    fun createProject(
        name: String,
        description: String?,
        startDate: String,
        endDate: String,
        budget: Double = 0.0,
        priority: Int = 1, // 0=LOW, 1=MEDIUM, 2=HIGH
        ownerUserId: String,
        tasks: List<CreateTaskRequest> = emptyList()
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val request = CreateProjectRequest(
                    name = name,
                    description = description,
                    startDate = startDate,
                    endDate = endDate,
                    budget = budget,
                    priority = priority,
                    ownerUserId = ownerUserId,
                    tasks = tasks
                )
                
                val response = projectApiService.createProject(request)
                
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al crear proyecto: Código ${response.code()}"
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
    
    fun resetState() {
        _uiState.value = CreateProjectUiState()
    }
}