package com.pointguatemala.transportesvictoria.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointguatemala.transportesvictoria.data.repository.AyudaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AyudaUiState {
    object Loading                        : AyudaUiState()
    data class Error(val message: String) : AyudaUiState()
    object Empty                          : AyudaUiState()
    data class Success(val html: String)  : AyudaUiState()
}

class AyudaViewModel : ViewModel() {

    private val repository = AyudaRepository()

    private val _uiState = MutableStateFlow<AyudaUiState>(AyudaUiState.Loading)
    val uiState: StateFlow<AyudaUiState> = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = AyudaUiState.Loading
            repository.getAyuda().fold(
                onSuccess = { html ->
                    _uiState.value = if (html.isNullOrBlank()) {
                        AyudaUiState.Empty
                    } else {
                        AyudaUiState.Success(html)
                    }
                },
                onFailure = { e ->
                    _uiState.value = AyudaUiState.Error(
                        e.message ?: "No se pudo cargar la ayuda."
                    )
                }
            )
        }
    }
}
