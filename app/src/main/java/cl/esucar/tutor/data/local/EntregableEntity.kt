package cl.esucar.tutor.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.esucar.tutor.domain.model.Entregable
import cl.esucar.tutor.domain.model.EstadoEntregable

/**
 * Entidad Room que persiste los entregables del Trabajo de Titulo.
 * Se mantiene separada del modelo de dominio para no acoplar
 * la persistencia a la logica de negocio.
 */
@Entity(tableName = "entregables")
data class EntregableEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val etapa: String,
    val descripcion: String,
    val fechaLimite: Long,
    val estado: String,
    val recordatorioActivo: Boolean
) {
    fun aDominio(): Entregable = Entregable(
        id = id,
        titulo = titulo,
        etapa = etapa,
        descripcion = descripcion,
        fechaLimite = fechaLimite,
        estado = EstadoEntregable.desdeTexto(estado),
        recordatorioActivo = recordatorioActivo
    )

    companion object {
        fun desdeDominio(e: Entregable): EntregableEntity = EntregableEntity(
            id = e.id,
            titulo = e.titulo,
            etapa = e.etapa,
            descripcion = e.descripcion,
            fechaLimite = e.fechaLimite,
            estado = e.estado.name,
            recordatorioActivo = e.recordatorioActivo
        )
    }
}
