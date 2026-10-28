package com.pointguatemala.transportesvictoria.ui.legal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pointguatemala.transportesvictoria.data.model.DocumentoLegalType
import com.pointguatemala.transportesvictoria.data.repository.DocumentoLegalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DocumentoLegalUiState {
    object Loading                         : DocumentoLegalUiState()
    data class Error(val message: String)  : DocumentoLegalUiState()
    data class Empty(val message: String)  : DocumentoLegalUiState()
    data class Success(val html: String)   : DocumentoLegalUiState()
}

class DocumentoLegalViewModel(val tipo: DocumentoLegalType) : ViewModel() {

    private val repository = DocumentoLegalRepository()

    private val _uiState = MutableStateFlow<DocumentoLegalUiState>(DocumentoLegalUiState.Loading)
    val uiState: StateFlow<DocumentoLegalUiState> = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = DocumentoLegalUiState.Loading
            repository.getDocumento(tipo).fold(
                onSuccess = { html ->
                    _uiState.value = if (html.isNullOrBlank()) {
                        DocumentoLegalUiState.Empty(tipo.mensajeVacio())
                    } else {
                        DocumentoLegalUiState.Success(html)
                    }
                },
                onFailure = { e ->
                    _uiState.value = DocumentoLegalUiState.Error(
                        e.message ?: "No se pudo cargar el documento."
                    )
                }
            )
        }
    }

    class Factory(private val tipo: DocumentoLegalType) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DocumentoLegalViewModel(tipo) as T
    }
}
