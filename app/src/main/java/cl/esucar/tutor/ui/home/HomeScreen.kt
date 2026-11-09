package cl.esucar.tutor.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import cl.esucar.tutor.ui.components.BarraProgresoAnimada
import cl.esucar.tutor.ui.components.EstadoChip
import cl.esucar.tutor.ui.components.TarjetaAccion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onIrAGuia: () -> Unit,
    onIrAFaq: () -> Unit,
    onNuevoEntregable: () -> Unit,
    onVerEntregables: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val progreso = if (estado.totalEntregables == 0) 0f
    else estado.aprobados.toFloat() / estado.totalEntregables

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tutor Virtual ESUCAR") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(400)) + slideInVertically(animationSpec = tween(400), initialOffsetY = { it / 2 })
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text(
                                text = if (estado.perfil.nombre.isBlank()) "Bienvenido/a"
                                else "Hola, ${estado.perfil.nombre}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Tu tutor virtual para Trabajo de Titulo I, II y III. " +
                                    "Avanza paso a paso y cumple tus hitos de entrega.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "Progreso de tus entregables",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(12.dp))
                        BarraProgresoAnimada(progreso = progreso)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "${estado.aprobados} de ${estado.totalEntregables} entregables aprobados " +
                                "(${(progreso * 100).toInt()}%)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            item {
                Text(
                    "Accesos rapidos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                TarjetaAccion(
                    titulo = "Guia metodologica",
                    descripcion = "Etapa a etapa, con formato y criterios",
                    icono = Icons.AutoMirrored.Filled.List,
                    onClick = onIrAGuia,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                TarjetaAccion(
                    titulo = "Preguntas frecuentes",
                    descripcion = "Resuelve tus dudas de metodologia y formato",
                    icono = Icons.Default.Search,
                    onClick = onIrAFaq,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                TarjetaAccion(
                    titulo = "Registrar entregable",
                    descripcion = "Crea un hito con fecha limite y recordatorio",
                    icono = Icons.Default.Add,
                    onClick = onNuevoEntregable,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Proximos entregables",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    androidx.compose.material3.TextButton(onClick = onVerEntregables) {
                        Text("Ver todos")
                    }
                }
            }

            if (estado.proximos.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Aun no registras entregables. Crea el primero desde " +
                                    "\"Registrar entregable\".",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(estado.proximos, key = { it.id }) { entregable ->
                    TarjetaEntregableResumen(entregable)
                }
            }
        }
    }
}

@Composable
private fun TarjetaEntregableResumen(entregable: Entregable) {
    val formatoFecha = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(entregable.titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${entregable.etapa} - ${formatoFecha.format(Date(entregable.fechaLimite))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            EstadoChip(estado = entregable.estado)
        }
    }
}
