package cl.esucar.tutor.data.repository

import cl.esucar.tutor.data.local.UsuarioDao
import cl.esucar.tutor.data.local.UsuarioEntity
import cl.esucar.tutor.domain.model.Rol
import cl.esucar.tutor.domain.model.Usuario
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID

/**
 * Resultado de una operacion de autenticacion. La logica vive aqui y no
 * en la interfaz: la UI solo muestra el resultado.
 */
sealed class ResultadoAuth {
    data class Exito(val usuario: Usuario) : ResultadoAuth()
    data class Error(val mensaje: String) : ResultadoAuth()
}

/**
 * Repositorio de usuarios: registro y autenticacion local con contrasenas
 * cifradas (SHA-256 + salt unico por usuario).
 */
class UsuarioRepository(private val dao: UsuarioDao) {

    suspend fun registrar(
        nombre: String,
        email: String,
        password: String,
        rol: Rol
    ): ResultadoAuth {
        val emailNormalizado = email.trim().lowercase()
        if (dao.porEmail(emailNormalizado) != null) {
            return ResultadoAuth.Error("Ya existe una cuenta registrada con ese correo")
        }

        val salt = generarSalt()
        val usuario = UsuarioEntity(
            nombre = nombre.trim(),
            email = emailNormalizado,
            passwordHash = hash(password, salt),
            salt = salt,
            rol = rol.name
        )
        dao.insertar(usuario)
        return ResultadoAuth.Exito(usuario.aDominio())
    }

    suspend fun ingresar(email: String, password: String): ResultadoAuth {
        val emailNormalizado = email.trim().lowercase()
        val usuario = dao.porEmail(emailNormalizado)
            ?: return ResultadoAuth.Error("No existe una cuenta con ese correo")

        val hashIntento = hash(password, usuario.salt)
        return if (hashIntento == usuario.passwordHash) {
            ResultadoAuth.Exito(usuario.aDominio())
        } else {
            ResultadoAuth.Error("Contrasena incorrecta")
        }
    }

    suspend fun usuarioDemo(): ResultadoAuth {
        dao.porEmail(DEMO_EMAIL)?.let { return ResultadoAuth.Exito(it.aDominio()) }
        return registrar("Estudiante Demo", DEMO_EMAIL, DEMO_PASSWORD, Rol.ESTUDIANTE)
    }

    private fun generarSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun hash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest((salt + password).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        const val DEMO_EMAIL = "demo@esucar.cl"
        const val DEMO_PASSWORD = "demo1234"
    }
}
