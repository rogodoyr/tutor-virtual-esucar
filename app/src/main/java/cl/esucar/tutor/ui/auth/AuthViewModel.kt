package cl.esucar.tutor.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.esucar.tutor.TutorApp
import cl.esucar.tutor.data.datastore.PerfilDataStore
import cl.esucar.tutor.data.repository.ResultadoAuth
import cl.esucar.tutor.data.repository.UsuarioRepository
import cl.esucar.tutor.domain.model.Rol
import cl.esucar.tutor.domain.validation.ValidadorLogin
import cl.esucar.tutor.domain.validation.ValidadorRegistro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado del formulario de acceso. Las validaciones se calculan en el
 * dominio (ValidadorLogin); la interfaz solo las muestra.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val errores: ValidadorLogin.Errores = ValidadorLogin.Errores(),
    val mostrarErrores: Boolean = false,
    val mensajeGeneral: String? = null,
    val accesoExitoso: Boolean = false
)

/**
 * Estado del formulario de registro de usuarios.
 */
data class RegistroUiState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val confirmacion: String = "",
    val rol: Rol = Rol.ESTUDIANTE,
    val errores: ValidadorRegistro.Errores = ValidadorRegistro.Errores(),
    val mostrarErrores: Boolean = false,
    val mensajeGeneral: String? = null,
    val registroExitoso: Boolean = false
)

class AuthViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val perfilDataStore: PerfilDataStore
) : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login.asStateFlow()

    private val _registro = MutableStateFlow(RegistroUiState())
    val registro: StateFlow<RegistroUiState> = _registro.asStateFlow()

    // ---------- Login ----------

    fun onLoginEmailChange(valor: String) = _login.update {
        val nuevo = it.copy(email = valor)
        if (nuevo.mostrarErrores) nuevo.copy(errores = validarLogin(nuevo)) else nuevo
    }

    fun onLoginPasswordChange(valor: String) = _login.update {
        val nuevo = it.copy(password = valor)
        if (nuevo.mostrarErrores) nuevo.copy(errores = validarLogin(nuevo)) else nuevo
    }

    private fun validarLogin(estado: LoginUiState): ValidadorLogin.Errores =
        ValidadorLogin.validar(estado.email, estado.password)

    fun ingresar() {
        val estado = _login.value
        val errores = validarLogin(estado)
        if (errores.hayErrores) {
            _login.value = estado.copy(errores = errores, mostrarErrores = true)
            return
        }

        viewModelScope.launch {
            when (val resultado = usuarioRepository.ingresar(estado.email, estado.password)) {
                is ResultadoAuth.Exito -> {
                    perfilDataStore.iniciarSesion(
                        email = resultado.usuario.email,
                        nombre = resultado.usuario.nombre,
                        rol = resultado.usuario.rol.name
                    )
                    _login.update { it.copy(accesoExitoso = true, mensajeGeneral = null) }
                }
                is ResultadoAuth.Error ->
                    _login.update { it.copy(mensajeGeneral = resultado.mensaje) }
            }
        }
    }

    /** Acceso rapido con la cuenta de demostracion. */
    fun ingresarDemo() {
        viewModelScope.launch {
            when (val resultado = usuarioRepository.usuarioDemo()) {
                is ResultadoAuth.Exito -> {
                    perfilDataStore.iniciarSesion(
                        email = resultado.usuario.email,
                        nombre = resultado.usuario.nombre,
                        rol = resultado.usuario.rol.name
                    )
                    _login.update { it.copy(accesoExitoso = true, mensajeGeneral = null) }
                }
                is ResultadoAuth.Error ->
                    _login.update { it.copy(mensajeGeneral = resultado.mensaje) }
            }
        }
    }

    // ---------- Registro ----------

    fun onRegistroNombreChange(v: String) = actualizarRegistro { it.copy(nombre = v) }
    fun onRegistroEmailChange(v: String) = actualizarRegistro { it.copy(email = v) }
    fun onRegistroPasswordChange(v: String) = actualizarRegistro { it.copy(password = v) }
    fun onRegistroConfirmacionChange(v: String) = actualizarRegistro { it.copy(confirmacion = v) }
    fun onRegistroRolChange(v: Rol) = actualizarRegistro { it.copy(rol = v) }

    private fun actualizarRegistro(cambio: (RegistroUiState) -> RegistroUiState) {
        _registro.update { estado ->
            val nuevo = cambio(estado)
            if (nuevo.mostrarErrores) nuevo.copy(errores = validarRegistro(nuevo)) else nuevo
        }
    }

    private fun validarRegistro(estado: RegistroUiState): ValidadorRegistro.Errores =
        ValidadorRegistro.validar(
            nombre = estado.nombre,
            email = estado.email,
            password = estado.password,
            confirmacion = estado.confirmacion,
            rol = estado.rol.etiqueta
        )

    fun registrar() {
        val estado = _registro.value
        val errores = validarRegistro(estado)
        if (errores.hayErrores) {
            _registro.value = estado.copy(errores = errores, mostrarErrores = true)
            return
        }

        viewModelScope.launch {
            when (val resultado = usuarioRepository.registrar(
                nombre = estado.nombre,
                email = estado.email,
                password = estado.password,
                rol = estado.rol
            )) {
                is ResultadoAuth.Exito -> {
                    perfilDataStore.iniciarSesion(
                        email = resultado.usuario.email,
                        nombre = resultado.usuario.nombre,
                        rol = resultado.usuario.rol.name
                    )
                    _registro.update { it.copy(registroExitoso = true, mensajeGeneral = null) }
                }
                is ResultadoAuth.Error ->
                    _registro.update { it.copy(mensajeGeneral = resultado.mensaje) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TutorApp
                AuthViewModel(app.usuarioRepository, app.perfilDataStore)
            }
        }
    }
}
