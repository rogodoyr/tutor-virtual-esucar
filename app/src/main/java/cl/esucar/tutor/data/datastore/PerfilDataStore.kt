package cl.esucar.tutor.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cl.esucar.tutor.domain.model.Ajustes
import cl.esucar.tutor.domain.model.Perfil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "perfil_ajustes")

/**
 * Persistencia local de preferencias y perfil del estudiante (DataStore).
 * Cubre datos que no requieren consultas relacionales: perfil, foto y ajustes.
 */
class PerfilDataStore(private val context: Context) {

    private object Claves {
        val NOMBRE = stringPreferencesKey("nombre")
        val EMAIL = stringPreferencesKey("email")
        val CARRERA = stringPreferencesKey("carrera")
        val SEMESTRE = stringPreferencesKey("semestre")
        val FOTO_RUTA = stringPreferencesKey("foto_ruta")
        val MODO_OSCURO = booleanPreferencesKey("modo_oscuro")
        val RECORDATORIOS = booleanPreferencesKey("recordatorios")
        val SESION_EMAIL = stringPreferencesKey("sesion_email")
        val SESION_NOMBRE = stringPreferencesKey("sesion_nombre")
        val SESION_ROL = stringPreferencesKey("sesion_rol")
    }

    val perfil: Flow<Perfil> = context.dataStore.data.map { prefs ->
        Perfil(
            nombre = prefs[Claves.NOMBRE] ?: "",
            email = prefs[Claves.EMAIL] ?: "",
            carrera = prefs[Claves.CARRERA] ?: "",
            semestre = prefs[Claves.SEMESTRE] ?: "",
            fotoRuta = prefs[Claves.FOTO_RUTA] ?: ""
        )
    }

    val ajustes: Flow<Ajustes> = context.dataStore.data.map { prefs ->
        Ajustes(
            modoOscuro = prefs[Claves.MODO_OSCURO] ?: false,
            recordatoriosActivos = prefs[Claves.RECORDATORIOS] ?: true
        )
    }

    /** Sesion activa: email vacio = sin sesion iniciada. */
    val sesion: Flow<Triple<String, String, String>> = context.dataStore.data.map { prefs ->
        Triple(
            prefs[Claves.SESION_EMAIL] ?: "",
            prefs[Claves.SESION_NOMBRE] ?: "",
            prefs[Claves.SESION_ROL] ?: ""
        )
    }

    suspend fun iniciarSesion(email: String, nombre: String, rol: String) {
        context.dataStore.edit { prefs ->
            prefs[Claves.SESION_EMAIL] = email
            prefs[Claves.SESION_NOMBRE] = nombre
            prefs[Claves.SESION_ROL] = rol
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { prefs ->
            prefs.remove(Claves.SESION_EMAIL)
            prefs.remove(Claves.SESION_NOMBRE)
            prefs.remove(Claves.SESION_ROL)
        }
    }

    suspend fun guardarPerfil(perfil: Perfil) {
        context.dataStore.edit { prefs ->
            prefs[Claves.NOMBRE] = perfil.nombre
            prefs[Claves.EMAIL] = perfil.email
            prefs[Claves.CARRERA] = perfil.carrera
            prefs[Claves.SEMESTRE] = perfil.semestre
            prefs[Claves.FOTO_RUTA] = perfil.fotoRuta
        }
    }

    suspend fun guardarFoto(ruta: String) {
        context.dataStore.edit { prefs -> prefs[Claves.FOTO_RUTA] = ruta }
    }

    suspend fun guardarModoOscuro(activo: Boolean) {
        context.dataStore.edit { prefs -> prefs[Claves.MODO_OSCURO] = activo }
    }

    suspend fun guardarRecordatorios(activo: Boolean) {
        context.dataStore.edit { prefs -> prefs[Claves.RECORDATORIOS] = activo }
    }
}
