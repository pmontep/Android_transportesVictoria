package com.pointguatemala.transportesvictoria.ui.solicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointguatemala.transportesvictoria.data.model.Ruta
import com.pointguatemala.transportesvictoria.data.model.SolicitudRequest
import com.pointguatemala.transportesvictoria.data.repository.ViajeEnTransitoException
import com.pointguatemala.transportesvictoria.data.repository.ViajeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── Estado del formulario ─────────────────────────────────────────────────────
data class SolicitudFormUiState(
    // Rutas disponibles (cargadas al abrir la pantalla)
    val rutas: List<Ruta>         = emptyList(),
    val isLoadingRutas: Boolean   = true,
    val errorRutas: String?       = null,

    // Selección del usuario
    val rutaSeleccionada: Ruta?   = null,
    val horaSeleccionada: String? = null,
    val observaciones: String     = "",

    // Validación
    val rutaError: Boolean = false,
    val horaError: Boolean = false,

    // Petición POST
    val isLoading: Boolean          = false,
    val errorMessage: String?       = null,
    val solicitudCreada: Boolean    = false,

    // Error específico: usuario ya tiene un viaje en tránsito
    val tieneViajeActivo: Boolean   = false,
    val mensajeViajeActivo: String  = ""
)

class SolicitudFormViewModel : ViewModel() {

    private val repository = ViajeRepository()

    private val _uiState = MutableStateFlow(SolicitudFormUiState())
    val uiState: StateFlow<SolicitudFormUiState> = _uiState.asStateFlow()

    init { cargarRutas() }

    // ── Carga de rutas ────────────────────────────────────────────────────────

    fun cargarRutas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRutas = true, errorRutas = null) }

            repository.getMisRutas().fold(
                onSuccess = { rutas ->
                    _uiState.update {
                        it.copy(
                            isLoadingRutas   = false,
                            rutas            = rutas,
                            // Si solo hay una ruta la preseleccionamos
                            rutaSeleccionada = if (rutas.size == 1) rutas.first() else null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoadingRutas = false, errorRutas = error.message) }
                }
            )
        }
    }

    // ── Selección de campos ───────────────────────────────────────────────────

    fun seleccionarRuta(ruta: Ruta) {
        _uiState.update {
            it.copy(
                rutaSeleccionada = ruta,
                // Limpiar hora si cambia la ruta (los horarios pueden ser distintos)
                horaSeleccionada = null,
                rutaError        = false
            )
        }
    }

    fun seleccionarHora(hora: String) {
        _uiState.update { it.copy(horaSeleccionada = hora, horaError = false) }
    }

    fun setObservaciones(value: String) {
        _uiState.update { it.copy(observaciones = value) }
    }

    // ── Envío ─────────────────────────────────────────────────────────────────

    fun crearSolicitud() {
        val state = _uiState.value

        val rutaError = state.rutaSeleccionada == null
        val horaError = state.horaSeleccionada == null

        if (rutaError || horaError) {
            _uiState.update { it.copy(rutaError = rutaError, horaError = horaError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val request = SolicitudRequest(
                rutaSolicitada = state.rutaSeleccionada!!.nombre,
                horaSolicitud  = state.horaSeleccionada!!,
                observaciones  = state.observaciones.trim().ifBlank { null }
            )

            repository.crearSolicitud(request).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, solicitudCreada = true) }
                },
                onFailure = { error ->
                    when (error) {
                        is ViajeEnTransitoException ->
                            _uiState.update {
                                it.copy(
                                    isLoading          = false,
                                    tieneViajeActivo   = true,
                                    mensajeViajeActivo = error.message
                                        ?: "Ya tienes un viaje en tránsito."
                                )
                            }
                        else ->
                            _uiState.update {
                                it.copy(isLoading = false, errorMessage = error.message)
                            }
                    }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /** Consumir el evento para que no se dispare al volver a esta pantalla. */
    fun onSolicitudCreadaConsumed() {
        _uiState.update { it.copy(solicitudCreada = false) }
    }

    /** Cerrar el diálogo de viaje en tránsito. */
    fun clearViajeActivo() {
        _uiState.update { it.copy(tieneViajeActivo = false, mensajeViajeActivo = "") }
    }
}
