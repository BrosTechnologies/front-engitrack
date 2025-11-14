package pe.edu.upc.engitrack.features.projects.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import pe.edu.upc.engitrack.core.auth.AuthManager
import pe.edu.upc.engitrack.features.projects.domain.models.Priority
import pe.edu.upc.engitrack.features.projects.domain.models.Task
import pe.edu.upc.engitrack.features.projects.domain.models.TaskStatus
import pe.edu.upc.engitrack.features.projects.presentation.components.PriorityBadge
import pe.edu.upc.engitrack.features.projects.presentation.components.PrioritySelector
import pe.edu.upc.engitrack.features.workers.presentation.project.ProjectWorkersSection
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: String,
    onNavigateBack: () -> Unit,
    onNavigateToWorkersSelector: (String, String) -> Unit = {_, _ -> },
    viewModel: ProjectDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val context = LocalContext.current
    val authManager = remember {
        val entryPoint = dagger.hilt.android.EntryPointAccessors.fromApplication(
            context.applicationContext,
            pe.edu.upc.engitrack.core.navigation.AuthManagerEntryPoint::class.java
        )
        entryPoint.authManager()
    }
    
    val currentUserId = authManager.getUserId()
    val currentUserRole = authManager.getUserRole()
    
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDueDate by remember { mutableStateOf("") }
    var isTaskDatePickerVisible by remember { mutableStateOf(false) }
    var taskDateValidationError by remember { mutableStateOf<String?>(null) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var showCompleteProjectDialog by remember { mutableStateOf(false) }
    var showEditProjectDialog by remember { mutableStateOf(false) }
    var showDeleteProjectDialog by remember { mutableStateOf(false) }
    var showDropdownMenu by remember { mutableStateOf(false) }
    
    // Estados para edición de proyecto
    var editProjectName by remember { mutableStateOf("") }
    var editProjectDescription by remember { mutableStateOf("") }
    var editProjectEndDate by remember { mutableStateOf("") }
    var editProjectBudget by remember { mutableStateOf("") }
    var editProjectPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var isEditProjectDatePickerVisible by remember { mutableStateOf(false) }
    var editProjectBudgetError by remember { mutableStateOf<String?>(null) }
    
    val taskDatePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val project = uiState.project ?: return true
                val projectEndDateMillis = try {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .parse(project.endDate)?.time ?: Long.MAX_VALUE
                } catch (e: Exception) {
                    Long.MAX_VALUE
                }
                return utcTimeMillis <= projectEndDateMillis
            }
        }
    )
    val editProjectDatePickerState = rememberDatePickerState()
    
    // Cargar el proyecto al iniciar
    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }
    
    // Limpiar operationSuccess después de mostrar
    LaunchedEffect(uiState.operationSuccess) {
        if (uiState.operationSuccess) {
            viewModel.resetOperationSuccess()
        }
    }
    
    // Navegar hacia atrás cuando se elimina el proyecto
    LaunchedEffect(uiState.projectDeleted) {
        if (uiState.projectDeleted) {
            onNavigateBack()
        }
    }
    
    // DatePicker Dialog para fecha de tarea con validación
    if (isTaskDatePickerVisible) {
        DatePickerDialog(
            onDismissRequest = { isTaskDatePickerVisible = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskDatePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            newTaskDueDate = formatter.format(Date(millis))
                        }
                        isTaskDatePickerVisible = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { isTaskDatePickerVisible = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = taskDatePickerState)
        }
    }
    
    // DatePicker Dialog para editar fecha límite del proyecto
    if (isEditProjectDatePickerVisible) {
        DatePickerDialog(
            onDismissRequest = { isEditProjectDatePickerVisible = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        editProjectDatePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            editProjectEndDate = formatter.format(Date(millis))
                        }
                        isEditProjectDatePickerVisible = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditProjectDatePickerVisible = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = editProjectDatePickerState)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.project?.name ?: "Cargando...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón de menú de opciones (solo si no está completado)
                    if (uiState.project?.status != "COMPLETED") {
                        Box {
                            IconButton(onClick = { showDropdownMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                            }
                            
                            DropdownMenu(
                                expanded = showDropdownMenu,
                                onDismissRequest = { showDropdownMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Editar proyecto") },
                                    onClick = {
                                        // Pre-llenar datos del proyecto
                                        uiState.project?.let { project ->
                                            editProjectName = project.name
                                            editProjectDescription = project.description ?: ""
                                            editProjectEndDate = project.endDate
                                            editProjectBudget = project.budget.toString()
                                            editProjectPriority = Priority.fromString(project.priority)
                                        }
                                        showEditProjectDialog = true
                                        showDropdownMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Edit, contentDescription = null)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Completar proyecto") },
                                    onClick = {
                                        showCompleteProjectDialog = true
                                        showDropdownMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Eliminar proyecto", color = Color.Red) },
                                    onClick = {
                                        showDeleteProjectDialog = true
                                        showDropdownMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Delete, 
                                            contentDescription = null,
                                            tint = Color.Red
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            // Solo mostrar FAB si el proyecto no está completado
            if (uiState.project?.status != "COMPLETED") {
                FloatingActionButton(
                    onClick = { showAddTaskDialog = true },
                    containerColor = Color(0xFF007AFF)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar tarea",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Mostrar loading
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            
            // Mostrar error
            uiState.error?.let { error ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Text(
                            text = error,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            
            // Mostrar proyecto
            uiState.project?.let { project ->
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Información del proyecto
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Estado del proyecto
                            if (project.status == "COMPLETED") {
                                Text(
                                    text = "✓ COMPLETADO",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                            }
                            
                            Text(
                                text = "Descripción",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = project.description?.takeIf { it.isNotBlank() } ?: "Sin descripción",
                                fontSize = 16.sp,
                                color = if (project.description.isNullOrBlank()) Color.Gray else Color.Black,
                                fontStyle = if (project.description.isNullOrBlank()) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Fecha Límite",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = project.endDate,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                
                                Column {
                                    Text(
                                        text = "Prioridad",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    PriorityBadge(
                                        priority = Priority.fromString(project.priority),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Presupuesto
                            Column {
                                Text(
                                    text = "Presupuesto",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "S/ ${String.format("%.2f", project.budget)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF007AFF)
                                )
                            }
                        }
                    }
                    
                    // Workers Section
                    ProjectWorkersSection(
                        projectId = projectId,
                        projectEndDate = project.endDate,
                        projectOwnerId = project.ownerUserId,
                        currentUserId = currentUserId,
                        currentUserRole = currentUserRole,
                        isProjectCompleted = project.status == "COMPLETED",
                        onNavigateToWorkersSelector = { onNavigateToWorkersSelector(project.startDate, project.endDate) }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Título de tareas con contador
                    val completedCount = project.tasks.count { it.status == "DONE" }
                    val totalCount = project.tasks.size
                    Text(
                        text = "Tareas ($completedCount/$totalCount)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                
                // Lista de tareas
                if (project.tasks.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hay tareas en este proyecto",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    items(project.tasks) { task ->
                        TaskCard(
                            task = task,
                            isProjectCompleted = project.status == "COMPLETED",
                            onTaskClick = {
                                if (project.status != "COMPLETED") {
                                    selectedTask = task
                                    showStatusDialog = true
                                }
                            },
                            onDeleteClick = {
                                if (project.status != "COMPLETED") {
                                    taskToDelete = task
                                    showDeleteDialog = true
                                }
                            }
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(80.dp)) // Espacio para el FAB
                }
            }
        }
    }
    
    // Dialog para agregar nueva tarea
    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddTaskDialog = false
                newTaskTitle = ""
                newTaskDueDate = ""
                taskDateValidationError = null
            },
            title = { Text("Nueva Tarea", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        placeholder = { Text("Título de la tarea") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    OutlinedTextField(
                        value = newTaskDueDate,
                        onValueChange = { },
                        placeholder = { Text("Fecha de vencimiento") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { isTaskDatePickerVisible = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Calendario")
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        isError = taskDateValidationError != null
                    )
                    
                    // Mostrar mensaje de validación si existe
                    taskDateValidationError?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    
                    // Mostrar información sobre la fecha límite del proyecto
                    uiState.project?.let { project ->
                        Text(
                            text = "Fecha límite del proyecto: ${project.endDate}",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTaskTitle.isNotBlank() && newTaskDueDate.isNotBlank()) {
                            // Validar que la fecha de la tarea no sea posterior a la fecha límite del proyecto
                            val projectEndDate = uiState.project?.endDate ?: ""
                            if (projectEndDate.isNotBlank() && isTaskDateAfterProjectEndDate(newTaskDueDate, projectEndDate)) {
                                taskDateValidationError = "La fecha de la tarea no puede ser posterior a la fecha límite del proyecto ($projectEndDate)"
                            } else {
                                viewModel.createTask(projectId, newTaskTitle, newTaskDueDate)
                                newTaskTitle = ""
                                newTaskDueDate = ""
                                taskDateValidationError = null
                                showAddTaskDialog = false
                            }
                        }
                    },
                    enabled = newTaskTitle.isNotBlank() && newTaskDueDate.isNotBlank() && !uiState.isCreatingTask
                ) {
                    if (uiState.isCreatingTask) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Agregar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddTaskDialog = false
                    newTaskTitle = ""
                    newTaskDueDate = ""
                    taskDateValidationError = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Dialog para cambiar estado de tarea
    if (showStatusDialog && selectedTask != null) {
        AlertDialog(
            onDismissRequest = {
                showStatusDialog = false
                selectedTask = null
            },
            title = { Text("Cambiar Estado", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Selecciona el nuevo estado para: ${selectedTask?.title}",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    TaskStatus.values().forEach { status ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.updateTaskStatus(
                                        projectId,
                                        selectedTask!!.taskId,
                                        status.name
                                    )
                                    showStatusDialog = false
                                    selectedTask = null
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedTask?.status == status.name) 
                                    Color(0xFFE3F2FD) else Color.White
                            )
                        ) {
                            Text(
                                text = when(status) {
                                    TaskStatus.PENDING -> "⏱️ Pendiente"
                                    TaskStatus.IN_PROGRESS -> "🔄 En Progreso"
                                    TaskStatus.DONE -> "✅ Completada"
                                },
                                modifier = Modifier.padding(16.dp),
                                fontWeight = if (selectedTask?.status == status.name) 
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = {
                    showStatusDialog = false
                    selectedTask = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Dialog para confirmar eliminación
    if (showDeleteDialog && taskToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                taskToDelete = null
            },
            title = { Text("Eliminar Tarea", fontWeight = FontWeight.Bold) },
            text = {
                Text("¿Estás seguro de que deseas eliminar la tarea \"${taskToDelete?.title}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTask(projectId, taskToDelete!!.taskId)
                        showDeleteDialog = false
                        taskToDelete = null
                    },
                    enabled = !uiState.isDeletingTask
                ) {
                    if (uiState.isDeletingTask) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Eliminar", color = Color.Red)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    taskToDelete = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Dialog para completar proyecto
    if (showCompleteProjectDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteProjectDialog = false },
            title = { Text("Completar Proyecto", fontWeight = FontWeight.Bold) },
            text = {
                Text("¿Estás seguro de que deseas marcar este proyecto como completado?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.completeProject(projectId)
                        showCompleteProjectDialog = false
                    },
                    enabled = !uiState.isCompletingProject
                ) {
                    if (uiState.isCompletingProject) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Completar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteProjectDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Dialog para eliminar proyecto
    if (showDeleteProjectDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteProjectDialog = false },
            title = { Text("Eliminar proyecto", fontWeight = FontWeight.Bold) },
            text = {
                Text("¿Seguro que deseas eliminar este proyecto? Se eliminarán también todas sus tareas.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProject(projectId)
                        showDeleteProjectDialog = false
                    },
                    enabled = !uiState.isDeletingProject,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    if (uiState.isDeletingProject) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.Red
                        )
                    } else {
                        Text("Eliminar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteProjectDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Dialog para editar proyecto
    if (showEditProjectDialog) {
        AlertDialog(
            onDismissRequest = {
                showEditProjectDialog = false
                editProjectBudgetError = null
            },
            title = { Text("Editar Proyecto", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Nombre del proyecto
                    OutlinedTextField(
                        value = editProjectName,
                        onValueChange = { editProjectName = it },
                        label = { Text("Nombre del proyecto") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    // Descripción
                    OutlinedTextField(
                        value = editProjectDescription,
                        onValueChange = { 
                            if (it.length <= 500) {
                                editProjectDescription = it
                            }
                        },
                        label = { Text("Descripción (opcional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 4,
                        supportingText = {
                            Text(
                                text = "${editProjectDescription.length}/500",
                                fontSize = 12.sp,
                                color = if (editProjectDescription.length > 450) Color.Red else Color.Gray
                            )
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Fecha límite
                    OutlinedTextField(
                        value = editProjectEndDate,
                        onValueChange = { },
                        label = { Text("Fecha límite") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        trailingIcon = {
                            IconButton(onClick = { isEditProjectDatePickerVisible = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Calendario")
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    // Presupuesto
                    OutlinedTextField(
                        value = editProjectBudget,
                        onValueChange = { 
                            editProjectBudget = it
                            // Validar que sea un número
                            if (it.isNotEmpty() && it.toDoubleOrNull() == null) {
                                editProjectBudgetError = "Ingrese un valor numérico válido"
                            } else {
                                editProjectBudgetError = null
                            }
                        },
                        label = { Text("Presupuesto (S/)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        isError = editProjectBudgetError != null
                    )
                    
                    editProjectBudgetError?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    
                    if (editProjectBudgetError == null) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Prioridad
                    Text(
                        text = "Prioridad",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    PrioritySelector(
                        selectedPriority = editProjectPriority,
                        onPrioritySelected = { editProjectPriority = it }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editProjectName.isNotBlank() && editProjectEndDate.isNotBlank() && editProjectBudgetError == null) {
                            val budget = editProjectBudget.toDoubleOrNull() ?: 0.0
                            viewModel.updateProject(
                                projectId = projectId,
                                name = editProjectName,
                                description = if (editProjectDescription.isBlank()) null else editProjectDescription,
                                endDate = editProjectEndDate,
                                budget = budget,
                                priority = editProjectPriority
                            )
                            showEditProjectDialog = false
                            editProjectBudgetError = null
                        }
                    },
                    enabled = editProjectName.isNotBlank() && editProjectEndDate.isNotBlank() && 
                              editProjectBudgetError == null && !uiState.isUpdatingProject
                ) {
                    if (uiState.isUpdatingProject) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Guardar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditProjectDialog = false
                    editProjectBudgetError = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TaskCard(
    task: Task,
    isProjectCompleted: Boolean,
    onTaskClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = !isProjectCompleted) { onTaskClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono de estado
            Icon(
                imageVector = if (task.status == "DONE") Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = task.status,
                tint = when(task.status) {
                    "DONE" -> Color(0xFF4CAF50)
                    "IN_PROGRESS" -> Color(0xFF2196F3)
                    else -> Color.Gray
                },
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    color = if (task.status == "DONE") Color.Gray else Color.Black,
                    fontWeight = if (task.status == "DONE") FontWeight.Normal else FontWeight.Medium
                )
                
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Badge de estado
                    Text(
                        text = when(task.status) {
                            "PENDING" -> "Pendiente"
                            "IN_PROGRESS" -> "En Progreso"
                            "DONE" -> "Completada"
                            else -> task.status
                        },
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier
                            .background(
                                color = when(task.status) {
                                    "DONE" -> Color(0xFF4CAF50)
                                    "IN_PROGRESS" -> Color(0xFF2196F3)
                                    else -> Color.Gray
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    
                    // Fecha de vencimiento
                    Text(
                        text = "📅 ${task.dueDate}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // Botón de eliminar (solo si no está completado el proyecto)
            if (!isProjectCompleted) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar tarea",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

private fun isTaskDateAfterProjectEndDate(taskDate: String, projectEndDate: String): Boolean {
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val task = formatter.parse(taskDate)
        val project = formatter.parse(projectEndDate)
        
        if (task != null && project != null) {
            task.after(project)
        } else {
            false
        }
    } catch (e: Exception) {
        false
    }
}