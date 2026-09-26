package com.pointguatemala.transportesvictoria.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointguatemala.transportesvictoria.data.model.EmpresaItem
import com.pointguatemala.transportesvictoria.data.model.RegistroRequest
import com.pointguatemala.transportesvictoria.data.repository.RegistroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistroUiState(
    // Campos del formulario
    val nombre:       String  = "",
    val apellido:     String  = "",
    val email:        String  = "",
    val telefono:     String  = "",
    val tipoUsuario:  String  = "1",   // "1" = Empleado, "0" = En entrenamiento
    val carnet:       String  = "",
    val dpi:          String  = "",
    val departamento: String  = "",
    val direccion:    String  = "",
    val acceptedTerms: Boolean = false,

    val empresaSeleccionada: EmpresaItem? = null,

    // Lista de empresas
    val empresas:          List<EmpresaItem> = emptyList(),
    val isLoadingEmpresas: Boolean           = true,

    // Errores de validación por campo
    val nombreError:       Boolean = false,
    val apellidoError:     Boolean = false,
    val emailError:        Boolean = false,
    val telefonoError:     Boolean = false,
    val documentoError:    Boolean = false,
    val empresaError:      Boolean = false,
    val departamentoError: Boolean = false,
    val direccionError:    Boolean = false,
    val termsError:        Boolean = false,

    // Estado del envío
    val isLoading:      Boolean = false,
    val errorMessage:   String? = null,
    val registroExitoso: Boolean = false,
    val mensajeExito:   String  = "",
    val submitCount:    Int     = 0
)

class RegistroViewModel : ViewModel() {

    private val repository = RegistroRepository()

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    init { cargarEmpresas() }

    private fun cargarEmpresas() {
        viewModelScope.launch {
            repository.getEmpresas().fold(
                onSuccess = { lista ->
                    _uiState.update { it.copy(empresas = lista, isLoadingEmpresas = false) }
                },
                onFailure = {
                    _uiState.update { it.copy(isLoadingEmpresas = false) }
                }
            )
        }
    }

    fun onNombreChange(v: String)       = _uiState.update { it.copy(nombre = v, nombreError = false) }
    fun onApellidoChange(v: String)     = _uiState.update { it.copy(apellido = v, apellidoError = false) }
    fun onEmailChange(v: String)        = _uiState.update { it.copy(email = v, emailError = false) }
    fun onTelefonoChange(v: String)     = _uiState.update { it.copy(telefono = v, telefonoError = false) }
    fun onCarnetChange(v: String)       = _uiState.update { it.copy(carnet = v, documentoError = false) }
    fun onDpiChange(v: String)          = _uiState.update { it.copy(dpi = v, documentoError = false) }
    fun onDepartamentoChange(v: String) = _uiState.update { it.copy(departamento = v, departamentoError = false) }
    fun onDireccionChange(v: String)    = _uiState.update { it.copy(direccion = v, direccionError = false) }
    fun onTermsChange(v: Boolean)       = _uiState.update { it.copy(acceptedTerms = v, termsError = false) }
    fun onEmpresaSeleccionada(e: EmpresaItem) = _uiState.update { it.copy(empresaSeleccionada = e, empresaError = false) }

    fun onTipoUsuarioChange(tipo: String) =
        _uiState.update { it.copy(tipoUsuario = tipo, documentoError = false, carnet = "", dpi = "") }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }

    fun registrar() {
        val s = _uiState.value

        val esEmpleado = s.tipoUsuario == "1"
        val documento  = if (esEmpleado) s.carnet else s.dpi

        val nombreError       = s.nombre.isBlank()
        val apellidoError     = s.apellido.isBlank()
        val emailError        = s.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(s.email).matches()
        val telefonoError     = s.telefono.isBlank()
        val documentoError    = documento.isBlank()
        val empresaError      = s.empresaSeleccionada == null
        val departamentoError = s.departamento.isBlank()
        val direccionError    = s.direccion.isBlank()
        val termsError        = !s.acceptedTerms

        if (nombreError || apellidoError || emailError || telefonoError ||
            documentoError || empresaError || departamentoError || direccionError || termsError) {
            _uiState.update {
                it.copy(
                    nombreError = nombreError, apellidoError = apellidoError,
                    emailError = emailError, telefonoError = telefonoError,
                    documentoError = documentoError, empresaError = empresaError,
                    departamentoError = departamentoError, direccionError = direccionError,
                    termsError = termsError,
                    submitCount = it.submitCount + 1
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val request = RegistroRequest(
                name         = s.nombre.trim(),
                lastname     = s.apellido.trim(),
                email        = s.email.trim(),
                tipoUsuario  = s.tipoUsuario,
                carnet       = if (esEmpleado) s.carnet.trim() else null,
                dpi          = if (!esEmpleado) s.dpi.trim() else null,
                empresaIdL   = s.empresaSeleccionada!!.idL,
                departamento = s.departamento.trim(),
                direccion    = s.direccion.trim(),
                telefono     = s.telefono.trim(),
                acceptedTerms = true
            )

            repository.registro(request).fold(
                onSuccess = { resp ->
                    _uiState.update {
                        it.copy(isLoading = false, registroExitoso = true, mensajeExito = resp.message)
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }
}
