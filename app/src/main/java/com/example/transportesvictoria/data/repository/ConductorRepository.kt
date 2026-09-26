package com.pointguatemala.transportesvictoria.data.repository

import android.util.Log
import com.pointguatemala.transportesvictoria.data.model.AbordajeResponse
import com.pointguatemala.transportesvictoria.data.model.ConductorViaje
import com.pointguatemala.transportesvictoria.data.network.RetrofitClient
import com.pointguatemala.transportesvictoria.data.session.SessionManager
import com.google.gson.JsonParser

class ConductorRepository {

    private val tag = "ConductorRepository"
    private val api = RetrofitClient.apiService

    suspend fun getViajesDelDia(fecha: String? = null): Result<List<ConductorViaje>> {
        return try {
            Log.d(tag, "→ getViajesDelDia fecha=$fecha authHeader='${SessionManager.authHeader.take(30)}…'")
            val response = api.getConductorViajes(SessionManager.authHeader, fecha)
            Log.d(tag, "  HTTP ${response.code()} ${response.message()}")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val viajes = body.data
                    Log.d(tag, "  ✓ ${viajes.size} viajes")
                    viajes.forEachIndexed { i, v -> Log.d(tag, "    [$i] id='${v.id}' ruta='${v.nombreRuta}' pasajeros=${v.pasajeros?.size}") }
                    Result.success(viajes)
                } else {
                    val rawError = response.errorBody()?.string() ?: "(body nulo)"
                    Log.e(tag, "  ✗ body=null raw=$rawError")
                    Result.failure(Exception("Respuesta vacía del servidor. Revisa Logcat (tag ConductorRepository)."))
                }
            } else {
                val raw = response.errorBody()?.string() ?: ""
                Log.e(tag, "  ✗ Error ${response.code()}: $raw")
                Result.failure(parseError(response.code(), raw))
            }
        } catch (e: Exception) {
            Log.e(tag, "  ✗ ${e::class.simpleName}: ${e.message}")
            Result.failure(connectionError(e))
        }
    }

    suspend fun iniciarViaje(idL: String): Result<String> {
        return try {
            Log.d(tag, "→ iniciarViaje idL='$idL'")
            val response = api.iniciarViaje(SessionManager.authHeader, idL)
            Log.d(tag, "  HTTP ${response.code()}")
            if (response.isSuccessful) {
                val fecha = response.body()?.fechaHoraInicio ?: ""
                Log.d(tag, "  ✓ inicio=$fecha")
                Result.success(fecha)
            } else {
                val raw = response.errorBody()?.string() ?: ""
                Log.e(tag, "  ✗ Error ${response.code()}: $raw")
                Result.failure(parseError(response.code(), raw))
            }
        } catch (e: Exception) {
            Log.e(tag, "  ✗ ${e::class.simpleName}: ${e.message}")
            Result.failure(connectionError(e))
        }
    }

    suspend fun finalizarViaje(idL: String): Result<String> {
        return try {
            Log.d(tag, "→ finalizarViaje idL='$idL'")
            val response = api.finalizarViaje(SessionManager.authHeader, idL)
            Log.d(tag, "  HTTP ${response.code()}")
            if (response.isSuccessful) {
                val fecha = response.body()?.fechaHoraFin ?: ""
                Log.d(tag, "  ✓ fin=$fecha")
                Result.success(fecha)
            } else {
                val raw = response.errorBody()?.string() ?: ""
                Log.e(tag, "  ✗ Error ${response.code()}: $raw")
                Result.failure(parseError(response.code(), raw))
            }
        } catch (e: Exception) {
            Log.e(tag, "  ✗ ${e::class.simpleName}: ${e.message}")
            Result.failure(connectionError(e))
        }
    }

    suspend fun abordarPasajero(idL: String): Result<AbordajeResponse> {
        return try {
            Log.d(tag, "→ abordarPasajero idL='$idL'")
            val response = api.abordarPasajero(SessionManager.authHeader, idL)
            Log.d(tag, "  HTTP ${response.code()}")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d(tag, "  ✓ abordado=${body.abordado}")
                    Result.success(body)
                } else {
                    Log.e(tag, "  ✗ body=null en abordarPasajero")
                    Result.failure(Exception("Respuesta vacía del servidor al registrar abordaje."))
                }
            } else {
                val raw = response.errorBody()?.string() ?: ""
                Log.e(tag, "  ✗ Error ${response.code()}: $raw")
                Result.failure(parseError(response.code(), raw))
            }
        } catch (e: Exception) {
            Log.e(tag, "  ✗ ${e::class.simpleName}: ${e.message}")
            Result.failure(connectionError(e))
        }
    }

    private fun parseError(code: Int, raw: String): Exception {
        val apiMsg = parseMensaje(raw)
        return when (code) {
            400  -> Exception(apiMsg ?: "El viaje ya está en curso, completado o no se puede modificar.")
            401  -> Exception(apiMsg ?: "Sin autorización. Inicia sesión de nuevo.")
            403  -> Exception(apiMsg ?: "Este viaje no está asignado a tu cuenta.")
            404  -> Exception(apiMsg ?: "Viaje no encontrado o no asignado al conductor.")
            422  -> Exception(apiMsg ?: "El viaje ya fue completado o cancelado.")
            else -> Exception(apiMsg ?: "Error del servidor ($code).")
        }
    }

    private fun parseMensaje(raw: String): String? = try {
        JsonParser.parseString(raw).asJsonObject.get("message")?.asString
    } catch (_: Exception) { null }

    private fun connectionError(e: Exception): Exception = when {
        e.message?.contains("Unable to resolve host") == true ||
        e.message?.contains("Failed to connect") == true ->
            Exception("No se pudo conectar al servidor. Verifica tu conexión.")
        else -> Exception("Error: ${e.message ?: "desconocido"}")
    }
}
