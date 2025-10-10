package pe.edu.upc.engitrack.features.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import pe.edu.upc.engitrack.features.projects.domain.models.Project
import pe.edu.upc.engitrack.features.projects.domain.models.Task
import pe.edu.upc.engitrack.core.auth.AuthManager
import javax.inject.Inject

@Composable
fun DashboardScreen(
    authManager: AuthManager,
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToProjects: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userData = authManager.getUserData()
    
    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        // Header con saludo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hola, ${userData?.fullName?.split(" ")?.firstOrNull() ?: "Usuario"}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Good morning!",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
            
            IconButton(onClick = { /* TODO: Notificaciones */ }) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Sección de Proyectos Activos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Proyectos activos",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            
            TextButton(onClick = onNavigateToProjects) {
                Text("Ver todos", color = Color(0xFF007AFF))
            }
        }

        // Cards de proyectos activos
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            items(uiState.activeProjects) { project ->
                ProjectCard(project = project)
            }
        }

        // Sección de Tareas del día
        Text(
            text = "Tareas del día",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Lista de tareas
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.todayTasks) { task ->
                TaskItem(
                    task = task,
                    onTaskClick = { taskId ->
                        viewModel.toggleTaskStatus(task.projectId, taskId)
                    }
                )
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun ProjectCard(project: Project) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (project.name.hashCode() % 2) {
                0 -> Color(0xFFF0E5D8) // Beige claro
                else -> Color(0xFFE8F4FD) // Azul claro
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = project.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val completedTasks = project.tasks.count { it.status == "DONE" }
                val totalTasks = project.tasks.size
                val progressPercentage = if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0
                
                Text(
                    text = "$progressPercentage% completado • ${project.endDate}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Barra de progreso
            Column {
                val completedTasks = project.tasks.count { it.status == "DONE" }
                val totalTasks = project.tasks.size
                
                LinearProgressIndicator(
                    progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks.toFloat() else 0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF007AFF),
                    trackColor = Color.Gray.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onTaskClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.status == "DONE",
                onCheckedChange = { onTaskClick(task.taskId) },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF007AFF)
                )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                
                Text(
                    text = task.dueDate,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Status badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when (task.status) {
                    "DONE" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                    "IN_PROGRESS" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                    else -> Color(0xFF9E9E9E).copy(alpha = 0.1f)
                }
            ) {
                Text(
                    text = when (task.status) {
                        "DONE" -> "Completada"
                        "IN_PROGRESS" -> "En progreso"
                        else -> "Pendiente"
                    },
                    fontSize = 12.sp,
                    color = when (task.status) {
                        "DONE" -> Color(0xFF4CAF50)
                        "IN_PROGRESS" -> Color(0xFFFF9800)
                        else -> Color(0xFF9E9E9E)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}