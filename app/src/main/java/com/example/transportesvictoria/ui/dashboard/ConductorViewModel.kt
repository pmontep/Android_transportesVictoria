package com.pointguatemala.transportesvictoria.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointguatemala.transportesvictoria.data.model.ConductorViaje
import com.pointguatemala.transportesvictoria.data.repository.ConductorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConductorUiState(
    val viajes: List<ConductorViaje> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

class ConductorViewModel : ViewModel() {

    private val repository = ConductorRepository()

    private val _uiState = MutableStateFlow(ConductorUiState())
    val uiState: StateFlow<ConductorUiState> = _uiState.asStateFlow()

    init {
        cargarViajes()
    }

    fun cargarViajes(fromRefresh: Boolean = false) {
        viewModelScope.launch {
            if (fromRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }
            repository.getViajesDelDia().fold(
                onSuccess = { viajes ->
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false, viajes = viajes) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
