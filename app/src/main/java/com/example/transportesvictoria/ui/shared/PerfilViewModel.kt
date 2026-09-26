package com.pointguatemala.transportesvictoria.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pointguatemala.transportesvictoria.data.model.User
import com.pointguatemala.transportesvictoria.data.repository.PerfilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PerfilUiState(
    val user: User,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isEliminando: Boolean = false,
    val eliminacionEnviada: Boolean = false,       // true tras 201
    val eliminacionYaSolicitada: Boolean = false   // true tras 409
)

class PerfilViewModel(initialUser: User) : ViewModel() {

    private val repository = PerfilRepository()

    private val _uiState = MutableStateFlow(PerfilUiState(user = initialUser))
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init { refrescarPerfil() }

    fun refrescarPerfil() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            repository.getMe().fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(isRefreshing = false, user = user) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isRefreshing = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun solicitarEliminacion(motivo: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isEliminando = true, errorMessage = null) }
            repository.solicitarEliminacionDatos(motivo).fold(
                onSuccess = {
                    _uiState.update { it.copy(isEliminando = false, eliminacionEnviada = true) }
                },
                onFailure = { error ->
                    if (error.message == "DATA_DELETION_ALREADY_REQUESTED") {
                        _uiState.update {
                            it.copy(isEliminando = false, eliminacionYaSolicitada = true)
                        }
                    } else {
                        _uiState.update {
                            it.copy(isEliminando = false, errorMessage = error.message)
                        }
                    }
                }
            )
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }

    class Factory(private val initialUser: User) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PerfilViewModel(initialUser) as T
    }
}
