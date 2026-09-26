package com.pointguatemala.transportesvictoria.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointguatemala.transportesvictoria.data.repository.RecuperarPasswordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PasoRecuperar { EMAIL, OTP, NUEVA_PASSWORD, EXITOSO }

data class RecuperarPasswordUiState(
    val paso: PasoRecuperar = PasoRecuperar.EMAIL,
    val email: String = "",
    val otp: String = "",
    val nuevaPassword: String = "",
    val confirmarPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class RecuperarPasswordViewModel : ViewModel() {

    private val repository = RecuperarPasswordRepository()

    private val _uiState = MutableStateFlow(RecuperarPasswordUiState())
    val uiState: StateFlow<RecuperarPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(v: String)            = _uiState.update { it.copy(email = v, errorMessage = null) }
    fun onOtpChange(v: String)              {
        val digits = v.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(otp = digits, errorMessage = null) }
    }
    fun onNuevaPasswordChange(v: String)    = _uiState.update { it.copy(nuevaPassword = v, errorMessage = null) }
    fun onConfirmarPasswordChange(v: String)= _uiState.update { it.copy(confirmarPassword = v, errorMessage = null) }
    fun clearError()                        = _uiState.update { it.copy(errorMessage = null) }

    fun enviarCodigo() {
        val email = _uiState.value.email
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El correo es requerido") }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(errorMessage = "Ingresa un correo válido") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.forgotPassword(email).fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, paso = PasoRecuperar.OTP) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
            )
        }
    }

    fun verificarCodigo() {
        val state = _uiState.value
        if (state.otp.length != 6) {
            _uiState.update { it.copy(errorMessage = "El código debe tener 6 dígitos") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.verifyOtp(state.email, state.otp).fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, paso = PasoRecuperar.NUEVA_PASSWORD) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
            )
        }
    }

    fun resetPassword() {
        val state = _uiState.value
        if (state.nuevaPassword.length < 8) {
            _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 8 caracteres") }
            return
        }
        if (state.nuevaPassword != state.confirmarPassword) {
            _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.resetPassword(state.email, state.otp, state.nuevaPassword).fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, paso = PasoRecuperar.EXITOSO) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
            )
        }
    }

    fun reenviarCodigo() {
        _uiState.update { it.copy(otp = "") }
        enviarCodigo()
    }
}
