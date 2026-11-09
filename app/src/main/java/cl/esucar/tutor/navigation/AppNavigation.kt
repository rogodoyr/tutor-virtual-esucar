package cl.esucar.tutor.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.esucar.tutor.TutorApp
import cl.esucar.tutor.ui.auth.LoginScreen
import cl.esucar.tutor.ui.auth.RegistroScreen
import cl.esucar.tutor.ui.entregables.EntregableFormScreen
import cl.esucar.tutor.ui.entregables.EntregablesScreen
import cl.esucar.tutor.ui.faq.FaqScreen
import cl.esucar.tutor.ui.guia.GuiaDetalleScreen
import cl.esucar.tutor.ui.guia.GuiaScreen
import cl.esucar.tutor.ui.home.HomeScreen
import cl.esucar.tutor.ui.perfil.PerfilScreen
import kotlinx.coroutines.flow.first

/** Rutas de navegacion de la aplicacion. */
object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val INICIO = "inicio"
    const val GUIA = "guia"
    const val GUIA_DETALLE = "guia/{etapaId}"
    const val FAQ = "faq"
    const val ENTREGABLES = "entregables"
    const val NUEVO_ENTREGABLE = "entregables/nuevo"
    const val PERFIL = "perfil"

    fun guiaDetalle(etapaId: Int) = "guia/$etapaId"
}

private data class Destino(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector
)

private val destinos = listOf(
    Destino(Rutas.INICIO, "Inicio", Icons.Default.Home),
    Destino(Rutas.GUIA, "Guia", Icons.AutoMirrored.Filled.List),
    Destino(Rutas.FAQ, "FAQ", Icons.Default.Search),
    Destino(Rutas.ENTREGABLES, "Entregables", Icons.Default.Info),
    Destino(Rutas.PERFIL, "Perfil", Icons.Default.Person)
)

/**
 * Navegacion principal. La aplicacion es institucional: el acceso requiere
 * autenticacion (estudiante o profesor guia). Si ya existe una sesion
 * persistida, entra directo a Inicio.
 */
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val app = TutorApp.desde(context)
    val navController = rememberNavController()
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    // Destino inicial segun la sesion persistida (login o inicio)
    var destinoInicial by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        val sesion = app.perfilDataStore.sesion.first()
        destinoInicial = if (sesion.first.isNotBlank()) Rutas.INICIO else Rutas.LOGIN
    }

    val pantallasPrincipales = destinos.map { it.ruta }
    val mostrarBarra = rutaActual in pantallasPrincipales

    if (destinoInicial == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = mostrarBarra,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar {
                    destinos.forEach { destino ->
                        NavigationBarItem(
                            selected = rutaActual == destino.ruta,
                            onClick = {
                                navController.navigate(destino.ruta) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destino.icono, contentDescription = destino.etiqueta) },
                            label = { Text(destino.etiqueta) }
                        )
                    }
                }
            }
        }
    ) { paddingInterno ->
        NavHost(
            navController = navController,
            startDestination = destinoInicial!!,
            modifier = Modifier.padding(paddingInterno),
            enterTransition = {
                fadeIn(animationSpec = tween(300)) +
                    slideInHorizontally(initialOffsetX = { it / 4 })
            },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300)) +
                    slideInHorizontally(initialOffsetX = { -it / 4 })
            },
            popExitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            composable(Rutas.LOGIN) {
                LoginScreen(
                    onLoginOk = {
                        navController.navigate(Rutas.INICIO) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    },
                    onIrARegistro = { navController.navigate(Rutas.REGISTRO) }
                )
            }
            composable(Rutas.REGISTRO) {
                RegistroScreen(
                    onRegistroOk = {
                        navController.navigate(Rutas.INICIO) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    },
                    onVolver = { navController.popBackStack() }
                )
            }
            composable(Rutas.INICIO) {
                HomeScreen(
                    onIrAGuia = { navController.navigate(Rutas.GUIA) },
                    onIrAFaq = { navController.navigate(Rutas.FAQ) },
                    onNuevoEntregable = { navController.navigate(Rutas.NUEVO_ENTREGABLE) },
                    onVerEntregables = { navController.navigate(Rutas.ENTREGABLES) }
                )
            }
            composable(Rutas.GUIA) {
                GuiaScreen(
                    onEtapaClick = { id -> navController.navigate(Rutas.guiaDetalle(id)) }
                )
            }
            composable(
                route = Rutas.GUIA_DETALLE,
                arguments = listOf(navArgument("etapaId") { type = NavType.IntType })
            ) { backStackEntry ->
                val etapaId = backStackEntry.arguments?.getInt("etapaId") ?: 1
                GuiaDetalleScreen(
                    etapaId = etapaId,
                    onVolver = { navController.popBackStack() }
                )
            }
            composable(Rutas.FAQ) {
                FaqScreen()
            }
            composable(Rutas.ENTREGABLES) {
                EntregablesScreen(
                    onNuevoEntregable = { navController.navigate(Rutas.NUEVO_ENTREGABLE) }
                )
            }
            composable(Rutas.NUEVO_ENTREGABLE) {
                EntregableFormScreen(
                    onVolver = { navController.popBackStack() }
                )
            }
            composable(Rutas.PERFIL) {
                PerfilScreen(
                    onCerrarSesion = {
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
