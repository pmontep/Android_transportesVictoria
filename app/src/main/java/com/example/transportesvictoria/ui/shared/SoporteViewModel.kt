package com.pointguatemala.transportesvictoria.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointguatemala.transportesvictoria.data.model.SoporteResponse
import com.pointguatemala.transportesvictoria.data.repository.SoporteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SoporteUiState(
    val isLoading: Boolean        = true,
    val soporte:   SoporteResponse? = null,
    val error:     String?        = null
)

class SoporteViewModel : ViewModel() {

    private val repository = SoporteRepository()

    private val _uiState = MutableStateFlow(SoporteUiState())
    val uiState: StateFlow<SoporteUiState> = _uiState.asStateFlow()

    init { cargarSoporte() }

    fun cargarSoporte() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getSoporte().fold(
                onSuccess = { soporte ->
                    _uiState.update { it.copy(isLoading = false, soporte = soporte) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }
}
