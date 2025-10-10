package pe.edu.upc.engitrack.features.projects.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TaskItem(
    val id: Int,
    val title: String,
    val isCompleted: Boolean,
    val assignedTo: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: String,
    onNavigateBack: () -> Unit
) {
    // Datos de ejemplo - en el futuro esto vendrá del ViewModel
    val projectName = "Mi Proyecto"
    val tasks = remember {
        mutableStateListOf(
            TaskItem(1, "Definir arquitectura del sistema", false),
            TaskItem(2, "Crear wireframes de la interfaz", true),
            TaskItem(3, "Configurar base de datos", false),
            TaskItem(4, "Implementar autenticación", false),
            TaskItem(5, "Diseñar API REST", true)
        )
    }
    
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(projectName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
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
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
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
                        Text(
                            text = "Descripción",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Este es un proyecto de desarrollo de aplicación móvil para el seguimiento de proyectos.",
                            fontSize = 16.sp,
                            color = Color.Black
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
                                    text = "15 Dic 2024",
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
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFFFEBCD)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Media",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        fontSize = 14.sp,
                                        color = Color(0xFFFF8C00)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Título de tareas
                Text(
                    text = "Tareas (${tasks.count { it.isCompleted }}/${tasks.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            items(tasks) { task ->
                TaskCard(
                    task = task,
                    onTaskToggle = { taskId ->
                        val index = tasks.indexOfFirst { it.id == taskId }
                        if (index != -1) {
                            tasks[index] = tasks[index].copy(isCompleted = !tasks[index].isCompleted)
                        }
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp)) // Espacio para el FAB
            }
        }
    }
    
    // Dialog para agregar nueva tarea
    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("Nueva Tarea") },
            text = {
                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    placeholder = { Text("Título de la tarea") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            tasks.add(
                                TaskItem(
                                    id = tasks.maxOfOrNull { it.id }?.plus(1) ?: 1,
                                    title = newTaskTitle,
                                    isCompleted = false
                                )
                            )
                            newTaskTitle = ""
                            showAddTaskDialog = false
                        }
                    }
                ) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TaskCard(
    task: TaskItem,
    onTaskToggle: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onTaskToggle(task.id) },
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
            Icon(
                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = if (task.isCompleted) "Completada" else "No completada",
                tint = if (task.isCompleted) Color(0xFF4CAF50) else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    color = if (task.isCompleted) Color.Gray else Color.Black,
                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium
                )
                
                if (task.assignedTo.isNotEmpty()) {
                    Text(
                        text = "Asignado a: ${task.assignedTo}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}