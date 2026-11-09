package cl.esucar.tutor.ui.faq

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.esucar.tutor.TutorApp
import cl.esucar.tutor.data.repository.FaqRepository
import cl.esucar.tutor.domain.model.PreguntaFaq
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FaqUiState(
    val consulta: String = "",
    val categoria: String = FaqRepository.CATEGORIA_TODAS,
    val categorias: List<String> = emptyList(),
    val resultados: List<PreguntaFaq> = emptyList()
)

/**
 * Gestiona el buscador del banco de preguntas frecuentes.
 * El filtrado se calcula en la logica (repositorio), no en la UI.
 */
class FaqViewModel(private val faqRepository: FaqRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FaqUiState(
            categorias = faqRepository.categorias(),
            resultados = faqRepository.preguntas()
        )
    )
    val uiState: StateFlow<FaqUiState> = _uiState.asStateFlow()

    fun onConsultaChange(texto: String) {
        _uiState.update { estado ->
            estado.copy(
                consulta = texto,
                resultados = faqRepository.buscar(texto, estado.categoria)
            )
        }
    }

    fun onCategoriaChange(categoria: String) {
        _uiState.update { estado ->
            estado.copy(
                categoria = categoria,
                resultados = faqRepository.buscar(estado.consulta, categoria)
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TutorApp
                FaqViewModel(app.faqRepository)
            }
        }
    }
}
