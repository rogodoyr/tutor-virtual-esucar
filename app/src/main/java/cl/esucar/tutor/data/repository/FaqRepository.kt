package cl.esucar.tutor.data.repository

import cl.esucar.tutor.domain.model.PreguntaFaq

/**
 * Banco de preguntas frecuentes para orientar al estudiante en las
 * distintas etapas del proceso metodologico y del formato academico.
 */
class FaqRepository {

    companion object {
        const val CATEGORIA_TODAS = "Todas"
        const val CATEGORIA_METODOLOGIA = "Metodologia"
        const val CATEGORIA_CITACION = "Citacion"
        const val CATEGORIA_FORMATO = "Formato"
    }

    fun preguntas(): List<PreguntaFaq> = listOf(
        PreguntaFaq(
            id = 1,
            pregunta = "¿Cual es la diferencia entre objetivo general y objetivos especificos?",
            categoria = CATEGORIA_METODOLOGIA,
            respuesta = "El objetivo general expresa el proposito global de la investigacion en una sola frase " +
                "verificable. Los objetivos especificos desagregan ese proposito en acciones concretas y medibles " +
                "que, en conjunto, permiten alcanzar el general. Cada objetivo especifico debe poder responderse " +
                "con un resultado al final del estudio."
        ),
        PreguntaFaq(
            id = 2,
            pregunta = "¿Como formulo correctamente el problema de investigacion?",
            categoria = CATEGORIA_METODOLOGIA,
            respuesta = "Delimita primero el contexto (que, quien, donde y cuando), identifica la brecha entre la " +
                "situacion actual y la deseada, y formula la pregunta central en terminos interrogativos. " +
                "Debe ser investigable, especifica y no plantear una solucion de antemano."
        ),
        PreguntaFaq(
            id = 3,
            pregunta = "¿Que enfoque de investigacion debo elegir para mi trabajo de titulo?",
            categoria = CATEGORIA_METODOLOGIA,
            respuesta = "Depende de tus objetivos: el enfoque cuantitativo busca medir y generalizar (encuestas, " +
                "analisis estadistico), el cualitativo busca comprender en profundidad (entrevistas, grupos focales) " +
                "y el mixto integra ambos. Justifica siempre la eleccion respecto de los objetivos y del tipo de estudio."
        ),
        PreguntaFaq(
            id = 4,
            pregunta = "¿Como valido un instrumento de recoleccion de datos?",
            categoria = CATEGORIA_METODOLOGIA,
            respuesta = "Aplica una prueba piloto a una muestra pequena similar a la poblacion de estudio, " +
                "somete el instrumento a juicio de expertos y calcula su confiabilidad (por ejemplo Alfa de Cronbach " +
                "para escalas cuantitativas). Documenta ambos procesos en el capitulo de metodologia."
        ),
        PreguntaFaq(
            id = 5,
            pregunta = "¿Como cito una fuente con dos autores en norma APA 7?",
            categoria = CATEGORIA_CITACION,
            respuesta = "En el texto se citan ambos apellidos unidos por 'y' (en espanol) o '&' (entre parentesis): " +
                "(Perez y Soto, 2023). En la lista de referencias: Perez, A. y Soto, B. (2023). Titulo de la obra. Editorial."
        ),
        PreguntaFaq(
            id = 6,
            pregunta = "¿Que diferencia hay entre cita parentetica y narrativa?",
            categoria = CATEGORIA_CITACION,
            respuesta = "En la cita narrativa el autor forma parte de la oracion: 'Perez (2023) senala que...'. " +
                "En la cita parentetica la referencia va al final entre parentesis: '(..., Perez, 2023)'. " +
                "Ambas requieren anio y, cuando se parafrasea una idea textual, pagina o parrafo."
        ),
        PreguntaFaq(
            id = 7,
            pregunta = "¿Como referencio correctamente una pagina web o documento institucional?",
            categoria = CATEGORIA_CITACION,
            respuesta = "Incluye autor u organizacion, anio, titulo del documento, nombre del sitio y la URL. " +
                "Ejemplo: Direccion General de Educacion. (2024). Reglamento de titulacion. https://ejemplo.cl/reglamento " +
                "Si no hay fecha, se coloca (s.f.). Verifica siempre que la URL funcione al momento de la entrega."
        ),
        PreguntaFaq(
            id = 8,
            pregunta = "¿Cual es el formato de entrega de los estados de avance?",
            categoria = CATEGORIA_FORMATO,
            respuesta = "Los avances se entregan en documento PDF con: portada institucional, indice, contenido " +
                "desarrollado hasta la etapa correspondiente y lista de referencias. Debe respetarse el margen, " +
                "la fuente y la numeracion de paginas definidos por la Escuela. Revisa siempre la pauta vigente del semestre."
        ),
        PreguntaFaq(
            id = 9,
            pregunta = "¿Cuantas paginas debe tener el trabajo de titulo final?",
            categoria = CATEGORIA_FORMATO,
            respuesta = "Depende del reglamento vigente de la Escuela y del tipo de estudio; como referencia, " +
                "los trabajos de titulacion suelen tener entre 40 y 80 paginas sin anexos. Confirma el rango " +
                "en la pauta de evaluacion de tu semestre antes de planificar la redaccion."
        ),
        PreguntaFaq(
            id = 10,
            pregunta = "¿Como organizo las referencias si uso fuentes digitales?",
            categoria = CATEGORIA_FORMATO,
            respuesta = "Todas las fuentes citadas en el texto deben aparecer en la lista de referencias y viceversa, " +
                "ordenadas alfabeticamente por apellido del primer autor, con sangria francesa y formato APA 7. " +
                "Un gestor bibliografico (Mendeley, Zotero) reduce errores y actualiza el formato automaticamente."
        ),
        PreguntaFaq(
            id = 11,
            pregunta = "¿Que hago si mi profesor guia me pide cambios fuera de la pauta?",
            categoria = CATEGORIA_FORMATO,
            respuesta = "Registra los cambios solicitados en tu bitacora de avance y confirma por escrito (correo) " +
                "el alcance de los ajustes. La pauta publicada sigue siendo la base de evaluacion; los ajustes " +
                "del profesor guia se documentan como observaciones del proceso."
        ),
        PreguntaFaq(
            id = 12,
            pregunta = "¿Como defino la poblacion y la muestra de mi estudio?",
            categoria = CATEGORIA_METODOLOGIA,
            respuesta = "La poblacion es el conjunto completo de sujetos o casos que responden a tus criterios de " +
                "seleccion; la muestra es el subconjunto efectivamente estudiado. Describe criterios de inclusion y " +
                "exclusion, tamano y tecnica de muestreo (probabilistico o no probabilistico), justificando la decision."
        )
    )

    fun categorias(): List<String> = listOf(
        CATEGORIA_TODAS, CATEGORIA_METODOLOGIA, CATEGORIA_CITACION, CATEGORIA_FORMATO
    )

    fun buscar(texto: String, categoria: String): List<PreguntaFaq> {
        val consulta = texto.trim().lowercase()
        return preguntas().filter { p ->
            val coincideCategoria = categoria == CATEGORIA_TODAS || p.categoria == categoria
            val coincideTexto = consulta.isEmpty() ||
                p.pregunta.lowercase().contains(consulta) ||
                p.respuesta.lowercase().contains(consulta) ||
                p.categoria.lowercase().contains(consulta)
            coincideCategoria && coincideTexto
        }
    }
}
