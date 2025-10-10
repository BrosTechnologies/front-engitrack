package pe.edu.upc.engitrack.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    
    // Cargar datos del perfil al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }
    
    // Actualizar campos cuando se cargan los datos
    LaunchedEffect(uiState.userProfile) {
        uiState.userProfile?.let { profile ->
            fullName = profile.fullName
            phone = profile.phone
        }
    }
    
    // Navegar de vuelta si se actualizó exitosamente
    LaunchedEffect(uiState.isUpdateSuccess) {
        if (uiState.isUpdateSuccess) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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
                        text = uiState.error!!,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { viewModel.loadUserProfile() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007AFF)
                        )
                    ) {
                        Text("Reintentar")
                    }
                }
            }
            
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF8F9FA))
                        .padding(paddingValues)
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Email (no editable)
                    Text(
                        text = "Email",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = uiState.userProfile?.email ?: "",
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        readOnly = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFE8E8E8),
                            unfocusedContainerColor = Color(0xFFE8E8E8)
                        )
                    )
                    
                    // Nombre Completo
                    Text(
                        text = "Nombre Completo",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        placeholder = { Text("Nombre completo") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFF0F0F0),
                            unfocusedContainerColor = Color(0xFFF0F0F0)
                        )
                    )
                    
                    // Teléfono
                    Text(
                        text = "Teléfono",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 40.dp),
                        placeholder = { Text("Número de teléfono") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFF0F0F0),
                            unfocusedContainerColor = Color(0xFFF0F0F0)
                        )
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Botón Guardar
                    Button(
                        onClick = {
                            if (fullName.isNotBlank() && phone.isNotBlank()) {
                                viewModel.updateProfile(fullName, phone)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007AFF)
                        ),
                        enabled = !uiState.isUpdating && fullName.isNotBlank() && phone.isNotBlank()
                    ) {
                        if (uiState.isUpdating) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                "Guardar Cambios",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    
                    // Mostrar error de actualización si existe
                    uiState.updateError?.let { error ->
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
}