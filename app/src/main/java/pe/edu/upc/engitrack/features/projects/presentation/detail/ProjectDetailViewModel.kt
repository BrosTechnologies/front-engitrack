package pe.edu.upc.engitrack.features.projects.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.engitrack.features.projects.domain.models.CreateTaskRequest
import pe.edu.upc.engitrack.features.projects.domain.models.Project
import pe.edu.upc.engitrack.features.projects.domain.repositories.ProjectRepository
import javax.inject.Inject

data class ProjectDetailUiState(
    val project: Project? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCreatingTask: Boolean = false,
    val isDeletingTask: Boolean = false,
    val isUpdatingStatus: Boolean = false,
    val isCompletingProject: Boolean = false,
    val operationSuccess: Boolean = false
)

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()
    
    fun loadProject(projectId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            projectRepository.getProjectById(projectId).fold(
                onSuccess = { project ->
                    _uiState.value = _uiState.value.copy(
                        project = project,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al cargar proyecto: ${exception.message}"
                    )
                }
            )
        }
    }
    
    fun createTask(projectId: String, title: String, dueDate: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingTask = true, error = null)
            
            val taskRequest = CreateTaskRequest(title = title, dueDate = dueDate)
            
            projectRepository.createTask(projectId, taskRequest).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isCreatingTask = false,
                        operationSuccess = true
                    )
                    // Recargar el proyecto para obtener las tareas actualizadas
                    loadProject(projectId)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isCreatingTask = false,
                        error = "Error al crear tarea: ${exception.message}"
                    )
                }
            )
        }
    }
    
    fun updateTaskStatus(projectId: String, taskId: String, newStatus: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingStatus = true, error = null)
            
            projectRepository.updateTaskStatus(projectId, taskId, newStatus).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isUpdatingStatus = false,
                        operationSuccess = true
                    )
                    // Recargar el proyecto para obtener las tareas actualizadas
                    loadProject(projectId)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isUpdatingStatus = false,
                        error = "Error al actualizar estado: ${exception.message}"
                    )
                }
            )
        }
    }
    
    fun deleteTask(projectId: String, taskId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeletingTask = true, error = null)
            
            projectRepository.deleteTask(projectId, taskId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isDeletingTask = false,
                        operationSuccess = true
                    )
                    // Recargar el proyecto para obtener las tareas actualizadas
                    loadProject(projectId)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isDeletingTask = false,
                        error = "Error al eliminar tarea: ${exception.message}"
                    )
                }
            )
        }
    }
    
    fun completeProject(projectId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCompletingProject = true, error = null)
            
            projectRepository.completeProject(projectId).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isCompletingProject = false,
                        operationSuccess = true
                    )
                    // Recargar el proyecto para obtener el estado actualizado
                    loadProject(projectId)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isCompletingProject = false,
                        error = "Error al completar proyecto: ${exception.message}"
                    )
                }
            )
        }
    }
    
    fun resetOperationSuccess() {
        _uiState.value = _uiState.value.copy(operationSuccess = false)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
