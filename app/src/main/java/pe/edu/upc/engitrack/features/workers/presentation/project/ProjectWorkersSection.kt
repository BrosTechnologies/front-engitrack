package pe.edu.upc.engitrack.features.workers.presentation.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
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
import pe.edu.upc.engitrack.features.workers.domain.models.ProjectWorker

@Composable
fun ProjectWorkersSection(
    projectId: String,
    projectEndDate: String,
    projectOwnerId: String,
    currentUserId: String?,
    currentUserRole: String?,
    isProjectCompleted: Boolean,
    onNavigateToWorkersSelector: () -> Unit,
    viewModel: ProjectWorkersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var workerToDelete by remember { mutableStateOf<ProjectWorker?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(projectId) {
        viewModel.loadProjectWorkers(projectId)
    }

    LaunchedEffect(uiState.operationSuccess) {
        if (uiState.operationSuccess) {
            viewModel.resetOperationSuccess()
            workerToDelete = null
            showDeleteDialog = false
        }
    }

    // Check if user can manage workers
    val canManageWorkers = !isProjectCompleted && 
        (currentUserId == projectOwnerId || 
         currentUserRole == "SUPERVISOR" || 
         currentUserRole == "CONTRACTOR")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = null,
                    tint = Color(0xFF007AFF),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Workers Asignados (${uiState.workers.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (canManageWorkers) {
                IconButton(
                    onClick = onNavigateToWorkersSelector,
                    enabled = !uiState.isAssigning
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Asignar worker",
                        tint = Color(0xFF007AFF)
                    )
                }
            }
        }

        // Loading state
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF007AFF),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        // Error state
        else if (uiState.error != null && uiState.workers.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))
            ) {
                Text(
                    text = uiState.error!!,
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF856404),
                    fontSize = 14.sp
                )
            }
        }
        // Empty state
        else if (uiState.workers.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = "No hay workers asignados",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    if (canManageWorkers) {
                        TextButton(onClick = onNavigateToWorkersSelector) {
                            Text("Asignar worker", color = Color(0xFF007AFF))
                        }
                    }
                }
            }
        }
        // Workers list
        else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.workers.forEach { worker ->
                    WorkerCard(
                        worker = worker,
                        canRemove = canManageWorkers,
                        isRemoving = uiState.isRemoving,
                        onRemoveClick = {
                            workerToDelete = worker
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && workerToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false
                workerToDelete = null
            },
            title = { Text("Confirmar eliminación") },
            text = {
                Text("¿Estás seguro de que quieres remover a ${workerToDelete!!.fullName} de este proyecto?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeWorkerFromProject(projectId, workerToDelete!!.workerId)
                    },
                    enabled = !uiState.isRemoving
                ) {
                    if (uiState.isRemoving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFF007AFF)
                        )
                    } else {
                        Text("Remover", color = Color.Red)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDeleteDialog = false
                        workerToDelete = null
                    },
                    enabled = !uiState.isRemoving
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun WorkerCard(
    worker: ProjectWorker,
    canRemove: Boolean,
    isRemoving: Boolean,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "S/ ${String.format("%.2f", worker.hourlyRate)}/h",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF007AFF)
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${worker.startDate} - ${worker.endDate}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            if (canRemove) {
                IconButton(
                    onClick = onRemoveClick,
                    enabled = !isRemoving
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remover",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}
