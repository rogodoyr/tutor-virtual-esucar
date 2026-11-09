package cl.esucar.tutor.ui.entregables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.esucar.tutor.TutorApp
import cl.esucar.tutor.domain.model.EstadoEntregable
import cl.esucar.tutor.ui.components.CampoValidado
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntregableFormScreen(
    onVolver: () -> Unit,
    viewModel: EntregableFormViewModel = viewModel(
        factory = EntregableFormViewModel.factory(TutorApp.desde(LocalContext.current))
    )
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarPicker by remember { mutableStateOf(false) }
    val formatoFecha = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }

    LaunchedEffect(estado.guardadoExitoso) {
        if (estado.guardadoExitoso) onVolver()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo entregable") },
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
            CampoValidado(
                valor = estado.titulo,
                onValorChange = viewModel::onTituloChange,
                etiqueta = "Titulo del entregable",
                error = estado.errores.titulo,
                iconoInicial = Icons.Default.Edit
            )

            DesplegableEtapa(
                valor = estado.etapa,
                error = estado.errores.etapa,
                opciones = viewModel.etapasDisponibles(),
                onSeleccion = viewModel::onEtapaChange
            )

            CampoValidado(
                valor = estado.descripcion,
                onValorChange = viewModel::onDescripcionChange,
                etiqueta = "Descripcion (que debes entregar)",
                error = estado.errores.descripcion,
                iconoInicial = Icons.Default.Info,
                unaLinea = false,
                modifier = Modifier.height(140.dp)
            )

            OutlinedTextField(
                value = estado.fechaMillis?.let { formatoFecha.format(Date(it)) } ?: "",
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Fecha limite") },
                isError = estado.errores.fechaLimite != null,
                leadingIcon = {
                    IconButton(onClick = { mostrarPicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                    }
                },
                supportingText = {
                    if (estado.errores.fechaLimite != null) {
                        Text(estado.errores.fechaLimite!!, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text("Toca el icono de calendario para elegir la fecha")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            DesplegableEstado(
                valor = estado.estado,
                onSeleccion = viewModel::onEstadoChange
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Recordatorio de notificacion", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Te avisaremos un dia antes de la fecha limite",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = estado.recordatorioActivo,
                    onCheckedChange = viewModel::onRecordatorioChange
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::guardar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar entregable")
            }

            if (estado.mostrarErrores && estado.errores.hayErrores) {
                Text(
                    "Revisa los campos marcados antes de guardar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (mostrarPicker) {
        val estadoFecha = rememberDatePickerState(
            initialSelectedDateMillis = estado.fechaMillis
        )
        DatePickerDialog(
            onDismissRequest = { mostrarPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onFechaChange(estadoFecha.selectedDateMillis)
                    mostrarPicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarPicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estadoFecha)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DesplegableEtapa(
    valor: String,
    error: String?,
    opciones: List<String>,
    onSeleccion: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it }
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text("Etapa de la tesis") },
            isError = error != null,
            leadingIcon = { Icon(Icons.Default.List, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            supportingText = {
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DesplegableEstado(
    valor: EstadoEntregable,
    onSeleccion: (EstadoEntregable) -> Unit
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
            label = { Text("Estado del entregable") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            EstadoEntregable.entries.forEach { opcion ->
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
