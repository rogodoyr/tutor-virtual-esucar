package cl.esucar.tutor.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Tema Material 3 de la aplicacion. Soporta color dinamico (Android 12+)
 * y modo oscuro persistido por el usuario en DataStore.
 */
private val EsquemaClaro = lightColorScheme(
    primary = AzulInstitucional,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8E3F6),
    onPrimaryContainer = Color(0xFF001C3A),
    secondary = Dorado,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF5E6B8),
    onSecondaryContainer = Color(0xFF251A00),
    background = FondoClaro,
    onBackground = Color(0xFF1A1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE0E7F0),
    onSurfaceVariant = Color(0xFF43474E),
    error = RojoError,
    onError = Color.White
)

private val EsquemaOscuro = darkColorScheme(
    primary = Color(0xFFA9C7F5),
    onPrimary = Color(0xFF00315E),
    primaryContainer = AzulInstitucional,
    onPrimaryContainer = Color(0xFFD8E3F6),
    secondary = Color(0xFFE2C16B),
    onSecondary = Color(0xFF3D2E00),
    background = FondoOscuro,
    onBackground = Color(0xFFE2E2E6),
    surface = SuperficieOscura,
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF43474E),
    onSurfaceVariant = Color(0xFFC3C6CF),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410)
)

@Composable
fun TutorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val esquema = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> EsquemaOscuro
        else -> EsquemaClaro
    }

    MaterialTheme(
        colorScheme = esquema,
        typography = Typography,
        content = content
    )
}
