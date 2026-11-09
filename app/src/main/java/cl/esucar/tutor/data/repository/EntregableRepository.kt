package cl.esucar.tutor.data.repository

import cl.esucar.tutor.data.local.EntregableDao
import cl.esucar.tutor.data.local.EntregableEntity
import cl.esucar.tutor.domain.model.Entregable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repositorio de entregables: encapsula el acceso a Room y expone
 * modelos de dominio, de modo que los ViewModels no conocen la BD.
 */
class EntregableRepository(private val dao: EntregableDao) {

    val entregables: Flow<List<Entregable>> =
        dao.observarTodos().map { lista -> lista.map { it.aDominio() } }

    suspend fun guardar(entregable: Entregable): Long =
        dao.insertar(EntregableEntity.desdeDominio(entregable))

    suspend fun actualizar(entregable: Entregable) =
        dao.actualizar(EntregableEntity.desdeDominio(entregable))

    suspend fun eliminar(entregable: Entregable) =
        dao.eliminar(EntregableEntity.desdeDominio(entregable))
}
