package cl.esucar.tutor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.esucar.tutor.domain.model.Ajustes
import cl.esucar.tutor.navigation.AppNavigation
import cl.esucar.tutor.ui.theme.TutorTheme

/**
 * Actividad unica de la aplicacion: aplica el tema Material 3
 * (claro/oscuro persistido en DataStore) y monta la navegacion.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = TutorApp.desde(this)

        setContent {
            val ajustes = app.perfilDataStore.ajustes
                .collectAsStateWithLifecycle(initialValue = Ajustes())
                .value

            TutorTheme(darkTheme = ajustes.modoOscuro) {
                AppNavigation()
            }
        }
    }
}
