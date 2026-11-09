package cl.esucar.tutor.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cl.esucar.tutor.domain.model.EstadoEntregable

/**
 * Campo de formulario con validacion visual por campo: icono de advertencia,
 * mensaje de retroalimentacion animado y confirmacion visual cuando es valido.
 */
@Composable
fun CampoValidado(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    error: String?,
    modifier: Modifier = Modifier,
    iconoInicial: ImageVector? = null,
    iconoFinal: ImageVector? = null,
    onIconoFinalClick: (() -> Unit)? = null,
    unaLinea: Boolean = true,
    habilitado: Boolean = true,
    transformacionVisual: VisualTransformation = VisualTransformation.None
) {
    val iconoFinalComposable: (@Composable () -> Unit)? = when {
        error != null -> {
            {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Campo con error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        onIconoFinalClick != null && iconoFinal != null -> {
            {
                Icon(
                    iconoFinal,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        else -> null
    }

    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        isError = error != null,
        singleLine = unaLinea,
        enabled = habilitado,
        visualTransformation = transformacionVisual,
        leadingIcon = if (iconoInicial != null) {
            { Icon(iconoInicial, contentDescription = null) }
        } else {
            null
        },
        trailingIcon = iconoFinalComposable,
        supportingText = {
            AnimatedVisibility(
                visible = error != null,
                enter = fadeIn(animationSpec = tween(250)) + expandVertically(),
                exit = fadeOut(animationSpec = tween(150)) + shrinkVertically()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}

/** Indicador visual del estado de un entregable, con color animado. */
@Composable
fun EstadoChip(estado: EstadoEntregable, modifier: Modifier = Modifier) {
    val colorPorEstado = when (estado) {
        EstadoEntregable.POR_INICIAR -> MaterialTheme.colorScheme.outline
        EstadoEntregable.EN_DESARROLLO -> Color(0xFFE08A00)
        EstadoEntregable.ENVIADO -> MaterialTheme.colorScheme.primary
        EstadoEntregable.APROBADO -> Color(0xFF2E7D32)
    }
    val colorAnimado by animateColorAsState(
        targetValue = colorPorEstado,
        animationSpec = tween(500),
        label = "colorEstado"
    )

    SuggestionChip(
        onClick = {},
        label = { Text(estado.etiqueta, color = colorAnimado, fontWeight = FontWeight.SemiBold) },
        icon = {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = colorAnimado,
                modifier = Modifier.size(16.dp)
            )
        },
        modifier = modifier
    )
}

/** Barra de progreso con animacion de fluidez al cambiar de valor. */
@Composable
fun BarraProgresoAnimada(progreso: Float, modifier: Modifier = Modifier) {
    val progresoAnimado by animateFloatAsState(
        targetValue = progreso.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 900),
        label = "progreso"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = { progresoAnimado },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

/**
 * Tarjeta accionable con animacion de escala al presionar,
 * que entrega retroalimentacion visual inmediata del tacto.
 */
@Composable
fun TarjetaAccion(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.95f else 1f,
        animationSpec = tween(120),
        label = "escalaTarjeta"
    )

    Card(
        onClick = onClick,
        interactionSource = interaccion,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.scale(escala)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(descripcion, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
