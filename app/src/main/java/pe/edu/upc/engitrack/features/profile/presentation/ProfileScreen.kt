package pe.edu.upc.engitrack.features.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import pe.edu.upc.engitrack.features.profile.domain.models.UserProfile

@Composable
fun ProfileScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToEditProfile: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }
    
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF007AFF)
                )
            }
        }
        uiState.error != null -> {
            ErrorScreen(
                error = uiState.error!!,
                onRetry = { viewModel.loadUserProfile() }
            )
        }
        else -> {
            ProfileContent(
                userProfile = uiState.userProfile,
                onEditProfile = onNavigateToEditProfile,
                onLogout = {
                    viewModel.logout()
                    onNavigateToAuth()
                }
            )
        }
    }
}

@Composable
fun ProfileContent(
    userProfile: UserProfile?,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(24.dp)
    ) {
        // Información del usuario
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF007AFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Nombre
                Text(
                    text = userProfile?.fullName ?: "Usuario",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                // Email
                Text(
                    text = userProfile?.email ?: "usuario@ejemplo.com",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Botón editar perfil
                OutlinedButton(
                    onClick = onEditProfile,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF007AFF)
                    )
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar Perfil")
                }
            }
        }
        
        // Estadísticas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Estadísticas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        title = "Proyectos",
                        value = "8"
                    )
                    StatItem(
                        title = "Tareas",
                        value = "24"
                    )
                    StatItem(
                        title = "Completadas",
                        value = "18"
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Botón cerrar sesión
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF3B30)
            )
        ) {
            Icon(
                Icons.Default.ExitToApp,
                contentDescription = "Cerrar sesión",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Cerrar Sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun StatItem(
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF007AFF)
        )
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ErrorScreen(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error al cargar el perfil",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error,
            fontSize = 14.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF007AFF)
            )
        ) {
            Text("Reintentar")
        }
    }
}