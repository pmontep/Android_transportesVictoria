package com.pointguatemala.transportesvictoria.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pointguatemala.transportesvictoria.data.model.SolicitudViaje
import com.pointguatemala.transportesvictoria.data.repository.ViajeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalleSolicitudUiState(
    val solicitud: SolicitudViaje?  = null,
    val isLoading: Boolean          = false,
    val isCanceling: Boolean        = false,
    val isCalificating: Boolean     = false,
    val errorMessage: String?       = null,
    val solicitudCancelada: Boolean = false,
    val calificacionExitosa: Boolean = false
)

class DetalleSolicitudViewModel(private val solicitudId: String) : ViewModel() {

    private val repository = ViajeRepository()

    private val _uiState = MutableStateFlow(DetalleSolicitudUiState())
    val uiState: StateFlow<DetalleSolicitudUiState> = _uiState.asStateFlow()

    init { cargarDetalle() }

    fun cargarDetalle() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getDetalleSolicitud(solicitudId).fold(
                onSuccess = { solicitud ->
                    _uiState.update { it.copy(isLoading = false, solicitud = solicitud) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun cancelarSolicitud() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCanceling = true, errorMessage = null) }

            repository.cancelarSolicitud(solicitudId).fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(
                            isCanceling        = false,
                            solicitudCancelada = true,
                            solicitud          = state.solicitud?.copy(
                                estadoSolicitud = "Cancelado",
                                isActive        = 0
                            )
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isCanceling = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun calificarViaje(calificacion: Int, comentario: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCalificating = true, errorMessage = null) }

            repository.calificarViaje(solicitudId, calificacion, comentario).fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(
                            isCalificating    = false,
                            calificacionExitosa = true,
                            solicitud         = state.solicitud?.copy(
                                calificacion           = calificacion,
                                comentarioCalificacion = comentario?.takeIf { it.isNotBlank() }
                            )
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isCalificating = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun onCalificacionExitosaConsumed() {
        _uiState.update { it.copy(calificacionExitosa = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onCanceladaConsumed() {
        _uiState.update { it.copy(solicitudCancelada = false) }
    }

    class Factory(private val solicitudId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetalleSolicitudViewModel(solicitudId) as T
    }
}
