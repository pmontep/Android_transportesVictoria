package com.pointguatemala.transportesvictoria.data.repository

import android.util.Log
import com.pointguatemala.transportesvictoria.data.model.SoporteResponse
import com.pointguatemala.transportesvictoria.data.network.RetrofitClient

class SoporteRepository {

    private val tag = "SoporteRepository"
    private val apiService = RetrofitClient.apiService

    suspend fun getSoporte(): Result<SoporteResponse> {
        return try {
            val response = apiService.getSoporte()
            Log.d(tag, "HTTP ${response.code()} ${response.message()}")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) Result.success(body)
                else Result.failure(Exception("Respuesta vacía del servidor."))
            } else {
                val rawError = response.errorBody()?.string() ?: ""
                Log.e(tag, "Error HTTP ${response.code()}: $rawError")
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Excepción: ${e.message}")
            val msg = when {
                e.message?.contains("Unable to resolve host") == true ||
                e.message?.contains("Failed to connect") == true ->
                    "No se pudo conectar al servidor. Verifica tu conexión."
                else -> "Error al cargar información de soporte."
            }
            Result.failure(Exception(msg))
        }
    }
}
