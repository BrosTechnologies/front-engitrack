package pe.edu.upc.engitrack.features.calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.engitrack.features.projects.data.remote.ProjectApiService
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class CalendarUiState(
    val isLoading: Boolean = false,
    val upcomingProjects: List<CalendarProject> = emptyList(),
    val error: String? = null
)

data class CalendarProject(
    val id: String,
    val name: String,
    val endDate: String,
    val day: String,
    val dayNumber: String,
    val isOverdue: Boolean,
    val isToday: Boolean,
    val isThisWeek: Boolean
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val projectApiService: ProjectApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()
    
    fun loadUpcomingProjects() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val response = projectApiService.getProjects()
                
                if (response.isSuccessful && response.body() != null) {
                    val projects = response.body()!!
                    val today = Calendar.getInstance()
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
                    val dayNumberFormat = SimpleDateFormat("dd", Locale.getDefault())
                    
                    val calendarProjects = projects
                        .filter { it.endDate.isNotEmpty() }
                        .map { project ->
                            val endDate = try {
                                dateFormat.parse(project.endDate)
                            } catch (e: Exception) {
                                null
                            }
                            
                            val isOverdue = endDate?.before(today.time) == true
                            val isToday = endDate?.let {
                                dateFormat.format(it) == dateFormat.format(today.time)
                            } == true
                            
                            val isThisWeek = endDate?.let { date ->
                                val endCal = Calendar.getInstance().apply { time = date }
                                val todayCal = Calendar.getInstance()
                                val diff = endCal.get(Calendar.DAY_OF_YEAR) - todayCal.get(Calendar.DAY_OF_YEAR)
                                diff in 0..7
                            } == true
                            
                            CalendarProject(
                                id = project.id,
                                name = project.name,
                                endDate = project.endDate,
                                day = endDate?.let { dayFormat.format(it) } ?: "",
                                dayNumber = endDate?.let { dayNumberFormat.format(it) } ?: "",
                                isOverdue = isOverdue,
                                isToday = isToday,
                                isThisWeek = isThisWeek
                            )
                        }
                        .sortedWith(compareBy<CalendarProject> { it.isOverdue }
                            .thenBy { it.isToday }
                            .thenBy { it.endDate })
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        upcomingProjects = calendarProjects,
                        error = null
                    )
                } else {
                    // Si no hay proyectos reales, mostrar algunos de ejemplo
                    val exampleProjects = createExampleProjects()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        upcomingProjects = exampleProjects,
                        error = null
                    )
                }
            } catch (e: Exception) {
                // En caso de error, mostrar proyectos de ejemplo
                val exampleProjects = createExampleProjects()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    upcomingProjects = exampleProjects,
                    error = null
                )
            }
        }
    }
    
    private fun createExampleProjects(): List<CalendarProject> {
        val today = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dayNumberFormat = SimpleDateFormat("dd", Locale.getDefault())
        
        // Proyecto de hoy
        val todayProject = CalendarProject(
            id = "1",
            name = "Entrega de wireframes",
            endDate = dateFormat.format(today.time),
            day = dayFormat.format(today.time),
            dayNumber = dayNumberFormat.format(today.time),
            isOverdue = false,
            isToday = true,
            isThisWeek = true
        )
        
        // Proyecto en 3 días
        val in3Days = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 3)
        }
        val futureProject = CalendarProject(
            id = "2",
            name = "Revisión de arquitectura",
            endDate = dateFormat.format(in3Days.time),
            day = dayFormat.format(in3Days.time),
            dayNumber = dayNumberFormat.format(in3Days.time),
            isOverdue = false,
            isToday = false,
            isThisWeek = true
        )
        
        // Proyecto atrasado
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -1)
        }
        val overdueProject = CalendarProject(
            id = "3",
            name = "Implementación API",
            endDate = dateFormat.format(yesterday.time),
            day = dayFormat.format(yesterday.time),
            dayNumber = dayNumberFormat.format(yesterday.time),
            isOverdue = true,
            isToday = false,
            isThisWeek = true
        )
        
        return listOf(overdueProject, todayProject, futureProject)
    }
}