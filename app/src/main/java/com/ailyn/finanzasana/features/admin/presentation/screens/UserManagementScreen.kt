package com.ailyn.finanzasana.features.admin.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ailyn.finanzasana.features.admin.domain.model.UserAdmin
import com.ailyn.finanzasana.features.admin.presentation.viewmodel.UserManagementUiState
import com.ailyn.finanzasana.features.admin.presentation.viewmodel.UserManagementViewModel

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.ailyn.finanzasana.core.hardware.biometric.BiometricHelper
import com.ailyn.finanzasana.core.hardware.biometric.BiometricResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onBackClick: () -> Unit,
    viewModel: UserManagementViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    var searchQuery by remember { mutableStateOf("") }
    
    val context = LocalContext.current as FragmentActivity
    var isAuthenticated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val canAuth = BiometricHelper.canAuthenticate(context)
        if (canAuth == BiometricResult.Success) {
            BiometricHelper.authenticate(
                activity = context,
                title = "Acceso Restringido",
                subtitle = "Usa tu huella para gestionar usuarios",
                negativeButtonText = "Cancelar",
                onResult = { result ->
                    when (result) {
                        is BiometricResult.Success -> {
                            isAuthenticated = true
                        }
                        else -> {
                            Toast.makeText(context, "Acceso denegado", Toast.LENGTH_SHORT).show()
                            onBackClick() // Expulsa al usuario si cancela o falla
                        }
                    }
                }
            )
        } else {
            // Si el teléfono no tiene hardware o no hay huellas registradas, lo dejamos pasar por ahora
            // (Para que no se rompa tu aplicación si la pruebas en un emulador sin huella)
            isAuthenticated = true
        }
    }

    if (!isAuthenticated) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Gestión de Usuarios", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF2D62FF))
                )
            },
            containerColor = Color(0xFFF3F6F9)
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Lock, contentDescription = "Bloqueado", modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Esperando autenticación...", color = Color.Gray)
                }
            }
        }
        return // Detiene la ejecución aquí hasta que se autentique
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Gestión de Usuarios", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF2D62FF))
            )
        },
        containerColor = Color(0xFFF3F6F9)
    ) { padding ->
        when (val currentState = state) {
            is UserManagementUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2D62FF))
                }
            }
            is UserManagementUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${currentState.message}", color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadUsers() }, modifier = Modifier.padding(top = 8.dp)) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            is UserManagementUiState.Success -> {
                LaunchedEffect(Unit) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }

                UserManagementScreenContent(
                    usuarios = currentState.usuarios,
                    searchQuery = searchQuery,
                    onSearchChange = {
                        searchQuery = it
                        viewModel.filterUsers(it) // Filtrado en el ViewModel
                    },
                    onDeleteUser = { id -> viewModel.deleteUser(id) },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun UserManagementScreenContent(
    usuarios: List<UserAdmin>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onDeleteUser: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Buscar por nombre o correo") },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF2D62FF)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            singleLine = true
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            if (usuarios.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No se encontraron usuarios", color = Color.Gray)
                    }
                }
            } else {
                items(usuarios, key = { it.id }) { user ->
                    UserItemCard(
                        user = user,
                        onDelete = { onDeleteUser(user.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun UserItemCard(user: UserAdmin, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar usuario?") },
            text = { Text("Esta acción eliminará a ${user.nombre} y todas sus deudas de forma permanente.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(45.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF3F6F9)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(user.nombre.take(1).uppercase(), fontWeight = FontWeight.Bold, color = Color(0xFF2D62FF))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(user.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(user.email, color = Color.Gray, fontSize = 12.sp)
                Text(
                    text = "${user.totalDeudas} deudas activas",
                    fontSize = 12.sp,
                    color = if (user.totalDeudas > 3) Color.Red else Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.DeleteSweep, null, tint = Color.Red)
            }
        }
    }
}