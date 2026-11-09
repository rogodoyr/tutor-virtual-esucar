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

    // ---------- Correo institucional ----------

    @Test
    fun `correo institucional esucar es valido`() {
        assertNull(Validadores.emailInstitucional("sadgzsgr@esucar.cl"))
    }

    @Test
    fun `correo de otro dominio produce error`() {
        assertNotNull(Validadores.emailInstitucional("ana@duocuc.cl"))
    }

    @Test
    fun `correo institucional sin formato produce error`() {
        assertNotNull(Validadores.emailInstitucional("xdfhb"))
    }

    // ---------- Contrasena segura ----------

    @Test
    fun `contrasena segura es valida`() {
        assertNull(Validadores.contrasenaSegura("clave#123"))
    }

    @Test
    fun `contrasena sin caracter especial produce error`() {
        assertNotNull(Validadores.contrasenaSegura("clave123"))
    }

    @Test
    fun `contrasena sin numero produce error`() {
        assertNotNull(Validadores.contrasenaSegura("clave#abc"))
    }

    @Test
    fun `contrasena sin letra produce error`() {
        assertNotNull(Validadores.contrasenaSegura("1234#56"))
    }

    @Test
    fun `contrasena corta produce error`() {
        assertNotNull(Validadores.contrasenaSegura("ab#1"))
    }

    // ---------- Nombre completo ----------

    @Test
    fun `nombre y apellido es valido`() {
        assertNull(Validadores.nombreCompleto("Ana Perez"))
    }

    @Test
    fun `nombre sin apellido produce error`() {
        assertNotNull(Validadores.nombreCompleto("Ana"))
    }

    @Test
    fun `nombre con numeros produce error`() {
        assertNotNull(Validadores.nombreCompleto("Ana123 Perez"))
    }

    // ---------- Otros ----------

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
            titulo = "Avance 1 - Planteamiento del problema",
            etapa = "1. Planteamiento del problema",
            descripcion = "Debe incluir: Portada institucional, Indice",
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
            email = "ana@esucar.cl",
            carrera = "Administracion y Gestion en Seguridad Publica",
            semestre = "6"
        )
        assertFalse(errores.hayErrores)
    }

    @Test
    fun `perfil con correo no institucional produce error`() {
        val errores = ValidadorPerfil.validar(
            nombre = "Ana Perez",
            email = "ana@duocuc.cl",
            carrera = "Administracion y Gestion en Seguridad Publica",
            semestre = "6"
        )
        assertNotNull(errores.email)
    }

    @Test
    fun `nombre con numeros produce error en perfil`() {
        val errores = ValidadorPerfil.validar(
            nombre = "Ana123 Perez",
            email = "ana@esucar.cl",
            carrera = "Administracion y Gestion en Seguridad Publica",
            semestre = "6"
        )
        assertNotNull(errores.nombre)
    }
}
