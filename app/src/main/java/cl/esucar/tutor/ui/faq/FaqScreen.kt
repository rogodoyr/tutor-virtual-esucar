package cl.esucar.tutor.ui.faq

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.esucar.tutor.domain.model.PreguntaFaq

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(
    viewModel: FaqViewModel = viewModel(factory = FaqViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val expandidas = remember { mutableStateMapOf<Int, Boolean>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preguntas frecuentes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = estado.consulta,
                onValueChange = viewModel::onConsultaChange,
                label = { Text("Buscar duda (ej: APA, muestra, formato)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (estado.consulta.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onConsultaChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar busqueda")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estado.categorias) { categoria ->
                    FilterChip(
                        selected = estado.categoria == categoria,
                        onClick = { viewModel.onCategoriaChange(categoria) },
                        label = { Text(categoria) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (estado.resultados.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "No se encontraron preguntas para tu busqueda. " +
                                    "Prueba con otras palabras o cambia de categoria.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                items(estado.resultados, key = { it.id }) { pregunta ->
                    TarjetaFaq(
                        pregunta = pregunta,
                        expandida = expandidas[pregunta.id] == true,
                        onAlternar = {
                            expandidas[pregunta.id] = !(expandidas[pregunta.id] ?: false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaFaq(pregunta: PreguntaFaq, expandida: Boolean, onAlternar: () -> Unit) {
    val rotacion by animateFloatAsState(
        targetValue = if (expandida) 180f else 0f,
        animationSpec = tween(250),
        label = "rotacionFlecha"
    )

    Card(
        onClick = onAlternar,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(300))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        pregunta.pregunta,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        pregunta.categoria,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expandida) "Contraer" else "Expandir",
                    modifier = Modifier.rotate(rotacion)
                )
            }

            AnimatedVisibility(
                visible = expandida,
                enter = fadeIn(tween(250)) + expandVertically(),
                exit = fadeOut(tween(200)) + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        pregunta.respuesta,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
