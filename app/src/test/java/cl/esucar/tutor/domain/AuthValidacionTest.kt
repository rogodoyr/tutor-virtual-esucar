package cl.esucar.tutor.domain

import cl.esucar.tutor.domain.validation.ValidadorLogin
import cl.esucar.tutor.domain.validation.ValidadorRegistro
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas de las validaciones de login y registro (logica desacoplada).
 */
class AuthValidacionTest {

    // ---------- Login ----------

    @Test
    fun `login valido no tiene errores`() {
        val errores = ValidadorLogin.validar("estudiante@esucar.cl", "clave#123")
        assertFalse(errores.hayErrores)
    }

    @Test
    fun `login con correo invalido produce error`() {
        val errores = ValidadorLogin.validar("no-es-correo", "clave#123")
        assertNotNull(errores.email)
    }

    @Test
    fun `login con correo no institucional produce error`() {
        val errores = ValidadorLogin.validar("ana@duocuc.cl", "clave#123")
        assertNotNull(errores.email)
    }

    @Test
    fun `login sin contrasena produce error`() {
        val errores = ValidadorLogin.validar("estudiante@esucar.cl", "")
        assertNotNull(errores.password)
    }

    // ---------- Registro ----------

    @Test
    fun `registro valido no tiene errores`() {
        val errores = ValidadorRegistro.validar(
            nombre = "Ana Perez",
            email = "ana@esucar.cl",
            password = "clave#123",
            confirmacion = "clave#123",
            rol = "Estudiante de trabajo de titulo"
        )
        assertFalse(errores.hayErrores)
    }

    @Test
    fun `registro con contrasenas distintas produce error`() {
        val errores = ValidadorRegistro.validar(
            nombre = "Ana Perez",
            email = "ana@esucar.cl",
            password = "clave#123",
            confirmacion = "otra#Clave",
            rol = "Estudiante de trabajo de titulo"
        )
        assertNotNull(errores.confirmacion)
    }

    @Test
    fun `registro con contrasena sin caracter especial produce error`() {
        val errores = ValidadorRegistro.validar(
            nombre = "Ana Perez",
            email = "ana@esucar.cl",
            password = "clave123",
            confirmacion = "clave123",
            rol = "Estudiante de trabajo de titulo"
        )
        assertNotNull(errores.password)
    }

    @Test
    fun `registro con contrasena corta produce error`() {
        val errores = ValidadorRegistro.validar(
            nombre = "Ana Perez",
            email = "ana@esucar.cl",
            password = "abc",
            confirmacion = "abc",
            rol = "Estudiante de trabajo de titulo"
        )
        assertNotNull(errores.password)
    }

    @Test
    fun `registro sin rol produce error`() {
        val errores = ValidadorRegistro.validar(
            nombre = "Ana Perez",
            email = "ana@esucar.cl",
            password = "clave#123",
            confirmacion = "clave#123",
            rol = ""
        )
        assertNotNull(errores.rol)
    }

    @Test
    fun `registro con nombre numerico produce error`() {
        val errores = ValidadorRegistro.validar(
            nombre = "Ana123",
            email = "ana@esucar.cl",
            password = "clave#123",
            confirmacion = "clave#123",
            rol = "Profesor guia"
        )
        assertNotNull(errores.nombre)
    }

    @Test
    fun `registro con nombre sin apellido produce error`() {
        val errores = ValidadorRegistro.validar(
            nombre = "Ana",
            email = "ana@esucar.cl",
            password = "clave#123",
            confirmacion = "clave#123",
            rol = "Profesor guia"
        )
        assertNotNull(errores.nombre)
    }

    @Test
    fun `registro con correo no institucional produce error`() {
        val errores = ValidadorRegistro.validar(
            nombre = "Ana Perez",
            email = "ana@gmail.com",
            password = "clave#123",
            confirmacion = "clave#123",
            rol = "Estudiante de trabajo de titulo"
        )
        assertTrue(errores.hayErrores)
        assertNotNull(errores.email)
    }
}
