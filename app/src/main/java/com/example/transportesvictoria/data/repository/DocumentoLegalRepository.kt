package com.pointguatemala.transportesvictoria.data.repository

import android.util.Log
import com.pointguatemala.transportesvictoria.data.model.DocumentoLegalType
import com.pointguatemala.transportesvictoria.data.network.RetrofitClient

class DocumentoLegalRepository {

    private val tag = "DocumentoLegalRepository"
    private val apiService = RetrofitClient.apiService

    suspend fun getDocumento(tipo: DocumentoLegalType): Result<String?> {
        return try {
            val response = apiService.getTerminos()
            if (response.isSuccessful) {
                val body = response.body()
                    ?: return Result.failure(Exception("No se pudo leer el documento."))
                Result.success(tipo.campo(body))
            } else {
                Log.e(tag, "HTTP ${response.code()}")
                Result.failure(Exception("No se pudo cargar el documento. (HTTP ${response.code()})"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Excepción: ${e.message}")
            Result.failure(Exception("No se pudo cargar el documento. ${e.message ?: ""}".trim()))
        }
    }
}
