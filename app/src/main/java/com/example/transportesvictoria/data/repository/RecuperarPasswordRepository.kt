package com.pointguatemala.transportesvictoria.data.repository

import com.pointguatemala.transportesvictoria.data.model.ForgotPasswordRequest
import com.pointguatemala.transportesvictoria.data.model.ForgotPasswordResponse
import com.pointguatemala.transportesvictoria.data.model.ResetPasswordRequest
import com.pointguatemala.transportesvictoria.data.model.ResetPasswordResponse
import com.pointguatemala.transportesvictoria.data.model.VerifyOtpRequest
import com.pointguatemala.transportesvictoria.data.model.VerifyOtpResponse
import com.pointguatemala.transportesvictoria.data.network.RetrofitClient
import com.google.gson.JsonParser

class RecuperarPasswordRepository {

    private val api = RetrofitClient.apiService

    suspend fun forgotPassword(email: String): Result<ForgotPasswordResponse> = runCatching {
        val response = api.forgotPassword(ForgotPasswordRequest(email.trim()))
        if (response.isSuccessful) {
            response.body() ?: ForgotPasswordResponse("Código enviado a tu correo")
        } else {
            val msg = parseMensaje(response.errorBody()?.string()) ?: when (response.code()) {
                404  -> "No encontramos una cuenta con ese correo"
                422  -> "Correo inválido"
                429  -> "Demasiados intentos. Intenta más tarde."
                500  -> "Error interno del servidor"
                else -> "Error ${response.code()}"
            }
            throw Exception(msg)
        }
    }.mapError()

    suspend fun verifyOtp(email: String, otp: String): Result<VerifyOtpResponse> = runCatching {
        val response = api.verifyOtp(VerifyOtpRequest(email.trim(), otp))
        if (response.isSuccessful) {
            response.body() ?: VerifyOtpResponse("Código válido")
        } else {
            val msg = parseMensaje(response.errorBody()?.string()) ?: when (response.code()) {
                400  -> "Código incorrecto o expirado"
                422  -> "Código inválido"
                else -> "Error ${response.code()}"
            }
            throw Exception(msg)
        }
    }.mapError()

    suspend fun resetPassword(
        email: String,
        otp: String,
        nuevaPassword: String
    ): Result<ResetPasswordResponse> = runCatching {
        val response = api.resetPassword(
            ResetPasswordRequest(
                email                = email.trim(),
                otp                  = otp,
                password             = nuevaPassword,
                passwordConfirmation = nuevaPassword
            )
        )
        if (response.isSuccessful) {
            response.body() ?: ResetPasswordResponse("Contraseña actualizada")
        } else {
            val msg = parseMensaje(response.errorBody()?.string()) ?: when (response.code()) {
                400  -> "Código incorrecto o expirado"
                422  -> "Datos inválidos. Verifica el código e intenta de nuevo."
                else -> "Error ${response.code()}"
            }
            throw Exception(msg)
        }
    }.mapError()

    private fun parseMensaje(raw: String?): String? = try {
        raw?.let { JsonParser.parseString(it).asJsonObject.get("message")?.asString }
    } catch (_: Exception) { null }

    private fun <T> Result<T>.mapError(): Result<T> = this.recoverCatching { e ->
        val msg = when {
            e.message?.contains("Unable to resolve host") == true ||
            e.message?.contains("Failed to connect") == true ->
                "No se pudo conectar al servidor. Verifica tu conexión."
            else -> e.message ?: "Error desconocido"
        }
        throw Exception(msg)
    }
}
