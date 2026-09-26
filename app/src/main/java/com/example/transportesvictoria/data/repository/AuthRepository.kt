package com.pointguatemala.transportesvictoria.data.repository

import android.os.Build
import com.pointguatemala.transportesvictoria.data.model.LoginRequest
import com.pointguatemala.transportesvictoria.data.model.LoginResponse
import com.pointguatemala.transportesvictoria.data.model.RefreshTokenResponse
import com.pointguatemala.transportesvictoria.data.network.RetrofitClient
import com.pointguatemala.transportesvictoria.data.session.SessionManager
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.sync.withLock
import java.io.IOException

class AuthRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
            val request = LoginRequest(
                email = email.trim(),
                password = password,
                deviceName = deviceName
            )
            val response = apiService.login(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    SessionManager.saveToken(
                        token     = body.token,
                        tokenType = body.tokenType.ifBlank { "Bearer" },
                        issuedAt  = body.issuedAt
                    )
                    Result.success(body)
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                val rawError = response.errorBody()?.string() ?: ""
                val apiMsg = parsearMensaje(rawError)
                val errorMsg = apiMsg ?: when (response.code()) {
                    401 -> "Credenciales incorrectas"
                    403 -> "Tu cuenta no está activa. Contacta al administrador."
                    422 -> "Datos inválidos. Verifica tu correo y contraseña"
                    500 -> "Error interno del servidor"
                    else -> "Error ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: JsonSyntaxException) {
            Result.failure(Exception("No se pudo procesar la respuesta del servidor. Intenta de nuevo."))
        } catch (e: IOException) {
            val msg = when {
                e.message?.contains("Unable to resolve host") == true ||
                e.message?.contains("Failed to connect") == true ->
                    "No se pudo conectar al servidor. Verifica tu conexión."
                else -> "Error de conexión. Intenta de nuevo."
            }
            Result.failure(Exception(msg))
        } catch (e: Exception) {
            val msg = when {
                e.message?.contains("Unable to resolve host") == true ||
                e.message?.contains("Failed to connect") == true ->
                    "No se pudo conectar al servidor. Verifica tu conexión."
                else -> e.message ?: "Error desconocido"
            }
            Result.failure(Exception(msg))
        }
    }

    /**
     * Rota el token si pasaron ≥72h desde issued_at.
     * Usa un Mutex para evitar llamadas concurrentes duplicadas.
     * Si shouldRefresh() devuelve false (dentro del lock), retorna sin llamar al servidor.
     */
    suspend fun refreshToken(): Result<RefreshTokenResponse> {
        return SessionManager.refreshMutex.withLock {
            if (!SessionManager.shouldRefresh()) {
                return@withLock Result.success(
                    RefreshTokenResponse(
                        token     = null,
                        tokenType = "Bearer",
                        issuedAt  = SessionManager.issuedAt ?: "",
                        rotated   = false
                    )
                )
            }
            try {
                val response = apiService.refreshToken(SessionManager.authHeader)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        if (body.rotated && !body.token.isNullOrBlank()) {
                            SessionManager.saveToken(body.token, body.tokenType, body.issuedAt)
                        }
                        Result.success(body)
                    } else {
                        Result.failure(Exception("Respuesta vacía del servidor"))
                    }
                } else {
                    Result.failure(Exception("Error ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "Error desconocido"))
            }
        }
    }

    private fun parsearMensaje(rawJson: String): String? {
        return try {
            JsonParser.parseString(rawJson).asJsonObject.get("message")?.asString
        } catch (_: Exception) { null }
    }
}
