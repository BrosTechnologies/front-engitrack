package pe.edu.upc.engitrack.features.workers.presentation.assignments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.engitrack.core.auth.AuthManager
import pe.edu.upc.engitrack.features.workers.domain.models.WorkerAssignment
import pe.edu.upc.engitrack.features.workers.domain.repositories.WorkersRepository
import javax.inject.Inject

@HiltViewModel
class WorkerAssignmentsViewModel @Inject constructor(
    private val repository: WorkersRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerAssignmentsUiState())
    val uiState: StateFlow<WorkerAssignmentsUiState> = _uiState.asStateFlow()

    init {
        loadAssignments()
    }

    fun loadAssignments() {
        val workerId = authManager.getWorkerId()
        if (workerId == null) {
            _uiState.value = _uiState.value.copy(
                error = "No tienes un perfil de worker activo"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getWorkerAssignments(workerId)
                .onSuccess { assignments ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        assignments = assignments
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
}

data class WorkerAssignmentsUiState(
    val isLoading: Boolean = false,
    val assignments: List<WorkerAssignment> = emptyList(),
    val error: String? = null
)
