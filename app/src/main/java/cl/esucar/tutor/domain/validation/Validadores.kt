package cl.esucar.tutor.domain.validation

/**
 * Logica de validacion centralizada y desacoplada de la interfaz.
 * Los componentes visuales solo muestran los mensajes que producen
 * estas funciones; ninguna regla vive dentro de los composables.
 */

object Validadores {

    private val REGEX_EMAIL = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val REGEX_SOLO_LETRAS = Regex("^[A-Za-z\\u00C0-\\u024F\\s]+$")

    /** Devuelve el mensaje de error, o null cuando el valor es valido. */
    fun requerido(valor: String, campo: String): String? =
        if (valor.isBlank()) "El campo $campo es obligatorio" else null

    fun longitudMinima(valor: String, minimo: Int, campo: String): String? =
        if (valor.trim().length < minimo) "$campo debe tener al menos $minimo caracteres" else null

    fun email(valor: String): String? = when {
        valor.isBlank() -> "El correo es obligatorio"
        !REGEX_EMAIL.matches(valor.trim()) -> "Ingrese un correo valido (ej: nombre@esucar.cl)"
        else -> null
    }

    /**
     * La app es de uso institucional: solo se aceptan correos @esucar.cl.
     */
    fun emailInstitucional(valor: String): String? =
        email(valor)
            ?: if (!valor.trim().endsWith("@esucar.cl", ignoreCase = true)) {
                "Solo se aceptan correos institucionales @esucar.cl (ej: nombre@esucar.cl)"
            } else {
                null
            }

    /**
     * Politica de contrasenas: minimo 6 caracteres e incluye al menos
     * una letra, un numero y un caracter especial.
     */
    fun contrasenaSegura(valor: String): String? = when {
        valor.isBlank() -> "La contrasena es obligatoria"
        valor.length < 6 -> "La contrasena debe tener al menos 6 caracteres"
        !valor.any { it.isLetter() } -> "Debe incluir al menos una letra"
        !valor.any { it.isDigit() } -> "Debe incluir al menos un numero"
        !valor.any { !it.isLetterOrDigit() } -> "Debe incluir al menos un caracter especial (ej: !#%&*)"
        else -> null
    }

    /** Nombre y apellido: solo letras y dos palabras como minimo. */
    fun nombreCompleto(valor: String): String? =
        requerido(valor, "Nombre")
            ?: soloLetras(valor, "El nombre")
            ?: if (valor.trim().split(Regex("\\s+")).size < 2) {
                "Ingrese nombre y apellido"
            } else {
                null
            }

    fun soloLetras(valor: String, campo: String): String? =
        if (!REGEX_SOLO_LETRAS.matches(valor.trim())) "$campo solo debe contener letras" else null

    fun fechaFutura(fechaMillis: Long?, hoyMillis: Long): String? = when {
        fechaMillis == null -> "Debe seleccionar una fecha limite"
        fechaMillis < hoyMillis -> "La fecha limite no puede ser anterior a hoy"
        else -> null
    }

    fun semestre(valor: String): String? {
        val numero = valor.trim().toIntOrNull()
        return when {
            valor.isBlank() -> "El semestre es obligatorio"
            numero == null || numero !in 1..12 -> "El semestre debe estar entre 1 y 12"
            else -> null
        }
    }
}

/**
 * Validacion integral del formulario de entregable. Se ejecuta desde el
 * ViewModel, nunca desde el composable.
 */
object ValidadorEntregable {

    data class Errores(
        val titulo: String? = null,
        val etapa: String? = null,
        val descripcion: String? = null,
        val fechaLimite: String? = null
    ) {
        val hayErrores: Boolean
            get() = titulo != null || etapa != null || descripcion != null || fechaLimite != null
    }

    fun validar(
        titulo: String,
        etapa: String,
        descripcion: String,
        fechaLimite: Long?,
        hoyMillis: Long = System.currentTimeMillis()
    ): Errores {
        val errorTitulo = Validadores.requerido(titulo, "Titulo")
            ?: Validadores.longitudMinima(titulo, 4, "El titulo")

        val errorEtapa = Validadores.requerido(etapa, "Etapa")

        val errorDescripcion = Validadores.requerido(descripcion, "Descripcion")
            ?: Validadores.longitudMinima(descripcion, 10, "La descripcion")

        val errorFecha = Validadores.fechaFutura(fechaLimite, hoyMillis)

        return Errores(errorTitulo, errorEtapa, errorDescripcion, errorFecha)
    }
}

/**
 * Validacion del formulario de acceso (login).
 */
object ValidadorLogin {

    data class Errores(
        val email: String? = null,
        val password: String? = null
    ) {
        val hayErrores: Boolean get() = email != null || password != null
    }

    fun validar(email: String, password: String): Errores = Errores(
        email = Validadores.emailInstitucional(email),
        password = Validadores.requerido(password, "Contrasena")
    )
}

/**
 * Validacion del formulario de registro de usuarios.
 */
object ValidadorRegistro {

    data class Errores(
        val nombre: String? = null,
        val email: String? = null,
        val password: String? = null,
        val confirmacion: String? = null,
        val rol: String? = null
    ) {
        val hayErrores: Boolean
            get() = nombre != null || email != null || password != null ||
                confirmacion != null || rol != null
    }

    fun validar(
        nombre: String,
        email: String,
        password: String,
        confirmacion: String,
        rol: String
    ): Errores {
        val errorPassword = Validadores.contrasenaSegura(password)

        val errorConfirmacion = when {
            confirmacion.isBlank() -> "Debe confirmar la contrasena"
            confirmacion != password -> "Las contrasenas no coinciden"
            else -> null
        }

        return Errores(
            nombre = Validadores.nombreCompleto(nombre),
            email = Validadores.emailInstitucional(email),
            password = errorPassword,
            confirmacion = errorConfirmacion,
            rol = Validadores.requerido(rol, "Perfil")
        )
    }
}

/**
 * Validacion integral del formulario de perfil del estudiante.
 */
object ValidadorPerfil {

    data class Errores(
        val nombre: String? = null,
        val email: String? = null,
        val carrera: String? = null,
        val semestre: String? = null
    ) {
        val hayErrores: Boolean
            get() = nombre != null || email != null || carrera != null || semestre != null
    }

    fun validar(
        nombre: String,
        email: String,
        carrera: String,
        semestre: String
    ): Errores {
        return Errores(
            nombre = Validadores.nombreCompleto(nombre),
            email = Validadores.emailInstitucional(email),
            carrera = Validadores.requerido(carrera, "Carrera"),
            semestre = Validadores.semestre(semestre)
        )
    }
}
