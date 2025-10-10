package pe.edu.upc.engitrack.features.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.engitrack.core.auth.AuthManager
import pe.edu.upc.engitrack.features.projects.domain.models.Project
import pe.edu.upc.engitrack.features.projects.domain.models.Task
import pe.edu.upc.engitrack.features.projects.domain.repositories.ProjectRepository
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            android.util.Log.d("DashboardViewModel", "Loading dashboard data...")
            
            projectRepository.getProjects(status = "ACTIVE")
                .onSuccess { projects ->
                    android.util.Log.d("DashboardViewModel", "Projects loaded successfully: ${projects.size} projects")
                    projects.forEach { project ->
                        android.util.Log.d("DashboardViewModel", "Project: ${project.name}, Tasks: ${project.tasks.size}")
                    }
                    
                    val today = getCurrentDateString()
                    val todayTasks = projects.flatMap { project ->
                        project.tasks.filter { task ->
                            task.dueDate == today
                        }
                    }
                    
                    android.util.Log.d("DashboardViewModel", "Today's tasks: ${todayTasks.size}")
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        activeProjects = projects.take(5), // Mostrar solo los primeros 5
                        todayTasks = todayTasks,
                        errorMessage = null
                    )
                }
                .onFailure { exception ->
                    android.util.Log.e("DashboardViewModel", "Error loading projects", exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar datos"
                    )
                }
        }
    }

    fun toggleTaskStatus(projectId: String, taskId: String) {
        viewModelScope.launch {
            val currentTask = _uiState.value.todayTasks.find { it.taskId == taskId }
            val newStatus = if (currentTask?.status == "DONE") "PENDING" else "DONE"
            
            projectRepository.updateTaskStatus(projectId, taskId, newStatus)
                .onSuccess {
                    // Recargar datos
                    loadDashboardData()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = exception.message ?: "Error al actualizar tarea"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    private fun getCurrentDateString(): String {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH) + 1
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        
        return String.format("%04d-%02d-%02d", year, month, day)
    }
}

data class DashboardUiState(
    val isLoading: Boolean = false,
    val activeProjects: List<Project> = emptyList(),
    val todayTasks: List<Task> = emptyList(),
    val errorMessage: String? = null
)