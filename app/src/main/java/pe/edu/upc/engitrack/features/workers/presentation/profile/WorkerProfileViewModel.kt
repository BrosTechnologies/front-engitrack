package pe.edu.upc.engitrack.features.workers.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.engitrack.core.auth.AuthManager
import pe.edu.upc.engitrack.features.workers.domain.models.Worker
import pe.edu.upc.engitrack.features.workers.domain.repositories.WorkersRepository
import javax.inject.Inject

@HiltViewModel
class WorkerProfileViewModel @Inject constructor(
    private val repository: WorkersRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerProfileUiState())
    val uiState: StateFlow<WorkerProfileUiState> = _uiState.asStateFlow()

    init {
        loadWorkerProfile()
    }

    fun loadWorkerProfile() {
        val workerId = authManager.getWorkerId()
        if (workerId != null) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                repository.getWorkerById(workerId)
                    .onSuccess { worker ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            worker = worker,
                            hasWorkerProfile = true
                        )
                    }
                    .onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message,
                            hasWorkerProfile = false
                        )
                    }
            }
        } else {
            _uiState.value = _uiState.value.copy(hasWorkerProfile = false)
        }
    }

    fun createWorker(
        fullName: String,
        documentNumber: String,
        phone: String,
        position: String,
        hourlyRate: Double,
        projectId: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.createWorker(
                fullName = fullName,
                documentNumber = documentNumber,
                phone = phone,
                position = position,
                hourlyRate = hourlyRate,
                projectId = projectId
            )
                .onSuccess { worker ->
                    authManager.saveWorkerId(worker.id)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        worker = worker,
                        hasWorkerProfile = true,
                        operationSuccess = true
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

    fun updateWorker(
        fullName: String,
        phone: String,
        position: String,
        hourlyRate: Double
    ) {
        viewModelScope.launch {
            val workerId = authManager.getWorkerId()
            if (workerId == null) {
                _uiState.value = _uiState.value.copy(error = "Worker ID no encontrado")
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.updateWorker(
                id = workerId,
                fullName = fullName,
                phone = phone,
                position = position,
                hourlyRate = hourlyRate
            )
                .onSuccess { worker ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        worker = worker,
                        operationSuccess = true
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

    fun resetOperationSuccess() {
        _uiState.value = _uiState.value.copy(operationSuccess = false)
    }
}

data class WorkerProfileUiState(
    val isLoading: Boolean = false,
    val worker: Worker? = null,
    val hasWorkerProfile: Boolean = false,
    val error: String? = null,
    val operationSuccess: Boolean = false
)
