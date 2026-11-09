package cl.esucar.tutor.domain.model

/**
 * Modelos de dominio. Estas clases no dependen de Android ni de la capa
 * visual, por lo que la logica puede probarse y reutilizarse.
 */

enum class EstadoEntregable(val etiqueta: String) {
    POR_INICIAR("Por iniciar"),
    EN_DESARROLLO("En desarrollo"),
    ENVIADO("Enviado"),
    APROBADO("Aprobado");

    companion object {
        fun desdeTexto(valor: String): EstadoEntregable =
            entries.firstOrNull { it.name == valor } ?: POR_INICIAR
    }
}

data class Entregable(
    val id: Long = 0,
    val titulo: String,
    val etapa: String,
    val descripcion: String,
    val fechaLimite: Long,
    val estado: EstadoEntregable = EstadoEntregable.POR_INICIAR,
    val recordatorioActivo: Boolean = false
)

data class Etapa(
    val id: Int,
    val titulo: String,
    val objetivo: String,
    val descripcion: String,
    val formatoRequerido: String,
    val criterios: List<String>,
    val consejos: List<String>
)

data class PreguntaFaq(
    val id: Int,
    val pregunta: String,
    val categoria: String,
    val respuesta: String
)

data class Perfil(
    val nombre: String = "",
    val email: String = "",
    val carrera: String = "",
    val semestre: String = "",
    val fotoRuta: String = ""
)

data class Ajustes(
    val modoOscuro: Boolean = false,
    val recordatoriosActivos: Boolean = true
)

/** Perfiles de usuario definidos en el caso ESUCAR. */
enum class Rol(val etiqueta: String) {
    ESTUDIANTE("Estudiante de trabajo de titulo"),
    PROFESOR("Profesor guia");

    companion object {
        fun desdeTexto(valor: String): Rol =
            entries.firstOrNull { it.name == valor } ?: ESTUDIANTE
    }
}

data class Usuario(
    val id: Long = 0,
    val nombre: String,
    val email: String,
    val rol: Rol = Rol.ESTUDIANTE
)
