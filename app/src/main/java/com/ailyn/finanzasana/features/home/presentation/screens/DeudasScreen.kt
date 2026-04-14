package com.ailyn.finanzasana.features.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ailyn.finanzasana.features.home.domain.model.Deuda
import com.ailyn.finanzasana.features.home.data.model.CategoriaResponse
import com.ailyn.finanzasana.features.home.data.model.DeudaRequest
import com.ailyn.finanzasana.features.home.presentation.viewmodel.DeudasUiState
import com.ailyn.finanzasana.features.home.presentation.viewmodel.DeudasViewModel
import java.time.LocalDate
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import com.ailyn.finanzasana.core.utils.ImageUtils
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import android.location.Geocoder
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeudasScreen(
    onUserClick: () -> Unit,
    onDeudaClick: (Int) -> Unit,
    onNavigateToPlanificador: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: DeudasViewModel = hiltViewModel()
){
    val state by viewModel.uiState.collectAsState()
    val userRol by viewModel.userRol.collectAsState()
    val isAdmin = userRol == 1
    var mostrarFormulario by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF3F6F9))) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderHome(
                onUserClick = onUserClick,
                isAdmin = isAdmin,
                onCerrarSesion = onCerrarSesion
            )

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(16.dp))

                when (val currentState = state) {
                    is DeudasUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF2D62FF))
                        }
                    }
                    is DeudasUiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.ErrorOutline, null, tint = Color.Red, modifier = Modifier.size(48.dp))
                            Text("Error: ${currentState.message}", color = Color.Red, modifier = Modifier.padding(16.dp))
                            // CORRECCIÓN: Botón de reintento funcional
                            Button(onClick = { viewModel.cargarDeudas() }) {
                                Text("Reintentar conexión")
                            }
                        }
                    }
                    is DeudasUiState.Success -> {
                        // Mostrar solo deudas con saldo pendiente (> 0)
                        val deudasActivas = currentState.deudas.filter { it.saldoActual > 0 }

                        ResumenSaldoCard(deudas = deudasActivas)
                        Spacer(modifier = Modifier.height(24.dp))

                        if (deudasActivas.isEmpty()) {
                            Text("No tienes deudas registradas", modifier = Modifier.padding(top = 20.dp), color = Color.Gray)
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 100.dp)
                            ) {
                                items(deudasActivas) { deuda ->
                                    DeudaItem(deuda, onClick = { onDeudaClick(deuda.id) })
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { mostrarFormulario = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = Color(0xFF2D62FF),
            contentColor = Color.White,
            shape = CircleShape
        ) { Icon(Icons.Default.Add, null) }

        if (mostrarFormulario) {
            ModalBottomSheet(
                onDismissRequest = { mostrarFormulario = false },
                sheetState = sheetState,
                containerColor = Color(0xFFF3F6F9),
                dragHandle = null,
                shape = RectangleShape
            ) {
                FormularioNuevaDeuda(
                    categorias = viewModel.categorias.collectAsState().value,
                    onDismiss = { mostrarFormulario = false },
                    onGuardar = { request ->
                        viewModel.registrarNuevaDeuda(request)
                        mostrarFormulario = false
                    }
                )
            }
        }
    }
}

// --- COMPONENTES SECUNDARIOS (Sin cambios en diseño, solo correcciones lógicas) ---

@Composable
fun HeaderHome(
    onUserClick: () -> Unit,
    isAdmin: Boolean = false,
    onCerrarSesion: () -> Unit = {}
) {
    var mostrarDialogoLogout by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth().height(130.dp).background(Color(0xFF2D62FF)).padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mis Finanzas", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Solo los administradores (rol = 1) ven el acceso al panel de control
                if (isAdmin) {
                    IconButton(onClick = onUserClick) {
                        Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.2f)) {
                            Icon(Icons.Default.Settings, null, modifier = Modifier.padding(8.dp), tint = Color.White)
                        }
                    }
                }
                // Botón de cerrar sesión (visible para todos los usuarios)
                IconButton(onClick = { mostrarDialogoLogout = true }) {
                    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.2f)) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión", modifier = Modifier.padding(8.dp), tint = Color.White)
                    }
                }
            }
        }
    }

    // Diálogo de confirmación de cierre de sesión
    if (mostrarDialogoLogout) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLogout = false },
            icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFF2D62FF)) },
            title = { Text("Cerrar sesión", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas cerrar tu sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoLogout = false
                        onCerrarSesion()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D62FF))
                ) { Text("Cerrar sesión") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoLogout = false }) {
                    Text("Cancelar", color = Color(0xFF2D62FF))
                }
            }
        )
    }
}

