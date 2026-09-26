package com.pointguatemala.transportesvictoria.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointguatemala.transportesvictoria.data.model.ReclutadorUsuario
import com.pointguatemala.transportesvictoria.data.repository.ReclutadorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReclutadorUiState(
    val usuarios: List<ReclutadorUsuario> = emptyList(),
    val isLoading: Boolean     = false,
    val isRefreshing: Boolean  = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String?  = null,
    val currentPage: Int       = 1,
    val lastPage: Int          = 1,
    val total: Int             = 0,
    val query: String          = "",
    val statusFilter: String   = "0",   // "0"=inactivos, "1"=activos, ""=todos
    val togglingIds: Set<Int>  = emptySet()
)

class ReclutadorViewModel : ViewModel() {

    private val repository = ReclutadorRepository()

    private val _uiState = MutableStateFlow(ReclutadorUiState())
    val uiState: StateFlow<ReclutadorUiState> = _uiState.asStateFlow()

    init { cargarUsuarios() }

    fun cargarUsuarios(
        query: String        = _uiState.value.query,
        status: String       = _uiState.value.statusFilter,
        fromRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            if (fromRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null, query = query, statusFilter = status) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, query = query, statusFilter = status) }
            }
            repository.getUsuarios(status = status, query = query, page = 1).fold(
                onSuccess = { paginated ->
                    _uiState.update {
                        it.copy(
                            isLoading    = false,
                            isRefreshing = false,
                            usuarios     = paginated.data,
                            currentPage  = paginated.currentPage,
                            lastPage     = paginated.lastPage,
                            total        = paginated.total
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun cargarMas() {
        val state = _uiState.value
        if (state.isLoadingMore || state.currentPage >= state.lastPage) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            repository.getUsuarios(
                status = state.statusFilter,
                query  = state.query,
                page   = state.currentPage + 1
            ).fold(
                onSuccess = { paginated ->
                    _uiState.update { s ->
                        s.copy(
                            isLoadingMore = false,
                            usuarios      = s.usuarios + paginated.data,
                            currentPage   = paginated.currentPage,
                            lastPage      = paginated.lastPage
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoadingMore = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun toggleStatus(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(togglingIds = it.togglingIds + id) }
            repository.toggleStatus(id).fold(
                onSuccess = { response ->
                    _uiState.update { state ->
                        state.copy(
                            togglingIds = state.togglingIds - id,
                            usuarios    = state.usuarios.map { u ->
                                if (u.id == id) u.copy(status = response.newStatus) else u
                            }
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { state ->
                        state.copy(
                            togglingIds  = state.togglingIds - id,
                            errorMessage = e.message
                        )
                    }
                }
            )
        }
    }

    fun setQuery(q: String) {
        _uiState.update { it.copy(query = q) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
