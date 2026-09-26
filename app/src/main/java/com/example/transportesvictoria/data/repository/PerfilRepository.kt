package com.pointguatemala.transportesvictoria.data.repository

import android.util.Log
import com.pointguatemala.transportesvictoria.data.model.CambiarPasswordRequest
import com.pointguatemala.transportesvictoria.data.model.EliminacionDatosRequest
import com.pointguatemala.transportesvictoria.data.model.EliminacionDatosResponse
import com.pointguatemala.transportesvictoria.data.model.User
import com.pointguatemala.transportesvictoria.data.network.RetrofitClient
import com.pointguatemala.transportesvictoria.data.session.SessionManager
import com.google.gson.JsonParser

class PerfilRepository {

    private val tag = "PerfilRepository"
    private val apiService = RetrofitClient.apiService

    /**
     * Obtiene el perfil actualizado del usuario autenticado.
     * GET /api/me
     */
    suspend fun getMe(): Result<User> {
        return try {
            Log.d(tag, "→ getMe")
            val response = apiService.getMe(authorization = SessionManager.authHeader)
            Log.d(tag, "  HTTP ${response.code()} ${response.message()}")

            if (response.isSuccessful) {
                val user = response.body()?.item
                if (user != null) {
                    Log.d(tag, "  ✓ user id=${user.id} rol=${user.rolPrincipal}")
                    Result.success(user)
                } else {
                    Log.e(tag, "  ✗ body.item=null — revisa Logcat tag OkHttp")
                    Result.failure(Exception("Respuesta inesperada del servidor."))
                }
            } else {
                val rawError = response.errorBody()?.string() ?: ""
                Log.e(tag, "  ✗ Error HTTP ${response.code()}: $rawError")
                val errorMsg = when (response.code()) {
                    403  -> "Tu cuenta ha sido desactivada. Contacta a soporte."
                    401  -> "Sin autorización (401)."
                    else -> parsearMensaje(rawError) ?: "Error ${response.code()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "  ✗ ${e::class.simpleName} — ${e.message}")
            Result.failure(Exception("No se pudo conectar al servidor."))
        }
    }

    /**
     * Invalida el token en el servidor. Falla silenciosa si no hay red.
     * POST /api/logoutapi
     */
    suspend fun logout() {
        try {
            Log.d(tag, "→ logout")
            val response = apiService.logout(authorization = SessionManager.authHeader)
            Log.d(tag, "  HTTP ${response.code()} ${response.message()}")
        } catch (e: Exception) {
            Log.w(tag, "  ✗ logout falló (ignorado): ${e.message}")
        }
    }

    /**
     * Cambia la contraseña del usuario autenticado.
     * POST /api/perfil/password
     * Devuelve un mensaje de éxito o lanza excepción con mensaje legible.
     */
    suspend fun cambiarPassword(
        actual: String,
        nueva: String,
        confirmacion: String
    ): Result<String> {
        return try {
            Log.d(tag, "→ cambiarPassword")
            val response = apiService.cambiarPassword(
                authorization = SessionManager.authHeader,
                request       = CambiarPasswordRequest(
                    currentPassword      = actual,
                    password             = nueva,
                    passwordConfirmation = confirmacion
                )
            )
            Log.d(tag, "  HTTP ${response.code()} ${response.message()}")

            if (response.isSuccessful) {
                val mensaje = response.body()?.message ?: "Contraseña actualizada"
                Log.d(tag, "  ✓ $mensaje")
                Result.success(mensaje)
            } else {
                val rawError = response.errorBody()?.string() ?: ""
                Log.e(tag, "  ✗ Error HTTP ${response.code()}: $rawError")

                // 400 con error_code WRONG_PASSWORD
                if (response.code() == 400) {
                    val errorCode = parsearErrorCode(rawError)
                    if (errorCode == "WRONG_PASSWORD") {
                        return Result.failure(Exception("La contraseña actual es incorrecta."))
                    }
                }

                val errorMsg = parsearMensaje(rawError) ?: when (response.code()) {
                    401  -> "Sin autorización (401)."
                    422  -> "Las contraseñas no son válidas."
                    else -> "Error ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "  ✗ ${e::class.simpleName} — ${e.message}")
            Result.failure(Exception("No se pudo conectar al servidor."))
        }
    }

    /**
     * Registra una solicitud de eliminación de datos para la cuenta autenticada.
     * POST /api/solicitar-eliminacion-datos
     * 201 → solicitud registrada (no elimina nada de inmediato).
     * 409 → ya existe una solicitud activa → Result.failure con "DATA_DELETION_ALREADY_REQUESTED".
     */
    suspend fun solicitarEliminacionDatos(motivo: String? = null): Result<EliminacionDatosResponse> {
        return try {
            Log.d(tag, "→ solicitarEliminacionDatos")
            val response = apiService.solicitarEliminacionDatos(
                authorization = SessionManager.authHeader,
                request       = EliminacionDatosRequest(motivo = motivo)
            )
            Log.d(tag, "  HTTP ${response.code()} ${response.message()}")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d(tag, "  ✓ solicitud registrada id=${body.solicitud?.id}")
                    Result.success(body)
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                val rawError = response.errorBody()?.string() ?: ""
                Log.e(tag, "  ✗ Error HTTP ${response.code()}: $rawError")
                if (response.code() == 409) {
                    Result.failure(Exception("DATA_DELETION_ALREADY_REQUESTED"))
                } else {
                    val errorMsg = parsearMensaje(rawError) ?: "Error ${response.code()}"
                    Result.failure(Exception(errorMsg))
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "  ✗ ${e::class.simpleName} — ${e.message}")
            Result.failure(Exception("No se pudo conectar al servidor."))
        }
    }

    private fun parsearMensaje(rawJson: String): String? = try {
        JsonParser.parseString(rawJson).asJsonObject.get("message")?.asString
    } catch (_: Exception) { null }

    private fun parsearErrorCode(rawJson: String): String? = try {
        JsonParser.parseString(rawJson).asJsonObject.get("error_code")?.asString
    } catch (_: Exception) { null }
}
