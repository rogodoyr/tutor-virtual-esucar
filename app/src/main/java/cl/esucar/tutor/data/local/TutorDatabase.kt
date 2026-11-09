package cl.esucar.tutor.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Base de datos local (Room) de la aplicacion. Persiste los entregables
 * y su estado, permitiendo el funcionamiento sin conexion.
 */
@Database(
    entities = [EntregableEntity::class, UsuarioEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TutorDatabase : RoomDatabase() {

    abstract fun entregableDao(): EntregableDao

    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var instancia: TutorDatabase? = null

        /** Migracion 1 -> 2: agrega la tabla de usuarios (conserva los datos). */
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `usuarios` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`nombre` TEXT NOT NULL, " +
                        "`email` TEXT NOT NULL, " +
                        "`passwordHash` TEXT NOT NULL, " +
                        "`salt` TEXT NOT NULL, " +
                        "`rol` TEXT NOT NULL)"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_email` " +
                        "ON `usuarios` (`email`)"
                )
            }
        }

        fun obtener(context: Context): TutorDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    TutorDatabase::class.java,
                    "tutor_esucar.db"
                ).addMigrations(MIGRACION_1_2).build().also { instancia = it }
            }
    }
}
