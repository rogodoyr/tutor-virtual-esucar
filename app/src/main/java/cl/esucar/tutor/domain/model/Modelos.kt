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

/**
 * Catalogos predefinidos de la institucion: la app es de uso institucional,
 * por lo que los datos se eligen de listas cerradas y no se escriben libres.
 */
object Catalogos {

    /** Programas academicos disponibles (ESUCAR). */
    val CARRERAS = listOf(
        "Administracion y Gestion en Seguridad Publica",
        "Administracion Publica",
        "Seguridad Publica"
    )

    /** Semestres academicos. */
    val SEMESTRES = (1..12).map { it.toString() }

    /** Titulos predefinidos de los entregables (hitos de Trabajo de Titulo). */
    val ENTREGABLES = listOf(
        "Avance 1 - Planteamiento del problema",
        "Avance 2 - Marco teorico",
        "Avance 3 - Metodologia",
        "Avance 4 - Recogida de informacion",
        "Avance 5 - Analisis de resultados",
        "Avance 6 - Conclusiones y recomendaciones",
        "Entrega de formato y normas APA",
        "Entrega final - Trabajo de Titulo"
    )

    /** Items concretos que debe incluir un entregable (seleccion multiple). */
    val ITEMS_ENTREGABLE = listOf(
        "Portada institucional",
        "Indice",
        "Contenido del capitulo",
        "Referencias APA 7",
        "Anexos",
        "PDF segun pauta de evaluacion"
    )
}

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
