package com.pointguatemala.transportesvictoria.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointguatemala.transportesvictoria.data.repository.TerminosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TerminosUiState(
    val isLoading: Boolean = true,
    val texto:     String? = null,
    val error:     String? = null
)

class TerminosViewModel : ViewModel() {

    private val repository = TerminosRepository()

    private val _uiState = MutableStateFlow(TerminosUiState())
    val uiState: StateFlow<TerminosUiState> = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getTerminos().fold(
                onSuccess = { texto ->
                    _uiState.update { it.copy(isLoading = false, texto = texto) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }
}
