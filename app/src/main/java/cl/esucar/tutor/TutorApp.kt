package cl.esucar.tutor

import android.app.Application
import android.content.Context
import cl.esucar.tutor.data.datastore.PerfilDataStore
import cl.esucar.tutor.data.local.TutorDatabase
import cl.esucar.tutor.data.repository.EntregableRepository
import cl.esucar.tutor.data.repository.FaqRepository
import cl.esucar.tutor.data.repository.GuiaRepository
import cl.esucar.tutor.notifications.NotificacionHelper

/**
 * Punto de entrada de la aplicacion. Actua como ServiceLocator sencillo:
 * concentra la creacion de la base de datos local (Room), el DataStore de
 * preferencias y los repositorios, de modo que los ViewModels no dependan
 * de la capa visual (arquitectura MVVM).
 */
class TutorApp : Application() {

    val database: TutorDatabase by lazy { TutorDatabase.obtener(this) }

    val entregableRepository: EntregableRepository by lazy {
        EntregableRepository(database.entregableDao())
    }

    val perfilDataStore: PerfilDataStore by lazy { PerfilDataStore(this) }

    val guiaRepository: GuiaRepository by lazy { GuiaRepository() }

    val faqRepository: FaqRepository by lazy { FaqRepository() }

    override fun onCreate() {
        super.onCreate()
        NotificacionHelper.crearCanal(this)
    }

    companion object {
        fun desde(context: Context): TutorApp = context.applicationContext as TutorApp
    }
}
