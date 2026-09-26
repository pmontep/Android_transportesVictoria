package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

data class CambiarPasswordRequest(
    @SerializedName("current_password")       val currentPassword: String,
    @SerializedName("password")               val password: String,
    @SerializedName("password_confirmation")  val passwordConfirmation: String
)
