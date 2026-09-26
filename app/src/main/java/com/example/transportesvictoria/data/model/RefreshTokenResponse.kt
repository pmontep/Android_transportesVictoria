package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(
    @SerializedName("token")      val token:     String?,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("issued_at")  val issuedAt:  String,
    @SerializedName("rotated")    val rotated:   Boolean
)
