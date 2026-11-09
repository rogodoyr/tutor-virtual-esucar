package cl.esucar.tutor.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.esucar.tutor.domain.model.Rol
import cl.esucar.tutor.ui.components.CampoValidado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegistroOk: () -> Unit,
    onVolver: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
) {
    val estado by viewModel.registro.collectAsStateWithLifecycle()

    LaunchedEffect(estado.registroExitoso) {
        if (estado.registroExitoso) onRegistroOk()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Registra tu cuenta institucional. Los datos se guardan de forma segura en el dispositivo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            CampoValidado(
                valor = estado.nombre,
                onValorChange = viewModel::onRegistroNombreChange,
                etiqueta = "Nombre y apellido",
                error = estado.errores.nombre,
                iconoInicial = Icons.Default.Person
            )

            CampoValidado(
                valor = estado.email,
                onValorChange = viewModel::onRegistroEmailChange,
                etiqueta = "Correo institucional",
                error = estado.errores.email,
                iconoInicial = Icons.Default.Email
            )

            CampoValidado(
                valor = estado.password,
                onValorChange = viewModel::onRegistroPasswordChange,
                etiqueta = "Contrasena (min. 6: letra, numero y caracter especial)",
                error = estado.errores.password,
                iconoInicial = Icons.Default.Lock,
                transformacionVisual = PasswordVisualTransformation()
            )

            CampoValidado(
                valor = estado.confirmacion,
                onValorChange = viewModel::onRegistroConfirmacionChange,
                etiqueta = "Confirmar contrasena",
                error = estado.errores.confirmacion,
                iconoInicial = Icons.Default.Lock,
                transformacionVisual = PasswordVisualTransformation()
            )

            DesplegableRol(
                valor = estado.rol,
                error = estado.errores.rol,
                onSeleccion = viewModel::onRegistroRolChange
            )

            AnimatedVisibility(
                visible = estado.mensajeGeneral != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.errorContainer,
                            MaterialTheme.shapes.medium
                        )
                        .padding(12.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        estado.mensajeGeneral ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Button(
                onClick = viewModel::registrar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear cuenta e ingresar")
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DesplegableRol(
    valor: Rol,
    error: String?,
    onSeleccion: (Rol) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it }
    ) {
        OutlinedTextField(
            value = valor.etiqueta,
            onValueChange = {},
            readOnly = true,
            label = { Text("Perfil de usuario") },
            isError = error != null,
            leadingIcon = { Icon(Icons.Default.AccountBox, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            supportingText = {
                if (error != null) Text(error, color = MaterialTheme.colorScheme.error)
            },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            Rol.entries.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion.etiqueta) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}
