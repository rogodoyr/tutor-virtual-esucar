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
        val errorNombre = Validadores.requerido(nombre, "Nombre")
            ?: Validadores.soloLetras(nombre, "El nombre")
            ?: Validadores.longitudMinima(nombre, 3, "El nombre")

        return Errores(
            nombre = errorNombre,
            email = Validadores.email(email),
            carrera = Validadores.requerido(carrera, "Carrera"),
            semestre = Validadores.semestre(semestre)
        )
    }
}
