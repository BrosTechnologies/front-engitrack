package pe.edu.upc.engitrack.features.workers.presentation.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.engitrack.features.workers.domain.models.ProjectWorker
import pe.edu.upc.engitrack.features.workers.domain.repositories.WorkersRepository
import javax.inject.Inject

@HiltViewModel
class ProjectWorkersViewModel @Inject constructor(
    private val repository: WorkersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectWorkersUiState())
    val uiState: StateFlow<ProjectWorkersUiState> = _uiState.asStateFlow()

    fun loadProjectWorkers(projectId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getProjectWorkers(projectId)
                .onSuccess { workers ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        workers = workers
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }

    fun assignWorkerToProject(
        projectId: String,
        workerId: String,
        startDate: String,
        endDate: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAssigning = true, error = null)
            repository.assignWorkerToProject(projectId, workerId, startDate, endDate)
                .onSuccess { projectWorker ->
                    _uiState.value = _uiState.value.copy(
                        isAssigning = false,
                        workers = _uiState.value.workers + projectWorker,
                        operationSuccess = true
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isAssigning = false,
                        error = exception.message
                    )
                }
        }
    }

    fun removeWorkerFromProject(projectId: String, workerId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRemoving = true, error = null)
            repository.removeWorkerFromProject(projectId, workerId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isRemoving = false,
                        workers = _uiState.value.workers.filter { it.workerId != workerId },
                        operationSuccess = true
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isRemoving = false,
                        error = exception.message
                    )
                }
        }
    }

    fun resetOperationSuccess() {
        _uiState.value = _uiState.value.copy(operationSuccess = false)
    }
}

data class ProjectWorkersUiState(
    val isLoading: Boolean = false,
    val isAssigning: Boolean = false,
    val isRemoving: Boolean = false,
    val workers: List<ProjectWorker> = emptyList(),
    val error: String? = null,
    val operationSuccess: Boolean = false
)
