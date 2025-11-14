package pe.edu.upc.engitrack.features.workers.presentation.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import pe.edu.upc.engitrack.features.workers.domain.models.Worker
import pe.edu.upc.engitrack.features.workers.presentation.profile.WorkerProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerFormScreen(
    worker: Worker? = null,
    onNavigateBack: () -> Unit,
    viewModel: WorkerProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var fullName by remember { mutableStateOf(worker?.fullName ?: "") }
    var position by remember { mutableStateOf(worker?.position ?: "") }
    var documentNumber by remember { mutableStateOf(worker?.documentNumber ?: "") }
    var phone by remember { mutableStateOf(worker?.phone ?: "") }
    var hourlyRate by remember { mutableStateOf(worker?.hourlyRate?.toString() ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isEditMode = worker != null

    LaunchedEffect(uiState.operationSuccess) {
        if (uiState.operationSuccess) {
            viewModel.resetOperationSuccess()
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.error) {
        errorMessage = uiState.error
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (isEditMode) "Editar Perfil de Worker" else "Crear Perfil de Worker") 
                },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isEditMode) 
                    "Actualiza tu información como colaborador" 
                else 
                    "Completa tu perfil para trabajar como colaborador",
                fontSize = 16.sp,
                color = Color.Gray
            )

            // Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = { 
                    fullName = it
                    errorMessage = null
                },
                label = { Text("Nombre completo *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Position
            OutlinedTextField(
                value = position,
                onValueChange = { 
                    position = it
                    errorMessage = null
                },
                label = { Text("Cargo/Posición *") },
                placeholder = { Text("Ej: Ingeniero Civil, Obrero, Técnico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Document Number
            OutlinedTextField(
                value = documentNumber,
                onValueChange = { 
                    documentNumber = it
                    errorMessage = null
                },
                label = { Text("Número de documento *") },
                placeholder = { Text("DNI, RUC, etc.") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Phone
            OutlinedTextField(
                value = phone,
                onValueChange = { 
                    phone = it
                    errorMessage = null
                },
                label = { Text("Teléfono *") },
                placeholder = { Text("Ej: +51 999 999 999") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            // Hourly Rate
            OutlinedTextField(
                value = hourlyRate,
                onValueChange = { 
                    hourlyRate = it
                    errorMessage = null
                },
                label = { Text("Tarifa por hora (S/) *") },
                placeholder = { Text("Ej: 25.50") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            // Error Message
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Submit Button
            Button(
                onClick = {
                    val validationError = validateInputs(
                        fullName, position, documentNumber, phone, hourlyRate
                    )
                    if (validationError != null) {
                        errorMessage = validationError
                        return@Button
                    }

                    val rateValue = hourlyRate.toDoubleOrNull() ?: 0.0
                    
                    if (isEditMode) {
                        viewModel.updateWorker(
                            fullName = fullName,
                            phone = phone,
                            position = position,
                            hourlyRate = rateValue
                        )
                    } else {
                        viewModel.createWorker(
                            fullName = fullName,
                            documentNumber = documentNumber,
                            phone = phone,
                            position = position,
                            hourlyRate = rateValue
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF)
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = if (isEditMode) "Actualizar Perfil" else "Crear Perfil",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (!isEditMode) {
                Text(
                    text = "* Campos obligatorios",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

private fun validateInputs(
    fullName: String,
    position: String,
    documentNumber: String,
    phone: String,
    hourlyRate: String
): String? {
    return when {
        fullName.isBlank() -> "El nombre completo es requerido"
        position.isBlank() -> "El cargo/posición es requerido"
        documentNumber.isBlank() -> "El número de documento es requerido"
        phone.isBlank() -> "El teléfono es requerido"
        hourlyRate.isBlank() -> "La tarifa por hora es requerida"
        else -> {
            val rate = hourlyRate.toDoubleOrNull()
            when {
                rate == null -> "La tarifa debe ser un número válido"
                rate <= 0 -> "La tarifa debe ser mayor a 0"
                else -> null
            }
        }
    }
}
