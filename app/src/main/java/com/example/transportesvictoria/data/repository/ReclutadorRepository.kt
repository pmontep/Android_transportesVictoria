package com.pointguatemala.transportesvictoria.data.repository

import android.util.Log
import com.pointguatemala.transportesvictoria.data.model.PaginatedResponse
import com.pointguatemala.transportesvictoria.data.model.ReclutadorUsuario
import com.pointguatemala.transportesvictoria.data.model.ToggleStatusResponse
import com.pointguatemala.transportesvictoria.data.network.RetrofitClient
import com.pointguatemala.transportesvictoria.data.session.SessionManager
import com.google.gson.JsonParser

class ReclutadorRepository {

    private val tag = "ReclutadorRepository"
    private val api = RetrofitClient.apiService

    suspend fun getUsuarios(
        status: String = "0",
        query: String  = "",
        page: Int      = 1
    ): Result<PaginatedResponse<ReclutadorUsuario>> {
        return try {
            Log.d(tag, "→ getUsuarios status='$status' query='$query' page=$page")
            val response = api.getReclutadorUsuarios(
                authorization = SessionManager.authHeader,
                status = status,
                query  = query,
                page   = page
            )
            Log.d(tag, "  HTTP ${response.code()}")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d(tag, "  ✓ ${body.data.size} usuarios (total=${body.total})")
                    Result.success(body)
                } else {
                    Log.e(tag, "  ✗ body=null")
                    Result.failure(Exception("Respuesta vacía del servidor."))
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

    suspend fun toggleStatus(id: Int): Result<ToggleStatusResponse> {
        return try {
            Log.d(tag, "→ toggleStatus id=$id")
            val response = api.toggleUsuarioStatus(SessionManager.authHeader, id)
            Log.d(tag, "  HTTP ${response.code()}")
            if (response.isSuccessful) {
                val body = response.body()!!
                Log.d(tag, "  ✓ new_status=${body.newStatus}")
                Result.success(body)
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
        val msg = parseMensaje(raw)
        return when (code) {
            401  -> Exception(msg ?: "Sin autorización.")
            403  -> Exception(msg ?: "Sin permiso para esta acción.")
            404  -> Exception(msg ?: "Usuario no encontrado.")
            else -> Exception(msg ?: "Error del servidor ($code).")
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
