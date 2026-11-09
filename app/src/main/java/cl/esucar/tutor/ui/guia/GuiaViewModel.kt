package cl.esucar.tutor.ui.guia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.esucar.tutor.TutorApp
import cl.esucar.tutor.data.repository.GuiaRepository
import cl.esucar.tutor.domain.model.Etapa

class GuiaViewModel(private val guiaRepository: GuiaRepository) : ViewModel() {

    fun etapas(): List<Etapa> = guiaRepository.etapas()

    fun etapa(id: Int): Etapa = guiaRepository.etapaPorId(id) ?: guiaRepository.etapas().first()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TutorApp
                GuiaViewModel(app.guiaRepository)
            }
        }
    }
}
