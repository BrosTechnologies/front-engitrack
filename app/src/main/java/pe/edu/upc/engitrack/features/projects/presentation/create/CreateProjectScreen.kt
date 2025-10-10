package pe.edu.upc.engitrack.features.projects.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import pe.edu.upc.engitrack.core.auth.AuthManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    onNavigateBack: () -> Unit,
    authManager: AuthManager,
    viewModel: CreateProjectViewModel = hiltViewModel()
) {
    var projectName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("") }
    var isPriorityDropdownExpanded by remember { mutableStateOf(false) }
    var isDatePickerVisible by remember { mutableStateOf(false) }
    
    val priorities = listOf("Alta", "Media", "Baja")
    val uiState by viewModel.uiState.collectAsState()
    
    // Mostrar DatePicker
    val datePickerState = rememberDatePickerState()
    
    // Observar cambios en el estado
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetState()
            onNavigateBack()
        }
    }
    
    // DatePicker Dialog
    if (isDatePickerVisible) {
        DatePickerDialog(
            onDismissRequest = { isDatePickerVisible = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            endDate = formatter.format(Date(millis))
                        }
                        isDatePickerVisible = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { isDatePickerVisible = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Proyecto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
                    .padding(paddingValues)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Nombre del Proyecto
                Text(
                    text = "Nombre del Proyecto",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    placeholder = { Text("Nombre del Proyecto") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF0F0F0),
                        unfocusedContainerColor = Color(0xFFF0F0F0)
                    )
                )
                
                // Descripción
                Text(
                    text = "Descripción",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(bottom = 24.dp),
                    placeholder = { Text("Descripción del proyecto") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF0F0F0),
                        unfocusedContainerColor = Color(0xFFF0F0F0)
                    ),
                    maxLines = 4
                )
                
                // Fecha Límite
                Text(
                    text = "Fecha Límite",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    placeholder = { Text("Seleccionar fecha") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { isDatePickerVisible = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Calendario")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF0F0F0),
                        unfocusedContainerColor = Color(0xFFF0F0F0)
                    )
                )
                
                // Prioridad (por ahora solo visual, no se envía al backend)
                Text(
                    text = "Prioridad",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                ExposedDropdownMenuBox(
                    expanded = isPriorityDropdownExpanded,
                    onExpandedChange = { isPriorityDropdownExpanded = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    OutlinedTextField(
                        value = selectedPriority,
                        onValueChange = { },
                        readOnly = true,
                        placeholder = { Text("Seleccionar prioridad") },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFF0F0F0),
                            unfocusedContainerColor = Color(0xFFF0F0F0)
                        )
                    )
                    
                    ExposedDropdownMenu(
                        expanded = isPriorityDropdownExpanded,
                        onDismissRequest = { isPriorityDropdownExpanded = false }
                    ) {
                        priorities.forEach { priority ->
                            DropdownMenuItem(
                                text = { Text(priority) },
                                onClick = {
                                    selectedPriority = priority
                                    isPriorityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // Miembros (hardcodeado por ahora)
                Text(
                    text = "Miembros",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = "Se asignará automáticamente",
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { /* TODO: Agregar miembros */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar miembros")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF0F0F0),
                        unfocusedContainerColor = Color(0xFFF0F0F0)
                    )
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Botón Guardar
                Button(
                    onClick = {
                        if (projectName.isNotBlank() && description.isNotBlank() && endDate.isNotBlank()) {
                            val userId = authManager.getUserId() ?: ""
                            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            
                            viewModel.createProject(
                                name = projectName,
                                description = description,
                                startDate = currentDate,
                                endDate = endDate,
                                budget = 0.0,
                                ownerUserId = userId,
                                tasks = emptyList()
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007AFF)
                    ),
                    enabled = !uiState.isLoading && projectName.isNotBlank() && description.isNotBlank() && endDate.isNotBlank()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            "Guardar",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                // Mostrar error si existe
                uiState.error?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}