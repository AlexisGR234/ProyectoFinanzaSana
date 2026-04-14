package com.ailyn.finanzasana.features.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ailyn.finanzasana.features.home.presentation.viewmodel.DetalleDeudaUiState
import com.ailyn.finanzasana.features.home.presentation.viewmodel.DetalleDeudaViewModel
import com.ailyn.finanzasana.features.home.data.model.DeudaRequest
import java.time.LocalDate
import com.ailyn.finanzasana.core.utils.ImageUtils
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import android.location.Geocoder
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleDeudaScreen(
    idDeuda: Int,
    onBack: () -> Unit,
    viewModel: DetalleDeudaViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var mostrarRegistroAbono by remember { mutableStateOf(false) }
    var montoAbono by remember { mutableStateOf("") }

    // LaunchedEffect para cargar los datos solo una vez al entrar
    LaunchedEffect(idDeuda) {
        viewModel.cargarDetalle(idDeuda)
    }

    LaunchedEffect(state) {
        if (state is DetalleDeudaUiState.Deleted) {
            onBack()
        }
    }

    var mostrarConfirmarEliminar by remember { mutableStateOf(false) }
    var mostrarFormularioEditar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle de Deuda", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    if (state is DetalleDeudaUiState.Success) {
                        IconButton(onClick = { mostrarFormularioEditar = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                        }
                        IconButton(onClick = { mostrarConfirmarEliminar = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF2D62FF))
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF3F6F9))) {
            when (val status = state) {
                is DetalleDeudaUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF2D62FF)
                    )
                }
                is DetalleDeudaUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(status.message, color = Color.Red)
                        Button(onClick = { viewModel.cargarDetalle(idDeuda) }) {
                            Text("Reintentar")
                        }
                    }
                }
                is DetalleDeudaUiState.Success -> {
                    val deuda = status.deuda

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Mostrar imagen de evidencia si existe
                        deuda.fotoBase64?.let { base64 ->
                            val bitmap = remember(base64) { ImageUtils.decodeBase64ToBitmap(base64) }
                            bitmap?.let { btm ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().height(250.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Image(
                                        bitmap = btm.asImageBitmap(),
                                        contentDescription = "Evidencia de la deuda",
                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        // Tarjeta de Información Principal
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                val estaVencida = try {
                                    val fechaVencimiento = LocalDate.parse(deuda.fechaVencimiento)
                                    fechaVencimiento.isBefore(LocalDate.now())
                                } catch (e: Exception) {
                                    false
                                }
                                val colorVencimiento = if (estaVencida) Color.Red else Color.DarkGray
                                val textoVencimiento = if (estaVencida) "${deuda.fechaVencimiento} (¡VENCIDA!)" else deuda.fechaVencimiento

                                Text(deuda.concepto, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = if (estaVencida) Color.Red else Color.Unspecified)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(deuda.categoria, color = Color(0xFF2D62FF), fontWeight = FontWeight.SemiBold)

                                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

                                DetalleFila("Vencimiento", textoVencimiento, valorColor = colorVencimiento)
                                DetalleFila("Tasa de interés", "${deuda.tasaInteres ?: 0}% anual")
                                if (deuda.latitud != null && deuda.longitud != null) {
                                    val context = LocalContext.current
                                    var direccion by remember { mutableStateOf<String?>(null) }
                                    
                                    LaunchedEffect(deuda.latitud, deuda.longitud) {
                                        try {
                                            val geocoder = Geocoder(context, Locale.getDefault())
                                            @Suppress("DEPRECATION")
                                            val addresses = geocoder.getFromLocation(deuda.latitud, deuda.longitud, 1)
                                            val address = addresses?.firstOrNull()
                                            if (address != null) {
                                                direccion = address.getAddressLine(0)
                                            }
                                        } catch (e: Exception) {
                                            // Handle case where geocoder is not available
                                        }
                                    }
                                    
                                    val textoUbicacion = if (!direccion.isNullOrBlank()) {
                                        "$direccion\n(${deuda.latitud}, ${deuda.longitud})"
                                    } else {
                                        "${deuda.latitud}, ${deuda.longitud}"
                                    }
                                    DetalleFila("Ubicación", textoUbicacion)
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Text("Saldo actual", color = Color.Gray, fontSize = 14.sp)
                                Text("$${String.format("%,.2f", deuda.saldoActual)}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D62FF))
                                Text("de $${String.format("%,.2f", deuda.montoOriginal)} original", color = Color.Gray, fontSize = 13.sp)
                            }
                        }

                        // Progreso Visual
                        SeccionCard("Progreso de pago") {
                            val progreso = (deuda.porcentajePagado.toFloat() / 100f).coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { progreso },
                                modifier = Modifier.fillMaxWidth().height(10.dp),
                                color = Color(0xFF2D62FF),
                                trackColor = Color(0xFFE0E0E0),
                                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                            Text(
                                "${deuda.porcentajePagado.toInt()}% liquidado",
                                modifier = Modifier.align(Alignment.End).padding(top = 8.dp),
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }

                        // Historial de Pagos
                        SeccionCard("Historial de Abonos") {
                            if (deuda.abonos.isEmpty()) {
                                Text("Aún no has registrado pagos", color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                            } else {
                                deuda.abonos.forEach { abono ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(abono.fecha, color = Color.Gray)
                                        Text("$${String.format("%,.2f", abono.monto)}", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                    }
                                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF0F0F0))
                                }
                            }
                        }

                        // Lógica de Registro de Abono
                        if (mostrarRegistroAbono) {
                            val montoAbonoDouble = montoAbono.toDoubleOrNull() ?: 0.0
                            val excedeSaldo = montoAbonoDouble > deuda.saldoActual
                            val abonoValido = montoAbonoDouble > 0 && !excedeSaldo

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2D62FF))
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text("Registrar nuevo pago", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    OutlinedTextField(
                                        value = montoAbono,
                                        onValueChange = { input ->
                                            // Solo permitir dígitos y punto decimal
                                            if (input.all { it.isDigit() || it == '.' }) {
                                                val valorIngresado = input.toDoubleOrNull() ?: 0.0
                                                // Si supera el saldo, lo recortamos al máximo exacto
                                                montoAbono = if (valorIngresado > deuda.saldoActual) {
                                                    String.format("%.2f", deuda.saldoActual)
                                                } else {
                                                    input
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        label = { Text("Monto") },
                                        prefix = { Text("$ ") },
                                        supportingText = {
                                            Text(
                                                text = "Máximo: $${String.format("%,.2f", deuda.saldoActual)}",
                                                color = if (excedeSaldo) Color.Red else Color.Gray,
                                                fontSize = 12.sp
                                            )
                                        },
                                        isError = excedeSaldo,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(onClick = {
                                            mostrarRegistroAbono = false
                                            montoAbono = ""
                                        }) { Text("Cancelar") }

                                        Button(
                                            onClick = {
                                                if (abonoValido) {
                                                    viewModel.registrarAbono(deuda.id, montoAbonoDouble)
                                                    mostrarRegistroAbono = false
                                                    montoAbono = ""
                                                }
                                            },
                                            enabled = abonoValido,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D62FF))
                                        ) { Text("Confirmar Pago") }
                                    }
                                }
                            }
                        } else {
                            Button(
                                onClick = { mostrarRegistroAbono = true },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D62FF))
                            ) {
                                Icon(Icons.Default.Add, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Registrar Abono", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
                is DetalleDeudaUiState.Deleted -> {
                    // No UI needed, it will navigate back automatically
                }
            }
        }
    }

    if (mostrarConfirmarEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmarEliminar = false },
            title = { Text("Eliminar Deuda") },
            text = { Text("¿Estás seguro de que deseas eliminar esta deuda? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmarEliminar = false
                        viewModel.eliminarDeuda(idDeuda)
                    }
                ) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }

    if (mostrarFormularioEditar && state is DetalleDeudaUiState.Success) {
        val deuda = (state as DetalleDeudaUiState.Success).deuda
        var concepto by remember { mutableStateOf(deuda.concepto) }
        var monto by remember { mutableStateOf(deuda.montoOriginal.toString()) }
        var tasa by remember { mutableStateOf((deuda.tasaInteres ?: 0.0).toString()) }
        var fecha by remember { mutableStateOf(deuda.fechaVencimiento) }

        ModalBottomSheet(
            onDismissRequest = { mostrarFormularioEditar = false },
            containerColor = Color(0xFFF3F6F9),
            dragHandle = null
        ) {
            Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF3F6F9))) {
                CenterAlignedTopAppBar(
                    title = { Text("Editar Deuda", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { mostrarFormularioEditar = false }) {
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
                    OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Vencimiento (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())

                    // Manejo de la imagen
                    var fotoEditada by remember { mutableStateOf(false) }
                    var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
                    
                    val cameraLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.TakePicturePreview()
                    ) { bitmap ->
                        if (bitmap != null) {
                            fotoBitmap = bitmap
                            fotoEditada = true
                        }
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Evidencia (Opcional)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Si se tomó una nueva, mostramos esa. Si no, mostramos la antigua si existe y no la han borrado.
                        val mostrarNuevaPreview = fotoEditada && fotoBitmap != null
                        val mostrarViejaPreview = !fotoEditada && !deuda.fotoBase64.isNullOrEmpty()
                        
                        if (mostrarNuevaPreview || mostrarViejaPreview) {
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                                val bitmapAMostrar = if (mostrarNuevaPreview) {
                                    fotoBitmap!!.asImageBitmap()
                                } else {
                                    ImageUtils.decodeBase64ToBitmap(deuda.fotoBase64!!)?.asImageBitmap()
                                }
                                
                                if (bitmapAMostrar != null) {
                                    Image(
                                        bitmap = bitmapAMostrar,
                                        contentDescription = "Vista previa",
                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { 
                                            fotoBitmap = null 
                                            fotoEditada = true 
                                        },
                                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Close, null, tint = Color.White)
                                    }
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
                                Text("Tomar Nueva Foto")
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (concepto.isNotBlank() && monto.isNotBlank() && fecha.isNotBlank()) {
                                val fotoFinalBase64 = if (fotoEditada) {
                                    fotoBitmap?.let { ImageUtils.encodeBitmapToBase64(it) }
                                } else {
                                    deuda.fotoBase64
                                }
                                
                                viewModel.actualizarDeuda(
                                    idDeuda,
                                    DeudaRequest(
                                        concepto = concepto,
                                        montoOriginal = monto.toDoubleOrNull() ?: 0.0,
                                        tasaInteres = tasa.toDoubleOrNull(),
                                        idCategoria = 1, // ID categoría por defecto
                                        fechaVencimiento = fecha,
                                        fotoBase64 = fotoFinalBase64,
                                        latitud = deuda.latitud,
                                        longitud = deuda.longitud
                                    )
                                )
                                mostrarFormularioEditar = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D62FF))
                    ) { Text("Guardar Cambios") }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun DetalleFila(label: String, valor: String, valorColor: Color = Color.DarkGray) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label: ", color = Color.Gray, fontSize = 15.sp)
        Text(valor, color = valorColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SeccionCard(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            contenido()
        }
    }
}