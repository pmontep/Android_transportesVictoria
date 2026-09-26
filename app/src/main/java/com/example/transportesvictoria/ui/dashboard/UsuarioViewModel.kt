package com.pointguatemala.transportesvictoria.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointguatemala.transportesvictoria.data.model.SolicitudViaje
import com.pointguatemala.transportesvictoria.data.repository.ViajeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UsuarioUiState(
    val solicitudes: List<SolicitudViaje> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val errorCargaSolicitudes: Boolean = false,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val query: String = "",
    val solicitudACalificar: SolicitudViaje? = null,
    val isCalificating: Boolean = false,
    val calificacionExitosa: Boolean = false
)

class UsuarioViewModel : ViewModel() {

    private val repository = ViajeRepository()

    private val _uiState = MutableStateFlow(UsuarioUiState())
    val uiState: StateFlow<UsuarioUiState> = _uiState.asStateFlow()

    init {
        cargarSolicitudes()
    }

    /** Carga (o recarga) la primera página de solicitudes */
    fun cargarSolicitudes(query: String = _uiState.value.query, fromRefresh: Boolean = false) {
        viewModelScope.launch {
            if (fromRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null, query = query) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, query = query) }
            }

            val result = repository.getMisSolicitudes(page = 1, query = query)

            result.fold(
                onSuccess = { paginated ->
                    _uiState.update {
                        it.copy(
                            isLoading            = false,
                            isRefreshing        = false,
                            solicitudes         = paginated.data.distinctBy { it.idL },
                            currentPage         = paginated.currentPage,
                            totalPages          = paginated.lastPage,
                            errorCargaSolicitudes = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading            = false,
                            isRefreshing        = false,
                            errorMessage        = error.message,
                            errorCargaSolicitudes = true
                        )
                    }
                }
            )
        }
    }

    fun abrirCalificar(solicitud: SolicitudViaje) {
        _uiState.update { it.copy(solicitudACalificar = solicitud) }
    }

    fun cerrarCalificar() {
        _uiState.update { it.copy(solicitudACalificar = null, isCalificating = false) }
    }

    fun onCalificacionExitosaConsumed() {
        _uiState.update { it.copy(calificacionExitosa = false, solicitudACalificar = null) }
    }

    fun calificarViaje(calificacion: Int, comentario: String?) {
        val solicitud = _uiState.value.solicitudACalificar ?: return
        val idL = solicitud.idL ?: solicitud.id.toString()
        viewModelScope.launch {
            _uiState.update { it.copy(isCalificating = true) }
            val result = repository.calificarViaje(idL, calificacion, comentario)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isCalificating = false, calificacionExitosa = true) }
                    cargarSolicitudes()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isCalificating = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearErrorCargaSolicitudes() {
        _uiState.update { it.copy(errorCargaSolicitudes = false) }
    }
}
