package cl.esucar.tutor.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos local (Room) de la aplicacion. Persiste los entregables
 * y su estado, permitiendo el funcionamiento sin conexion.
 */
@Database(entities = [EntregableEntity::class], version = 1, exportSchema = false)
abstract class TutorDatabase : RoomDatabase() {

    abstract fun entregableDao(): EntregableDao

    companion object {
        @Volatile
        private var instancia: TutorDatabase? = null

        fun obtener(context: Context): TutorDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    TutorDatabase::class.java,
                    "tutor_esucar.db"
                ).build().also { instancia = it }
            }
    }
}
