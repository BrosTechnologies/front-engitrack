package pe.edu.upc.engitrack.features.projects.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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

@Composable
fun ProjectsScreen(
    viewModel: ProjectsViewModel = hiltViewModel(),
    onProjectClick: (String) -> Unit,
    onCreateProject: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val filterOptions = listOf("En curso", "Completado", "Atrasado")
    
    LaunchedEffect(Unit) {
        viewModel.loadProjects()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Proyectos",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            IconButton(
                onClick = onCreateProject,
                modifier = Modifier
                    .background(
                        Color(0xFF007AFF),
                        RoundedCornerShape(12.dp)
                    )
                    .size(48.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Crear proyecto",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Filtros
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            items(filterOptions) { filter ->
                FilterChip(
                    onClick = { viewModel.setSelectedFilter(filter) },
                    label = { Text(filter) },
                    selected = uiState.selectedFilter == filter,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF007AFF),
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = Color.Gray
                    )
                )
            }
        }

        // Lista de proyectos
        if (uiState.filteredProjects.isEmpty() && !uiState.isLoading) {
            // Estado vacío
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Imagen de caja vacía (placeholder)
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                Color(0xFFF0E5D8),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📦",
                            fontSize = 48.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "No hay proyectos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    
                    Text(
                        text = "Crea un nuevo proyecto para empezar a colaborar\ncon tu equipo.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                    )
                    
                    Button(
                        onClick = onCreateProject,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007AFF)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Crear proyecto",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.filteredProjects) { project ->
                    ProjectListItem(
                        project = project,
                        onClick = { onProjectClick(project.id) }
                    )
                }
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

        uiState.errorMessage?.let { error ->
            LaunchedEffect(error) {
                // Mostrar error
            }
        }
    }
}

@Composable
private fun ProjectListItem(
    project: Project,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header del proyecto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Prioridad: ",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        
                        Text(
                            text = getPriorityText(project),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = getPriorityColor(project)
                        )
                        
                        Text(
                            text = " | Fecha límite: ${project.endDate}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                // Progreso circular
                val completedTasks = project.tasks.count { it.status == "DONE" }
                val totalTasks = project.tasks.size
                val progress = if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0
                
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks.toFloat() else 0f,
                        modifier = Modifier.size(48.dp),
                        color = Color(0xFF007AFF),
                        strokeWidth = 4.dp,
                        trackColor = Color.Gray.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "$progress",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Barra de progreso
            val completedTasksForProgress = project.tasks.count { it.status == "DONE" }
            val totalTasksForProgress = project.tasks.size
            
            LinearProgressIndicator(
                progress = if (totalTasksForProgress > 0) completedTasksForProgress.toFloat() / totalTasksForProgress.toFloat() else 0f,
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

private fun getPriorityText(project: Project): String {
    val today = getCurrentDateString()
    return when {
        project.tasks.any { it.status == "PENDING" && it.dueDate < today } -> "Alta"
        project.tasks.size > 5 -> "Media"
        else -> "Baja"
    }
}

private fun getPriorityColor(project: Project): Color {
    return when (getPriorityText(project)) {
        "Alta" -> Color(0xFFE53E3E)
        "Media" -> Color(0xFFFF9800)
        else -> Color(0xFF4CAF50)
    }
}

private fun getCurrentDateString(): String {
    val calendar = java.util.Calendar.getInstance()
    val year = calendar.get(java.util.Calendar.YEAR)
    val month = calendar.get(java.util.Calendar.MONTH) + 1
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
    
    return String.format("%04d-%02d-%02d", year, month, day)
}