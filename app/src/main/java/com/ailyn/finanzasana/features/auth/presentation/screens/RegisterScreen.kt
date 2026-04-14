package com.ailyn.finanzasana.features.auth.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ailyn.finanzasana.features.auth.presentation.viewmodel.AuthUiState
import com.ailyn.finanzasana.features.auth.presentation.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    val state by viewModel.uiState.collectAsState()

    // Navegación automática al tener éxito
    LaunchedEffect(state) {
        if (state is AuthUiState.Success) {
            onNavigateToLogin()
            viewModel.resetState() // Limpiamos el estado para que no se quede en "Success"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Crear Cuenta",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF2D62FF)
        )
        Text(
            text = "Únete a FinanzaSana hoy mismo",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                if (state is AuthUiState.Error) viewModel.resetState()
            },
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFF2D62FF)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            enabled = state !is AuthUiState.Loading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (state is AuthUiState.Error) viewModel.resetState()
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF2D62FF)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            enabled = state !is AuthUiState.Loading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo Teléfono
        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it
                if (state is AuthUiState.Error) viewModel.resetState()
            },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(Icons.Default.Phone, null, tint = Color(0xFF2D62FF)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            enabled = state !is AuthUiState.Loading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                if (state is AuthUiState.Error) viewModel.resetState()
            },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF2D62FF)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (nombre.isNotBlank() && email.isNotBlank() && contrasena.isNotBlank()) {
                        viewModel.registrar(nombre, email, contrasena, telefono)
                    }
                }
            ),
            enabled = state !is AuthUiState.Loading
        )

        // Manejo de Error (Área fija)
        Box(modifier = Modifier.height(40.dp).padding(top = 8.dp), contentAlignment = Alignment.Center) {
            if (state is AuthUiState.Error) {
                Text(
                    text = (state as AuthUiState.Error).message,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // Botón de Registro
        Button(
            onClick = { viewModel.registrar(nombre, email, contrasena, telefono) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D62FF)),
            enabled = state !is AuthUiState.Loading &&
                    nombre.isNotBlank() && email.isNotBlank() && contrasena.isNotBlank()
        ) {
            if (state is AuthUiState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
            } else {
                Text("Registrarse", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToLogin, enabled = state !is AuthUiState.Loading) {
            Text("¿Ya tienes cuenta? Inicia sesión", color = Color(0xFF2D62FF), fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}