@Composable
fun ResumenSaldoCard(deudas: List<Deuda>) {
    val totalMonto = deudas.sumOf { it.saldoActual }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Total Adeudado", color = Color.Gray)
            Text("$${String.format("%,.2f", totalMonto)}", color = Color(0xFF2D62FF), fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text("${deudas.size} deudas activas", color = Color.Gray)
        }
    }
}

@Composable
fun DeudaItem(deuda: Deuda, onClick: () -> Unit) {
    val estaVencida = try {
        val fechaVencimiento = LocalDate.parse(deuda.fechaVencimiento)
        fechaVencimiento.isBefore(LocalDate.now())
    } catch (e: Exception) {
        false
    }
    
    val colorVencimiento = if (estaVencida) Color.Red else Color.Gray
    val fontWeightVencimiento = if (estaVencida) FontWeight.Bold else FontWeight.Normal

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(deuda.concepto, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (estaVencida) Color.Red else Color.Unspecified)
                Text(deuda.categoria, color = Color.Gray, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$${String.format("%,.2f", deuda.saldoActual)}", fontWeight = FontWeight.Bold, color = Color(0xFF2D62FF))
                Text(deuda.fechaVencimiento, color = colorVencimiento, fontSize = 12.sp, fontWeight = fontWeightVencimiento)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioNuevaDeuda(
    categorias: List<CategoriaResponse>,
    onDismiss: () -> Unit,
    onGuardar: (DeudaRequest) -> Unit
) {
    var concepto by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var tasa by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }

    // Estado del selector de categoría
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaResponse?>(null) }
    var menuExpandido by remember { mutableStateOf(false) }

    // Estado para la foto
    var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotoBitmap = bitmap
        }
    }

    // Estado y lógica para GPS (Ubicación)
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var latitud by remember { mutableStateOf<Double?>(null) }
    var longitud by remember { mutableStateOf<Double?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocation = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocation = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineLocation || coarseLocation) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        latitud = it.latitude
                        longitud = it.longitude
                    }
                }
            } catch (e: SecurityException) {
                // Should not happen since we just checked permission
            }
        }
    }

    // Solicitar permiso de ubicación al abrir el formulario
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF3F6F9))) {
        CenterAlignedTopAppBar(
            title = { Text("Nueva Deuda", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF2D62FF))
        )

        Column(
            modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = concepto, onValueChange = { concepto = it }, label = { Text("Concepto") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = monto, onValueChange = { monto = it }, label = { Text("Monto") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = tasa, onValueChange = { tasa = it }, label = { Text("Tasa (%)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

            // Selector de categoría con dropdown
            ExposedDropdownMenuBox(
                expanded = menuExpandido,
                onExpandedChange = { menuExpandido = it }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false }
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.nombre) },
                            onClick = {
                                categoriaSeleccionada = categoria
                                menuExpandido = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Vencimiento (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())

            // Sección de Foto
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Evidencia (Opcional)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                
                if (fotoBitmap != null) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                        Image(
                            bitmap = fotoBitmap!!.asImageBitmap(),
                            contentDescription = "Vista previa",
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { fotoBitmap = null },
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White)
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { cameraLauncher.launch() },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2D62FF))
                    ) {
                        Icon(Icons.Default.PhotoCamera, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomar Foto del Objeto")
                    }
                }
            }
            
            // Ubicación visual en el formulario
            if (latitud != null && longitud != null) {
                var direccionForm by remember { mutableStateOf<String?>(null) }
                LaunchedEffect(latitud, longitud) {
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(latitud!!, longitud!!, 1)
                        val address = addresses?.firstOrNull()
                        if (address != null) {
                            direccionForm = address.getAddressLine(0)
                        }
                    } catch (e: Exception) {}
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Ubicación", tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    val texto = if (!direccionForm.isNullOrBlank()) "Ubicación: $direccionForm" else "GPS: $latitud, $longitud"
                    Text(texto, color = Color(0xFF4CAF50), fontSize = 12.sp)
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Sin ubicación", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Obteniendo ubicación GPS...", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Button(
                onClick = {
                    if (concepto.isNotBlank() && monto.isNotBlank() && fecha.isNotBlank() && categoriaSeleccionada != null) {
                        onGuardar(DeudaRequest(
                            concepto = concepto,
                            montoOriginal = monto.toDoubleOrNull() ?: 0.0,
                            tasaInteres = tasa.toDoubleOrNull(),
                            idCategoria = categoriaSeleccionada!!.id,
                            fechaVencimiento = fecha,
                            fotoBase64 = fotoBitmap?.let { ImageUtils.encodeBitmapToBase64(it) },
                            latitud = latitud,
                            longitud = longitud
                        ))
                    }
                },
                enabled = concepto.isNotBlank() && monto.isNotBlank() && fecha.isNotBlank() && categoriaSeleccionada != null,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D62FF))
            ) { Text("Guardar Deuda") }
        }
    }
}