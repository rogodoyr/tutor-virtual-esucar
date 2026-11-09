package cl.esucar.tutor.domain

import cl.esucar.tutor.domain.validation.ValidadorEntregable
import cl.esucar.tutor.domain.validation.ValidadorPerfil
import cl.esucar.tutor.domain.validation.Validadores
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias de la logica de validacion desacoplada de la interfaz.
 */
class ValidadoresTest {

    // ---------- Validadores base ----------

    @Test
    fun `campo requerido vacio produce error`() {
        assertNotNull(Validadores.requerido("   ", "Titulo"))
    }

    @Test
    fun `campo requerido con contenido es valido`() {
        assertNull(Validadores.requerido("Entrega parcial", "Titulo"))
    }

    @Test
    fun `email invalido produce error`() {
        assertNotNull(Validadores.email("no-es-un-correo"))
    }

    @Test
    fun `email valido no produce error`() {
        assertNull(Validadores.email("estudiante@esucar.cl"))
    }

    @Test
    fun `semestre fuera de rango produce error`() {
        assertNotNull(Validadores.semestre("15"))
    }

    @Test
    fun `semestre valido no produce error`() {
        assertNull(Validadores.semestre("5"))
    }

    @Test
    fun `fecha anterior a hoy produce error`() {
        val hoy = 1_700_000_000_000L
        assertNotNull(Validadores.fechaFutura(hoy - 86_400_000L, hoy))
    }

    @Test
    fun `fecha futura no produce error`() {
        val hoy = 1_700_000_000_000L
        assertNull(Validadores.fechaFutura(hoy + 86_400_000L, hoy))
    }

    // ---------- Formulario de entregable ----------

    @Test
    fun `formulario de entregable completo y valido no tiene errores`() {
        val errores = ValidadorEntregable.validar(
            titulo = "Entrega capitulo 1",
            etapa = "1. Planteamiento del problema",
            descripcion = "Entrega del planteamiento del problema completo",
            fechaLimite = System.currentTimeMillis() + 86_400_000L
        )
        assertFalse(errores.hayErrores)
    }

    @Test
    fun `formulario de entregable vacio tiene errores en todos los campos`() {
        val errores = ValidadorEntregable.validar(
            titulo = "",
            etapa = "",
            descripcion = "",
            fechaLimite = null
        )
        assertTrue(errores.hayErrores)
        assertNotNull(errores.titulo)
        assertNotNull(errores.etapa)
        assertNotNull(errores.descripcion)
        assertNotNull(errores.fechaLimite)
    }

    @Test
    fun `titulo muy corto produce error`() {
        val errores = ValidadorEntregable.validar(
            titulo = "abc",
            etapa = "Metodologia",
            descripcion = "Descripcion suficientemente larga",
            fechaLimite = System.currentTimeMillis() + 86_400_000L
        )
        assertNotNull(errores.titulo)
    }

    // ---------- Formulario de perfil ----------

    @Test
    fun `perfil completo y valido no tiene errores`() {
        val errores = ValidadorPerfil.validar(
            nombre = "Ana Perez",
            email = "ana@duocuc.cl",
            carrera = "Analisis de Sistemas",
            semestre = "6"
        )
        assertFalse(errores.hayErrores)
    }

    @Test
    fun `nombre con numeros produce error`() {
        val errores = ValidadorPerfil.validar(
            nombre = "Ana123",
            email = "ana@duocuc.cl",
            carrera = "Analisis de Sistemas",
            semestre = "6"
        )
        assertNotNull(errores.nombre)
    }
}
