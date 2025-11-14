package pe.edu.upc.engitrack.features.workers.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.engitrack.features.workers.domain.models.Worker
import pe.edu.upc.engitrack.features.workers.domain.repositories.WorkersRepository
import javax.inject.Inject

@HiltViewModel
class WorkersListViewModel @Inject constructor(
    private val repository: WorkersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkersListUiState())
    val uiState: StateFlow<WorkersListUiState> = _uiState.asStateFlow()

    init {
        loadWorkers()
    }

    fun loadWorkers(page: Int? = null, pageSize: Int? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getWorkers(page, pageSize)
                .onSuccess { workers ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        workers = workers,
                        filteredWorkers = workers
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

    fun filterWorkers(query: String) {
        val filtered = if (query.isBlank()) {
            _uiState.value.workers
        } else {
            _uiState.value.workers.filter {
                it.fullName.contains(query, ignoreCase = true) ||
                it.documentNumber.contains(query, ignoreCase = true) ||
                it.position.contains(query, ignoreCase = true)
            }
        }
        _uiState.value = _uiState.value.copy(
            filteredWorkers = filtered,
            searchQuery = query
        )
    }
}

data class WorkersListUiState(
    val isLoading: Boolean = false,
    val workers: List<Worker> = emptyList(),
    val filteredWorkers: List<Worker> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
)
