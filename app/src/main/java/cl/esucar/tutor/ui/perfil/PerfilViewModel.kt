package cl.esucar.tutor.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.esucar.tutor.TutorApp
import cl.esucar.tutor.data.datastore.PerfilDataStore
import cl.esucar.tutor.domain.model.Perfil
import cl.esucar.tutor.domain.validation.ValidadorPerfil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado del formulario de perfil y de los ajustes de la aplicacion.
 * Las validaciones viven en el dominio (ValidadorPerfil).
 */
data class PerfilUiState(
    val nombre: String = "",
    val email: String = "",
    val carrera: String = "",
    val semestre: String = "",
    val fotoRuta: String = "",
    val errores: ValidadorPerfil.Errores = ValidadorPerfil.Errores(),
    val mostrarErrores: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val modoOscuro: Boolean = false,
    val recordatoriosActivos: Boolean = true
)

class PerfilViewModel(
    private val perfilDataStore: PerfilDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val perfil = perfilDataStore.perfil.first()
            val ajustes = perfilDataStore.ajustes.first()
            _uiState.update {
                it.copy(
                    nombre = perfil.nombre,
                    email = perfil.email,
                    carrera = perfil.carrera,
                    semestre = perfil.semestre,
                    fotoRuta = perfil.fotoRuta,
                    modoOscuro = ajustes.modoOscuro,
                    recordatoriosActivos = ajustes.recordatoriosActivos
                )
            }
        }
    }

    fun onNombreChange(valor: String) = actualizar { it.copy(nombre = valor) }
    fun onEmailChange(valor: String) = actualizar { it.copy(email = valor) }
    fun onCarreraChange(valor: String) = actualizar { it.copy(carrera = valor) }
    fun onSemestreChange(valor: String) = actualizar { it.copy(semestre = valor) }

    private fun actualizar(cambio: (PerfilUiState) -> PerfilUiState) {
        _uiState.update { estado ->
            val nuevo = cambio(estado)
            if (nuevo.mostrarErrores) {
                nuevo.copy(errores = validar(nuevo))
            } else {
                nuevo
            }
        }
    }

    private fun validar(estado: PerfilUiState): ValidadorPerfil.Errores =
        ValidadorPerfil.validar(estado.nombre, estado.email, estado.carrera, estado.semestre)

    fun guardar() {
        val estado = _uiState.value
        val errores = validar(estado)
        if (errores.hayErrores) {
            _uiState.value = estado.copy(errores = errores, mostrarErrores = true)
            return
        }

        viewModelScope.launch {
            perfilDataStore.guardarPerfil(
                Perfil(
                    nombre = estado.nombre.trim(),
                    email = estado.email.trim(),
                    carrera = estado.carrera.trim(),
                    semestre = estado.semestre.trim(),
                    fotoRuta = estado.fotoRuta
                )
            )
            _uiState.update { it.copy(guardadoExitoso = true, mostrarErrores = false) }
        }
    }

    /** Recurso nativo: guarda la ruta de la foto tomada con la camara. */
    fun guardarFoto(ruta: String) {
        _uiState.update { it.copy(fotoRuta = ruta) }
        viewModelScope.launch { perfilDataStore.guardarFoto(ruta) }
    }

    fun cambiarModoOscuro(activo: Boolean) {
        _uiState.update { it.copy(modoOscuro = activo) }
        viewModelScope.launch { perfilDataStore.guardarModoOscuro(activo) }
    }

    fun cambiarRecordatorios(activo: Boolean) {
        _uiState.update { it.copy(recordatoriosActivos = activo) }
        viewModelScope.launch { perfilDataStore.guardarRecordatorios(activo) }
    }

    /** Cierra la sesion activa (la app vuelve a la pantalla de acceso). */
    fun cerrarSesion() {
        viewModelScope.launch { perfilDataStore.cerrarSesion() }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TutorApp
                PerfilViewModel(app.perfilDataStore)
            }
        }
    }
}
