package com.ailyn.finanzasana.features.planificador.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.ChevronRight
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
import com.ailyn.finanzasana.features.home.domain.model.Deuda
import com.ailyn.finanzasana.features.planificador.presentation.viewmodel.PlanificadorUiState
import com.ailyn.finanzasana.features.planificador.presentation.viewmodel.PlanificadorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificadorScreen(
    onBack: () -> Unit,
    viewModel: PlanificadorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var metodoSeleccionado by remember { mutableStateOf("Bola de Nieve") }
    var ingresoExtra by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Planificador Estratégico", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF2D62FF))
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF3F6F9))) {
            when (val currentState = state) {
                is PlanificadorUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF2D62FF))
                }
                is PlanificadorUiState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(currentState.message, color = Color.Red, modifier = Modifier.padding(16.dp))
                        Button(onClick = { viewModel.refrescar() }) { Text("Reintentar") }
                    }
                }
                is PlanificadorUiState.Success -> {
                    val resultado = currentState.resultado
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Selector de Estrategia
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE9EEF5))
                        ) {
                            Row(modifier = Modifier.padding(4.dp)) {
                                TabMetodo("Bola de Nieve", metodoSeleccionado == "Bola de Nieve", Modifier.weight(1f)) {
                                    metodoSeleccionado = "Bola de Nieve"
                                    viewModel.cambiarMetodo("Bola de Nieve")
                                }
                                TabMetodo("Avalancha", metodoSeleccionado == "Avalancha", Modifier.weight(1f)) {
                                    metodoSeleccionado = "Avalancha"
                                    viewModel.cambiarMetodo("Avalancha")
                                }
                            }
                        }

                        ExplicacionMetodo(metodoSeleccionado)

                        // Entrada de Pago Extra con Lógica de Actualización
                        EntradaPagoExtra(ingresoExtra) { nuevoValor ->
                            ingresoExtra = nuevoValor
                            // Opcional: Podrías añadir un botón de "Calcular" si prefieres no saturar la API
                            // val monto = nuevoValor.toDoubleOrNull() ?: 0.0
                            // viewModel.actualizarPagoExtra(monto)
                        }

                        Text("Orden de pago recomendado", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2D3748))

                        // Lista de Deudas Ordenadas por la API
                        resultado.deudasOrdenadas.forEachIndexed { index, deuda ->
                            DeudaPlanCard(index + 1, deuda)
                        }

                        ResumenFinalCard(resultado.totalDeuda, resultado.tasaPromedio, resultado.deudasOrdenadas.size)

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TabMetodo(texto: String, seleccionado: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (seleccionado) Color.White else Color.Transparent,
            contentColor = if (seleccionado) Color(0xFF2D62FF) else Color.Gray
        ),
        elevation = if (seleccionado) ButtonDefaults.buttonElevation(2.dp) else ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(texto, fontSize = 13.sp, fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun ExplicacionMetodo(metodo: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F0FF))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoGraph, null, tint = Color(0xFF2D62FF), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (metodo == "Bola de Nieve")
                    "Prioriza deudas menores para generar victorias rápidas y motivación."
                else "Prioriza deudas con mayor interés para ahorrar dinero a largo plazo.",
                color = Color(0xFF2D3748),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun EntradaPagoExtra(valor: String, onValueChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Inversión mensual adicional", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text("Este monto acelerará tu libertad financiera", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = valor,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) onValueChange(it) },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("$ ") },
                placeholder = { Text("0.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
        }
    }
}

@Composable
fun DeudaPlanCard(posicion: Int, deuda: Deuda) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = if (posicion == 1) Color(0xFF2D62FF) else Color(0xFFF3F6F9)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "$posicion",
                        color = if (posicion == 1) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(deuda.concepto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("$${String.format("%,.2f", deuda.saldoActual)}", color = Color(0xFF2D62FF), fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${deuda.tasaInteres ?: 0}%", fontWeight = FontWeight.Medium)
                Text("Interés", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ResumenFinalCard(totalDeuda: Double, tasaPromedio: Double, cantidadDeudas: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D3748)) // Color oscuro para resaltar resumen
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Estado General", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            ResumenFilaPlan("Deudas a liquidar", "$cantidadDeudas", Color.White)
            ResumenFilaPlan("Deuda Total acumulada", "$${String.format("%,.2f", totalDeuda)}", Color.White)
            ResumenFilaPlan("Carga de interés (prom)", "${String.format("%.2f", tasaPromedio)}%", Color(0xFF63B3ED))
        }
    }
}

@Composable
fun ResumenFilaPlan(label: String, valor: String, valorColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        Text(valor, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = valorColor)
    }
}