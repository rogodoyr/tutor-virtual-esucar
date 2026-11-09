package cl.esucar.tutor.ui.perfil

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.esucar.tutor.ui.components.CampoValidado
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel = viewModel(factory = PerfilViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Recurso nativo 2: camara para la foto de perfil (via FileProvider)
    var archivoFoto = remember { File(context.cacheDir, "fotos") }
    val lanzadorCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) {
            val ruta = archivoFoto.absolutePath
            viewModel.guardarFoto(ruta)
        }
    }

    // Recurso nativo 1: permiso de notificaciones para los recordatorios
    val lanzadorPermisoNotificaciones = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        viewModel.cambiarRecordatorios(concedido)
        if (!concedido) {
            viewModel.cambiarRecordatorios(false)
        }
    }

    LaunchedEffect(estado.guardadoExitoso) {
        if (estado.guardadoExitoso) {
            snackbarHostState.showSnackbar("Perfil guardado correctamente")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto de perfil con boton de camara
            Box(contentAlignment = Alignment.BottomEnd) {
                val bitmap = remember(estado.fotoRuta) {
                    val archivo = File(estado.fotoRuta)
                    if (estado.fotoRuta.isNotBlank() && archivo.exists()) {
                        BitmapFactory.decodeFile(archivo.absolutePath)?.asImageBitmap()
                    } else {
                        null
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    )
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Sin foto",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val carpeta = File(context.cacheDir, "fotos").apply { mkdirs() }
                        val archivo = File(carpeta, "perfil_${System.currentTimeMillis()}.jpg")
                        archivoFoto = archivo
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            archivo
                        )
                        lanzadorCamara.launch(uri)
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        Icons.Default.Face,
                        contentDescription = "Tomar foto con la camara",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Text(
                "Toca el icono para tomar una foto con la camara del dispositivo",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            CampoValidado(
                valor = estado.nombre,
                onValorChange = viewModel::onNombreChange,
                etiqueta = "Nombre y apellido",
                error = estado.errores.nombre,
                iconoInicial = Icons.Default.Person
            )

            CampoValidado(
                valor = estado.email,
                onValorChange = viewModel::onEmailChange,
                etiqueta = "Correo electronico",
                error = estado.errores.email,
                iconoInicial = Icons.Default.Email
            )

            CampoValidado(
                valor = estado.carrera,
                onValorChange = viewModel::onCarreraChange,
                etiqueta = "Carrera / programa",
                error = estado.errores.carrera,
                iconoInicial = Icons.Default.Face
            )

            CampoValidado(
                valor = estado.semestre,
                onValorChange = viewModel::onSemestreChange,
                etiqueta = "Semestre (1 al 12)",
                error = estado.errores.semestre,
                iconoInicial = Icons.Default.Settings
            )

            Button(
                onClick = viewModel::guardar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar perfil")
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Ajustes de la aplicacion",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Modo oscuro", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Se guarda tu preferencia en el dispositivo",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Switch(
                            checked = estado.modoOscuro,
                            onCheckedChange = viewModel::cambiarModoOscuro
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Notificaciones", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Recordatorios de fechas limite de entrega",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Switch(
                            checked = estado.recordatoriosActivos,
                            onCheckedChange = { activar ->
                                if (activar &&
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                    ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.POST_NOTIFICATIONS
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    lanzadorPermisoNotificaciones.launch(
                                        Manifest.permission.POST_NOTIFICATIONS
                                    )
                                } else {
                                    viewModel.cambiarRecordatorios(activar)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
