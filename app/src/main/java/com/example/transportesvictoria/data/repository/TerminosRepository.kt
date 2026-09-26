package com.pointguatemala.transportesvictoria.data.repository

import android.util.Log
import com.pointguatemala.transportesvictoria.data.network.RetrofitClient

class TerminosRepository {

    private val tag = "TerminosRepository"
    private val apiService = RetrofitClient.apiService

    suspend fun getTerminos(): Result<String?> {
        return try {
            val response = apiService.getTerminos()
            if (response.isSuccessful) {
                Result.success(response.body()?.acuerdoUso)
            } else {
                Log.e(tag, "Error ${response.code()}")
                Result.failure(Exception("No se pudo cargar el contenido."))
            }
        } catch (e: Exception) {
            Log.e(tag, "Excepción: ${e.message}")
            Result.failure(Exception("Sin conexión al servidor."))
        }
    }
}
