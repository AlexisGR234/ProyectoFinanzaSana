package com.ailyn.finanzasana.features.admin.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ailyn.finanzasana.features.admin.presentation.viewmodel.AdminUiState
import com.ailyn.finanzasana.features.admin.presentation.viewmodel.AdminViewModel
import androidx.compose.foundation.shape.CircleShape
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToUsers: () -> Unit,
    onBackClick: () -> Unit, // Añadido para navegación atrás
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Panel de Control", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF2D62FF)),
                actions = {
                    IconButton(onClick = onNavigateToUsers) {
                        // Cambiado a PersonSearch (estándar) para evitar errores de librería
                        Icon(Icons.Default.PersonSearch, "Gestionar Usuarios", tint = Color.White)
                    }
                }
            )
        },
        containerColor = Color(0xFFF3F6F9)
    ) { padding ->
        when (val currentState = state) {
            is AdminUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2D62FF))
                }
            }
            is AdminUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CloudOff, null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Error: ${currentState.message}", color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadDashboardData() }, modifier = Modifier.padding(top = 16.dp)) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            is AdminUiState.Success -> {
                // Feedback táctil una sola vez al cargar datos con éxito
                LaunchedEffect(currentState) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }

                AdminDashboardContent(
                    modifier = Modifier.padding(padding),
                    data = currentState
                )
            }
        }
    }
}

@Composable
fun AdminDashboardContent(
    modifier: Modifier = Modifier,
    data: AdminUiState.Success
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        item {
            Text("Métricas Globales", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2D3748))
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Person es el icono estándar para usuarios
                MetricaCard("Usuarios", data.metrics.usuariosTotales.toString(), Icons.Default.Person, Modifier.weight(1f))

                // Formateo de moneda para el monto global
                val montoFormateado = String.format("%,.0f", data.metrics.montoGlobal)
                MetricaCard("Monto Global", "$$montoFormateado", Icons.Default.Payments, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricaCard("Deudas Vencidas", data.metrics.deudasVencidas.toString(), Icons.Default.Warning, Modifier.weight(1f))
                MetricaCard("Actividades", data.activities.size.toString(), Icons.Default.List, Modifier.weight(1f))
            }
        }

        item {
            Text("Actividad Reciente", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
        }

        if (data.activities.isEmpty()) {
            item {
                Text("No hay actividad reciente para mostrar.", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            items(data.activities) { actividad ->
                ActividadCard(usuario = actividad.usuario, accion = actividad.accion, fecha = actividad.fecha)
            }
        }
    }
}

@Composable
fun MetricaCard(titulo: String, valor: String, icono: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icono, null, tint = Color(0xFF2D62FF), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(valor, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(titulo, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun ActividadCard(usuario: String, accion: String, fecha: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = Color(0xFFF3F6F9), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Info, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("$usuario $accion", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(fecha, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}