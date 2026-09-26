package com.pointguatemala.transportesvictoria.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointguatemala.transportesvictoria.data.repository.PerfilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CambiarPasswordUiState(
    val actual:       String  = "",
    val nueva:        String  = "",
    val confirmacion: String  = "",

    val actualError:       Boolean = false,
    val nuevaError:        Boolean = false,
    val confirmacionError: Boolean = false,

    val isLoading:    Boolean = false,
    val errorMessage: String? = null,
    val exitoso:      Boolean = false
)

class CambiarPasswordViewModel : ViewModel() {

    private val repository = PerfilRepository()

    private val _uiState = MutableStateFlow(CambiarPasswordUiState())
    val uiState: StateFlow<CambiarPasswordUiState> = _uiState.asStateFlow()

    fun onActualChange(v: String)       = _uiState.update { it.copy(actual = v,       actualError = false) }
    fun onNuevaChange(v: String)        = _uiState.update { it.copy(nueva = v,         nuevaError = false) }
    fun onConfirmacionChange(v: String) = _uiState.update { it.copy(confirmacion = v, confirmacionError = false) }

    fun cambiarPassword() {
        val s = _uiState.value

        // Validaciones locales
        val actualError       = s.actual.isBlank()
        val nuevaError        = s.nueva.length < 8
        val confirmacionError = s.confirmacion != s.nueva
        val nuevaIgualActual  = s.nueva == s.actual && s.actual.isNotBlank()

        if (actualError || nuevaError || confirmacionError || nuevaIgualActual) {
            val msg = when {
                actualError       -> "Ingresa tu contraseña actual."
                nuevaError        -> "La nueva contraseña debe tener al menos 8 caracteres."
                confirmacionError -> "Las contraseñas no coinciden."
                nuevaIgualActual  -> "La nueva contraseña debe ser diferente a la actual."
                else              -> null
            }
            _uiState.update {
                it.copy(
                    actualError       = actualError,
                    nuevaError        = nuevaError || nuevaIgualActual,
                    confirmacionError = confirmacionError,
                    errorMessage      = msg
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.cambiarPassword(s.actual, s.nueva, s.confirmacion).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, exitoso = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun clearError()  = _uiState.update { it.copy(errorMessage = null) }
    fun onExitosoConsumed() = _uiState.update { it.copy(exitoso = false) }
}
