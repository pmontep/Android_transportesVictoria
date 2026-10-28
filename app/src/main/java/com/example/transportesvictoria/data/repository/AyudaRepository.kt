package com.pointguatemala.transportesvictoria.data.repository

import android.util.Log
import com.pointguatemala.transportesvictoria.data.network.RetrofitClient

class AyudaRepository {

    private val tag = "AyudaRepository"
    private val apiService = RetrofitClient.apiService

    suspend fun getAyuda(): Result<String?> {
        return try {
            val response = apiService.getAyuda()
            if (response.isSuccessful) {
                Result.success(response.body()?.ayuda)
            } else {
                Log.w(tag, "HTTP ${response.code()} — tratado como sin contenido")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(tag, "Excepción: ${e.message}")
            Result.failure(Exception("No se pudo cargar la ayuda. ${e.message ?: ""}".trim()))
        }
    }
}
