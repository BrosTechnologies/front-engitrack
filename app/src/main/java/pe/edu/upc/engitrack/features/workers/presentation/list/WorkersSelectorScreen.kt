package pe.edu.upc.engitrack.features.workers.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import pe.edu.upc.engitrack.features.workers.domain.models.Worker
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkersSelectorScreen(
    projectId: String,
    projectStartDate: String,
    projectEndDate: String,
    onNavigateBack: () -> Unit,
    onWorkerSelected: (String, String, String) -> Unit, // workerId, startDate, endDate
    viewModel: WorkersListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedWorker by remember { mutableStateOf<Worker?>(null) }
    var showDateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar Worker") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF007AFF),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.filterWorkers(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar por nombre o documento") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { 
                            searchQuery = ""
                            viewModel.filterWorkers("")
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF007AFF))
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = uiState.error!!,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(
                                onClick = { viewModel.loadWorkers() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF007AFF)
                                )
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                uiState.filteredWorkers.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                            Text(
                                text = if (searchQuery.isEmpty()) 
                                    "No hay workers disponibles" 
                                else 
                                    "No se encontraron resultados",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.filteredWorkers) { worker ->
                            WorkerSelectionCard(
                                worker = worker,
                                onClick = {
                                    selectedWorker = worker
                                    showDateDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Date Selection Dialog
    if (showDateDialog && selectedWorker != null) {
        AssignmentDateDialog(
            worker = selectedWorker!!,
            projectStartDate = projectStartDate,
            projectEndDate = projectEndDate,
            onDismiss = { 
                showDateDialog = false
                selectedWorker = null
            },
            onConfirm = { startDate, endDate ->
                onWorkerSelected(selectedWorker!!.id, startDate, endDate)
                showDateDialog = false
                selectedWorker = null
            }
        )
    }
}

@Composable
fun WorkerSelectionCard(
    worker: Worker,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = worker.fullName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E)
                )
                Text(
                    text = worker.position,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Doc: ${worker.documentNumber}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF007AFF).copy(alpha = 0.1f)
            ) {
                Text(
                    text = "S/ ${String.format("%.2f", worker.hourlyRate)}/h",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF007AFF)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDateDialog(
    worker: Worker,
    projectStartDate: String,
    projectEndDate: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // Parse project dates to LocalDate and get millis
    val projectStart = try { LocalDate.parse(projectStartDate) } catch (e: Exception) { LocalDate.now() }
    val projectEnd = try { LocalDate.parse(projectEndDate) } catch (e: Exception) { LocalDate.now().plusMonths(1) }
    
    val projectStartMillis = projectStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val projectEndMillis = projectEnd.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    // DatePicker states with calendar constraints
    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = projectStartMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= projectStartMillis && utcTimeMillis <= projectEndMillis
            }
        }
    )
    
    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = projectStartMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val selectedStart = if (startDate.isNotEmpty()) {
                    try {
                        LocalDate.parse(startDate).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    } catch (e: Exception) {
                        projectStartMillis
                    }
                } else {
                    projectStartMillis
                }
                return utcTimeMillis >= selectedStart && utcTimeMillis <= projectEndMillis
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Asignar a ${worker.fullName}")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Selecciona las fechas de asignación:",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                // Start Date
                OutlinedButton(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (startDate.isEmpty()) 
                            "Fecha de inicio *" 
                        else 
                            "Inicio: $startDate"
                    )
                }

                // End Date
                OutlinedButton(
                    onClick = { 
                        if (startDate.isEmpty()) {
                            errorMessage = "Selecciona primero la fecha de inicio"
                        } else {
                            showEndDatePicker = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = startDate.isNotEmpty()
                ) {
                    Text(
                        text = if (endDate.isEmpty()) 
                            "Fecha de fin *" 
                        else 
                            "Fin: $endDate"
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val validation = validateDates(startDate, endDate, projectStartDate, projectEndDate)
                    if (validation != null) {
                        errorMessage = validation
                    } else {
                        onConfirm(startDate, endDate)
                    }
                }
            ) {
                Text("Asignar", color = Color(0xFF007AFF))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

    // DatePicker Dialogs
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDatePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        startDate = formatter.format(Date(millis))
                    }
                    showStartDatePicker = false
                    errorMessage = null
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDatePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        endDate = formatter.format(Date(millis))
                    }
                    showEndDatePicker = false
                    errorMessage = null
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }
}

private fun validateDates(
    startDate: String, 
    endDate: String, 
    projectStartDate: String,
    projectEndDate: String
): String? {
    return when {
        startDate.isEmpty() -> "Selecciona una fecha de inicio"
        endDate.isEmpty() -> "Selecciona una fecha de fin"
        else -> {
            try {
                val start = LocalDate.parse(startDate)
                val end = LocalDate.parse(endDate)
                val projectStart = LocalDate.parse(projectStartDate)
                val projectEnd = LocalDate.parse(projectEndDate)

                when {
                    start.isBefore(projectStart) -> 
                        "La fecha de inicio debe estar dentro del rango del proyecto (desde ${projectStartDate})"
                    start.isAfter(projectEnd) -> 
                        "La fecha de inicio no puede ser posterior a la fecha límite del proyecto (${projectEndDate})"
                    end.isBefore(start) -> 
                        "La fecha de fin debe ser mayor o igual a la fecha de inicio"
                    end.isAfter(projectEnd) -> 
                        "La fecha de fin no puede ser posterior a la fecha límite del proyecto (${projectEndDate})"
                    else -> null
                }
            } catch (e: Exception) {
                "Error al validar fechas: ${e.message}"
            }
        }
    }
}
