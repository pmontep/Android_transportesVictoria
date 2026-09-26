package com.pointguatemala.transportesvictoria.data.repository

import android.util.Log
import com.pointguatemala.transportesvictoria.data.model.EmpresaItem
import com.pointguatemala.transportesvictoria.data.model.RegistroRequest
import com.pointguatemala.transportesvictoria.data.model.RegistroResponse
import com.pointguatemala.transportesvictoria.data.network.RetrofitClient
import com.google.gson.JsonParser

class RegistroRepository {

    private val tag = "RegistroRepository"
    private val apiService = RetrofitClient.apiService

    suspend fun getEmpresas(): Result<List<EmpresaItem>> {
        return try {
            val response = apiService.getEmpresas()
            if (response.isSuccessful) {
                Result.success(response.body()?.empresas ?: emptyList())
            } else {
                Log.e(tag, "Error empresas ${response.code()}")
                Result.failure(Exception("No se pudieron cargar las empresas."))
            }
        } catch (e: Exception) {
            Log.e(tag, "Excepción empresas: ${e.message}")
            Result.failure(Exception("Sin conexión al servidor."))
        }
    }

    suspend fun registro(request: RegistroRequest): Result<RegistroResponse> {
        return try {
            val response = apiService.registro(request)
            Log.d(tag, "HTTP ${response.code()}")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) Result.success(body)
                else Result.failure(Exception("Respuesta vacía del servidor."))
            } else {
                val rawError = response.errorBody()?.string() ?: ""
                Log.e(tag, "Error ${response.code()}: $rawError")
                val apiMsg = parsearMensaje(rawError)
                val errorMsg = apiMsg ?: when (response.code()) {
                    422  -> "Verifica los datos ingresados."
                    403  -> "La empresa seleccionada está inactiva."
                    500  -> "Error interno del servidor."
                    else -> "Error ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Excepción: ${e.message}")
            val msg = when {
                e.message?.contains("Unable to resolve host") == true ||
                e.message?.contains("Failed to connect") == true ->
                    "No se pudo conectar al servidor."
                else -> "Error al enviar el registro."
            }
            Result.failure(Exception(msg))
        }
    }

    private fun parsearMensaje(rawJson: String): String? {
        return try {
            JsonParser.parseString(rawJson).asJsonObject.get("message")?.asString
        } catch (_: Exception) { null }
    }
}
