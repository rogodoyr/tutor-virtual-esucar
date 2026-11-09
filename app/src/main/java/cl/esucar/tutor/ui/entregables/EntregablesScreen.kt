package cl.esucar.tutor.ui.entregables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.esucar.tutor.domain.model.Entregable
import cl.esucar.tutor.domain.model.EstadoEntregable
import cl.esucar.tutor.ui.components.EstadoChip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntregablesScreen(
    onNuevoEntregable: () -> Unit,
    viewModel: EntregablesViewModel = viewModel(factory = EntregablesViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis entregables") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevoEntregable) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo entregable")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = estado.filtro == null,
                        onClick = { viewModel.filtrarPor(null) },
                        label = { Text("Todas") }
                    )
                }
                items(EstadoEntregable.entries.toList()) { opcion ->
                    FilterChip(
                        selected = estado.filtro == opcion,
                        onClick = {
                            viewModel.filtrarPor(if (estado.filtro == opcion) null else opcion)
                        },
                        label = { Text(opcion.etiqueta) }
                    )
                }
            }

            if (estado.filtrados.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    Spacer(Modifier.height(32.dp))
                    Text(
                        if (estado.todos.isEmpty())
                            "Aun no hay entregables registrados. Usa el boton + para crear el primero."
                        else
                            "No hay entregables con el filtro seleccionado.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(estado.filtrados, key = { it.id }) { entregable ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(350)) + slideInVertically(tween(350), initialOffsetY = { it / 4 })
                        ) {
                            TarjetaEntregable(
                                entregable = entregable,
                                onAvanzar = { viewModel.avanzarEstado(entregable) },
                                onEliminar = { viewModel.eliminar(entregable) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaEntregable(
    entregable: Entregable,
    onAvanzar: () -> Unit,
    onEliminar: () -> Unit
) {
    val formatoFecha = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    entregable.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    entregable.etapa,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Fecha limite: ${formatoFecha.format(Date(entregable.fechaLimite))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.height(8.dp))
                EstadoChip(estado = entregable.estado)
            }
            Column {
                IconButton(onClick = onAvanzar) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Avanzar estado",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onEliminar) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar entregable",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
