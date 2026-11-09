package cl.esucar.tutor.ui.entregables

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.esucar.tutor.TutorApp
import cl.esucar.tutor.data.repository.EntregableRepository
import cl.esucar.tutor.data.repository.GuiaRepository
import cl.esucar.tutor.domain.model.Entregable
import cl.esucar.tutor.domain.model.EstadoEntregable
import cl.esucar.tutor.domain.validation.ValidadorEntregable
import cl.esucar.tutor.notifications.RecordatorioScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

/**
 * Estado del formulario de registro de entregable. Las validaciones se
 * calculan en el dominio (ValidadorEntregable) y la UI solo las muestra.
 */
data class EntregableFormState(
    val titulo: String = "",
    val etapa: String = "",
    val descripcion: String = "",
    val fechaMillis: Long? = null,
    val estado: EstadoEntregable = EstadoEntregable.POR_INICIAR,
    val recordatorioActivo: Boolean = true,
    val errores: ValidadorEntregable.Errores = ValidadorEntregable.Errores(),
    val mostrarErrores: Boolean = false,
    val guardadoExitoso: Boolean = false
)

class EntregableFormViewModel(
    private val entregableRepository: EntregableRepository,
    private val guiaRepository: GuiaRepository,
    private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(EntregableFormState())
    val uiState: StateFlow<EntregableFormState> = _uiState.asStateFlow()

    fun etapasDisponibles(): List<String> = guiaRepository.etapas().map { it.titulo }

    fun onTituloChange(valor: String) = actualizar { it.copy(titulo = valor) }
    fun onEtapaChange(valor: String) = actualizar { it.copy(etapa = valor) }
    fun onDescripcionChange(valor: String) = actualizar { it.copy(descripcion = valor) }
    /**
     * Recibe la fecha del DatePicker (medianoche UTC) y la normaliza al
     * inicio del dia local, para que la validacion compare dias y no horas.
     */
    fun onFechaChange(millis: Long?) {
        val normalizada = millis?.let { normalizarFechaLocal(it) }
        actualizar { it.copy(fechaMillis = normalizada) }
    }

    private fun normalizarFechaLocal(millisUtc: Long): Long {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millisUtc }
        return Calendar.getInstance().apply {
            set(
                utc.get(Calendar.YEAR),
                utc.get(Calendar.MONTH),
                utc.get(Calendar.DAY_OF_MONTH),
                0, 0, 0
            )
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    fun onEstadoChange(valor: EstadoEntregable) = actualizar { it.copy(estado = valor) }
    fun onRecordatorioChange(valor: Boolean) = actualizar { it.copy(recordatorioActivo = valor) }

    /**
     * Revalida el formulario con cada cambio cuando ya se intento guardar,
     * de modo que la interfaz responde de inmediato a los cambios de estado.
     */
    private fun actualizar(cambio: (EntregableFormState) -> EntregableFormState) {
        _uiState.update { estado ->
            val nuevo = cambio(estado)
            if (nuevo.mostrarErrores) {
                nuevo.copy(errores = validar(nuevo))
            } else {
                nuevo
            }
        }
    }

    private fun validar(estado: EntregableFormState): ValidadorEntregable.Errores =
        ValidadorEntregable.validar(
            titulo = estado.titulo,
            etapa = estado.etapa,
            descripcion = estado.descripcion,
            fechaLimite = estado.fechaMillis,
            hoyMillis = inicioDelDiaDeHoy()
        )

    private fun inicioDelDiaDeHoy(): Long =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    fun guardar() {
        val estado = _uiState.value
        val errores = validar(estado)
        if (errores.hayErrores) {
            _uiState.value = estado.copy(errores = errores, mostrarErrores = true)
            return
        }

        val entregable = Entregable(
            titulo = estado.titulo.trim(),
            etapa = estado.etapa,
            descripcion = estado.descripcion.trim(),
            fechaLimite = estado.fechaMillis ?: 0L,
            estado = estado.estado,
            recordatorioActivo = estado.recordatorioActivo
        )

        viewModelScope.launch {
            val id = entregableRepository.guardar(entregable)
            if (entregable.recordatorioActivo) {
                programarRecordatorio(id, entregable)
            }
            _uiState.update { it.copy(guardadoExitoso = true) }
        }
    }

    /**
     * Recurso nativo: notificacion local un dia antes de la fecha limite.
     * Si ese momento ya paso, el recordatorio se dispara a los pocos segundos
     * para que el estudiante reciba la alerta de inmediato.
     */
    private fun programarRecordatorio(id: Long, entregable: Entregable) {
        val unDiaAntes = entregable.fechaLimite - UN_DIA_MILLIS
        val ahora = System.currentTimeMillis()
        val cuando = if (unDiaAntes > ahora) unDiaAntes else ahora + 5_000L

        RecordatorioScheduler.programar(
            context = appContext,
            id = id.toInt(),
            titulo = "Entrega proxima: ${entregable.titulo}",
            mensaje = "Recuerda que \"${entregable.titulo}\" (${entregable.etapa}) vence pronto. " +
                "Revisa el formato requerido antes de enviarlo.",
            cuandoMillis = cuando
        )
    }

    companion object {
        private const val UN_DIA_MILLIS = 24 * 60 * 60 * 1000L

        fun factory(app: TutorApp): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                EntregableFormViewModel(
                    app.entregableRepository,
                    app.guiaRepository,
                    app.applicationContext
                )
            }
        }
    }
}
