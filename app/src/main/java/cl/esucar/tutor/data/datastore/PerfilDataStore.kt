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
