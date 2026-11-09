package cl.esucar.tutor.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntregableDao {

    @Query("SELECT * FROM entregables ORDER BY fechaLimite ASC")
    fun observarTodos(): Flow<List<EntregableEntity>>

    @Query("SELECT * FROM entregables WHERE id = :id")
    suspend fun porId(id: Long): EntregableEntity?

    @Insert
    suspend fun insertar(entregable: EntregableEntity): Long

    @Update
    suspend fun actualizar(entregable: EntregableEntity)

    @Delete
    suspend fun eliminar(entregable: EntregableEntity)
}
