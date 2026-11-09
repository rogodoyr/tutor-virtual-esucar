package cl.esucar.tutor.data.repository

import cl.esucar.tutor.domain.model.Etapa

/**
 * Guia metodologica paso a paso del Trabajo de Titulo.
 * Contenido local (sin conexion), alineado al caso ESUCAR.
 */
class GuiaRepository {

    fun etapas(): List<Etapa> = listOf(
        Etapa(
            id = 1,
            titulo = "1. Planteamiento del problema",
            objetivo = "Definir el problema de investigacion, su contexto y justificacion.",
            descripcion = "Etapa inicial donde se delimita la situacion problematica en el ambito de la seguridad publica. " +
                "Debes identificar el gap entre la situacion actual y la deseada, formular el problema en terminos " +
                "interrogativos y establecer los objetivos general y especificos que guiaran todo el trabajo.",
            formatoRequerido = "Documento de 4 a 6 paginas: antecedentes, descripcion del problema, " +
                "formulacion del problema, objetivos, justificacion y delimitacion del estudio.",
            criterios = listOf(
                "El problema esta claramente delimitado en ambito, espacio y tiempo",
                "Los objetivos son coherentes con el problema planteado (general y especificos)",
                "La justificacion explica el aporte practico y teorico del estudio",
                "La redaccion es impersonal y en tiempo verbal adecuado"
            ),
            consejos = listOf(
                "Formula el problema como una pregunta de investigacion central",
                "Evita objetivos que no puedan medirse al final del estudio",
                "Apoya los antecedentes con fuentes verificables y actualizadas"
            )
        ),
        Etapa(
            id = 2,
            titulo = "2. Marco teorico",
            objetivo = "Sustentar la investigacion en teorias, conceptos y estudios previos.",
            descripcion = "Recoleccion y organizacion del conocimiento existente sobre las variables del estudio. " +
                "Incluye definiciones conceptuales y operacionales, teorias que sustentan el estudio, " +
                "investigaciones previas relacionadas y el modelo o sistema de variables.",
            formatoRequerido = "Documento de 8 a 15 paginas con subcapitulos tematicos, " +
                "citas segun norma APA 7 y referencias al final.",
            criterios = listOf(
                "Las teorias citadas se relacionan explicitamente con las variables del estudio",
                "Las citas APA 7 estan correctamente aplicadas (texto y lista de referencias)",
                "Se distinguen antecedentes nacionales e internacionales",
                "El sistema de variables esta explicito y coherente"
            ),
            consejos = listOf(
                "Organiza el marco teorico por temas, no por autor",
                "Cada parrafo de analisis debe integrar varias fuentes",
                "Usa gestores bibliograficos para evitar errores de citacion"
            )
        ),
        Etapa(
            id = 3,
            titulo = "3. Metodologia",
            objetivo = "Disenar el camino metodologico para responder la pregunta de investigacion.",
            descripcion = "Se define el enfoque de investigacion (cualitativo, cuantitativo o mixto), el tipo de estudio, " +
                "la poblacion y muestra, las tecnicas e instrumentos de recoleccion y los criterios de validez y confiabilidad. " +
                "Tambien se describen las consideraciones eticas del estudio.",
            formatoRequerido = "Documento de 4 a 8 paginas: enfoque, tipo de investigacion, poblacion/muestra, " +
                "tecnicas e instrumentos, validez/confiabilidad y aspectos eticos.",
            criterios = listOf(
                "El enfoque es coherente con los objetivos y el tipo de estudio",
                "La poblacion y la muestra estan descritas con criterios de seleccion claros",
                "Los instrumentos estan validados o se describe su proceso de validacion",
                "Se explicitan los aspectos eticos (consentimiento, anonimato, resguardo de datos)"
            ),
            consejos = listOf(
                "Justifica por que el enfoque elegido responde a tus objetivos",
                "Si disenaste el instrumento, describe su prueba piloto",
                "No olvides la carta de presentacion institucional para la recoleccion"
            )
        ),
        Etapa(
            id = 4,
            titulo = "4. Recogida de informacion",
            objetivo = "Ejecutar la aplicacion de los instrumentos y organizar los datos recolectados.",
            descripcion = "Implementacion del plan de recoleccion: aplicacion de encuestas, entrevistas o " +
                "analisis documental, registro sistematico de la informacion y organizacion de los datos " +
                "para su posterior analisis. Etapa critica de control de calidad de la informacion.",
            formatoRequerido = "Registro de campo, planillas de recoleccion y memoria de la aplicacion de instrumentos " +
                "(fechas, responsables y n de casos obtenidos).",
            criterios = listOf(
                "La aplicacion de instrumentos sigue el diseno metodologico declarado",
                "La tasa de respuesta y los casos invalidos estan registrados",
                "Los datos quedan organizados y respaldados para el analisis",
                "Se documenta cualquier desviacion respecto del plan original"
            ),
            consejos = listOf(
                "Lleva un bitacora con fecha y lugar de cada aplicacion",
                "Verifica completitud de los instrumentos antes de cerrar la recoleccion",
                "Respalda la informacion en dos lugares fisicos distintos"
            )
        ),
        Etapa(
            id = 5,
            titulo = "5. Analisis de resultados",
            objetivo = "Procesar, analizar e interpretar la informacion recolectada.",
            descripcion = "Aplicacion de tecnicas de analisis (estadisticas descriptivas/inferenciales o analisis " +
                "cualitativo de contenido), presentacion de resultados en tablas y figuras, e interpretacion " +
                "a la luz del marco teorico y de los objetivos de la investigacion.",
            formatoRequerido = "Documento de 6 a 12 paginas con tablas y figuras numeradas, " +
                "analisis por objetivo especifico e interpretacion critica.",
            criterios = listOf(
                "Cada objetivo especifico tiene su resultado asociado",
                "Las tablas y figuras cumplen norma APA y son autoexplicativas",
                "La interpretacion vincula resultados con el marco teorico",
                "Se distingue con claridad entre resultado, analisis y discusion"
            ),
            consejos = listOf(
                "Presenta primero el resultado y luego interpretalo",
                "No introduzcas informacion nueva en la discusion",
                "Usa software (SPSS, Excel, Atlas.ti) y documenta el proceso"
            )
        ),
        Etapa(
            id = 6,
            titulo = "6. Conclusiones y recomendaciones",
            objetivo = "Responder la pregunta de investigacion y proponer lineamientos de accion.",
            descripcion = "Sintesis de los hallazgos principales, respuesta explicita a la pregunta de investigacion " +
                "y a los objetivos, reflexion sobre limitaciones del estudio y recomendaciones practicas " +
                "para la organizacion vinculada y para futuras investigaciones.",
            formatoRequerido = "Documento de 2 a 4 paginas: conclusiones por objetivo, limitaciones, " +
                "recomendaciones practicas y proyecciones de investigacion futura.",
            criterios = listOf(
                "Las conclusiones responden directamente a la pregunta de investigacion",
                "Se reconoce honestamente las limitaciones del estudio",
                "Las recomendaciones son viables y estan dirigidas a actores identificados",
                "Se proponen lineamientos para investigaciones futuras"
            ),
            consejos = listOf(
                "Una conclusion por objetivo especifico",
                "Evita repetir literalmente los resultados",
                "Redacta recomendaciones accionables y priorizadas"
            )
        )
    )

    fun etapaPorId(id: Int): Etapa? = etapas().firstOrNull { it.id == id }
}
