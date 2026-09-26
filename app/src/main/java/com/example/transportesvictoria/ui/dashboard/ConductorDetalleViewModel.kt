package com.pointguatemala.transportesvictoria.ui.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pointguatemala.transportesvictoria.data.model.ConductorViaje
import com.pointguatemala.transportesvictoria.data.repository.ConductorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConductorDetalleUiState(
    val viaje: ConductorViaje? = null,
    val isLoadingInicio: Boolean = false,
    val isLoadingFin: Boolean = false,
    val loadingAbordaje: Set<String> = emptySet(),
    val errorMessage: String? = null
)

class ConductorDetalleViewModel(initialViaje: ConductorViaje) : ViewModel() {

    private val repository = ConductorRepository()

    private val _uiState = MutableStateFlow(ConductorDetalleUiState(viaje = initialViaje))
    val uiState: StateFlow<ConductorDetalleUiState> = _uiState.asStateFlow()

    fun iniciarViaje() {
        val idL = _uiState.value.viaje?.id
        if (idL == null) {
            _uiState.update { it.copy(errorMessage = "No se puede identificar el viaje") }
            return
        }
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoadingInicio = true, errorMessage = null) }
                repository.iniciarViaje(idL).fold(
                    onSuccess = { fechaHoraInicio ->
                        _uiState.update { state ->
                            state.copy(
                                isLoadingInicio = false,
                                viaje = state.viaje?.copy(
                                    estadoViaje     = "En curso",
                                    fechaHoraInicio = fechaHoraInicio
                                )
                            )
                        }
                    },
                    onFailure = { e ->
                        _uiState.update { it.copy(isLoadingInicio = false, errorMessage = e.message) }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingInicio = false, errorMessage = e.message ?: "Error al iniciar viaje") }
            }
        }
    }

    fun finalizarViaje() {
        val idL = _uiState.value.viaje?.id
        if (idL == null) {
            _uiState.update { it.copy(errorMessage = "No se puede identificar el viaje") }
            return
        }
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoadingFin = true, errorMessage = null) }
                repository.finalizarViaje(idL).fold(
                    onSuccess = { fechaHoraFin ->
                        _uiState.update { state ->
                            state.copy(
                                isLoadingFin = false,
                                viaje = state.viaje?.copy(
                                    estadoViaje  = "Completado",
                                    fechaHoraFin = fechaHoraFin
                                )
                            )
                        }
                    },
                    onFailure = { e ->
                        _uiState.update { it.copy(isLoadingFin = false, errorMessage = e.message) }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingFin = false, errorMessage = e.message ?: "Error al finalizar viaje") }
            }
        }
    }

    fun toggleAbordaje(solicitudId: String?) {
        Log.d("ConductorDetalle", "toggleAbordaje llamado con solicitudId='$solicitudId'")
        if (solicitudId == null) {
            _uiState.update { it.copy(errorMessage = "Error: ID de solicitud nulo") }
            return
        }
        val id = solicitudId
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(loadingAbordaje = it.loadingAbordaje + id) }
                repository.abordarPasajero(id).fold(
                    onSuccess = { response ->
                        _uiState.update { state ->
                            val updatedPasajeros = state.viaje?.pasajeros?.map { p ->
                                if (p.solicitudId == id) {
                                    p.copy(
                                        abordado     = response.abordado ?: p.abordado ?: false,
                                        horaAbordaje = response.horaAbordaje
                                    )
                                } else p
                            } ?: emptyList()
                            state.copy(
                                loadingAbordaje = state.loadingAbordaje - id,
                                viaje = state.viaje?.copy(pasajeros = updatedPasajeros)
                            )
                        }
                    },
                    onFailure = { e ->
                        _uiState.update { state ->
                            state.copy(
                                loadingAbordaje = state.loadingAbordaje - id,
                                errorMessage    = e.message
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        loadingAbordaje = state.loadingAbordaje - id,
                        errorMessage    = e.message ?: "Error al registrar abordaje"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    class Factory(private val viaje: ConductorViaje) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ConductorDetalleViewModel(viaje) as T
    }
}
