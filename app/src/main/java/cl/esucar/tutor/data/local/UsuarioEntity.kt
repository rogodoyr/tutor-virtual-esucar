package cl.esucar.tutor.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import cl.esucar.tutor.domain.model.Rol
import cl.esucar.tutor.domain.model.Usuario

/**
 * Usuario registrado en la aplicacion (estudiante o profesor guia).
 * La contrasena nunca se guarda en claro: se almacena su hash SHA-256
 * junto con un salt unico por usuario.
 */
@Entity(tableName = "usuarios", indices = [Index(value = ["email"], unique = true)])
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val email: String,
    val passwordHash: String,
    val salt: String,
    val rol: String
) {
    fun aDominio(): Usuario = Usuario(
        id = id,
        nombre = nombre,
        email = email,
        rol = Rol.desdeTexto(rol)
    )
}
