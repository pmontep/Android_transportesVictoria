package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String
)

data class ForgotPasswordResponse(
    @SerializedName("message") val message: String
)

data class VerifyOtpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("code")  val otp: String
)

data class VerifyOtpResponse(
    @SerializedName("message") val message: String,
    @SerializedName("valid")   val valid: Boolean = true
)

data class ResetPasswordRequest(
    @SerializedName("email")                 val email: String,
    @SerializedName("code")                  val otp: String,
    @SerializedName("password")              val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String
)

data class ResetPasswordResponse(
    @SerializedName("message") val message: String
)
