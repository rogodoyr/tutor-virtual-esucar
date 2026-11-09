package cl.esucar.tutor.ui.entregables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.esucar.tutor.TutorApp
import cl.esucar.tutor.data.repository.EntregableRepository
import cl.esucar.tutor.domain.model.Entregable
import cl.esucar.tutor.domain.model.EstadoEntregable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EntregablesUiState(
    val todos: List<Entregable> = emptyList(),
    val filtrados: List<Entregable> = emptyList(),
    val filtro: EstadoEntregable? = null,
    val cargando: Boolean = true
)

class EntregablesViewModel(
    private val entregableRepository: EntregableRepository
) : ViewModel() {

    private val filtro = MutableStateFlow<EstadoEntregable?>(null)

    val uiState: StateFlow<EntregablesUiState> = combine(
        entregableRepository.entregables,
        filtro
    ) { entregables, filtroActual ->
        EntregablesUiState(
            todos = entregables,
            filtrados = if (filtroActual == null) entregables
            else entregables.filter { it.estado == filtroActual },
            filtro = filtroActual,
            cargando = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EntregablesUiState()
    )

    fun filtrarPor(estado: EstadoEntregable?) {
        filtro.value = estado
    }

    fun avanzarEstado(entregable: Entregable) {
        val siguiente = when (entregable.estado) {
            EstadoEntregable.POR_INICIAR -> EstadoEntregable.EN_DESARROLLO
            EstadoEntregable.EN_DESARROLLO -> EstadoEntregable.ENVIADO
            EstadoEntregable.ENVIADO -> EstadoEntregable.APROBADO
            EstadoEntregable.APROBADO -> EstadoEntregable.APROBADO
        }
        if (siguiente != entregable.estado) {
            viewModelScope.launch {
                entregableRepository.actualizar(entregable.copy(estado = siguiente))
            }
        }
    }

    fun eliminar(entregable: Entregable) {
        viewModelScope.launch {
            entregableRepository.eliminar(entregable)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TutorApp
                EntregablesViewModel(app.entregableRepository)
            }
        }
    }
}
