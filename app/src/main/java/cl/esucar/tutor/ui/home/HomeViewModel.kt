package cl.esucar.tutor.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.esucar.tutor.TutorApp
import cl.esucar.tutor.data.datastore.PerfilDataStore
import cl.esucar.tutor.data.repository.EntregableRepository
import cl.esucar.tutor.domain.model.EstadoEntregable
import cl.esucar.tutor.domain.model.Perfil
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val perfil: Perfil = Perfil(),
    val totalEntregables: Int = 0,
    val aprobados: Int = 0,
    val proximos: List<cl.esucar.tutor.domain.model.Entregable> = emptyList(),
    val cargando: Boolean = true
)

class HomeViewModel(
    private val entregableRepository: EntregableRepository,
    private val perfilDataStore: PerfilDataStore
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        entregableRepository.entregables,
        perfilDataStore.perfil
    ) { entregables, perfil ->
        HomeUiState(
            perfil = perfil,
            totalEntregables = entregables.size,
            aprobados = entregables.count { it.estado == EstadoEntregable.APROBADO },
            proximos = entregables
                .filter { it.estado != EstadoEntregable.APROBADO }
                .take(3),
            cargando = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TutorApp
                HomeViewModel(app.entregableRepository, app.perfilDataStore)
            }
        }
    }
}
