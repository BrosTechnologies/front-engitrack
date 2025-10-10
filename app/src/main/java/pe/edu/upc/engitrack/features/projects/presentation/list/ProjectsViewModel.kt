package pe.edu.upc.engitrack.features.projects.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.engitrack.features.projects.domain.models.Project
import pe.edu.upc.engitrack.features.projects.domain.repositories.ProjectRepository
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState.asStateFlow()

    fun loadProjects() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            android.util.Log.d("ProjectsViewModel", "Loading projects...")
            
            projectRepository.getProjects()
                .onSuccess { projects ->
                    android.util.Log.d("ProjectsViewModel", "Projects loaded successfully: ${projects.size} projects")
                    projects.forEach { project ->
                        android.util.Log.d("ProjectsViewModel", "Project: ${project.name}, Status: ${project.status}")
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        allProjects = projects,
                        filteredProjects = filterProjects(projects, _uiState.value.selectedFilter),
                        errorMessage = null
                    )
                }
                .onFailure { exception ->
                    android.util.Log.e("ProjectsViewModel", "Error loading projects", exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar proyectos"
                    )
                }
        }
    }

    fun setSelectedFilter(filter: String) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            selectedFilter = filter,
            filteredProjects = filterProjects(currentState.allProjects, filter)
        )
    }

    private fun filterProjects(projects: List<Project>, filter: String): List<Project> {
        return try {
            val today = getCurrentDateString()
            
            when (filter) {
                "En curso" -> projects.filter { it.status == "ACTIVE" }
                "Completado" -> projects.filter { it.status == "COMPLETED" }
                "Atrasado" -> projects.filter { 
                    try {
                        it.status != "COMPLETED" && isDateBefore(it.endDate, today)
                    } catch (e: Exception) {
                        android.util.Log.e("ProjectsViewModel", "Error filtering overdue projects", e)
                        false
                    }
                }
                else -> projects
            }
        } catch (e: Exception) {
            android.util.Log.e("ProjectsViewModel", "Error filtering projects", e)
            projects
        }
    }
    
    private fun isDateBefore(date1: String, date2: String): Boolean {
        return try {
            date1 < date2
        } catch (e: Exception) {
            false
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

data class ProjectsUiState(
    val isLoading: Boolean = false,
    val allProjects: List<Project> = emptyList(),
    val filteredProjects: List<Project> = emptyList(),
    val selectedFilter: String = "En curso",
    val errorMessage: String? = null
)