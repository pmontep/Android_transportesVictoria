package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

data class SoporteResponse(
    @SerializedName("soporte_email")    val email:    String?,
    @SerializedName("soporte_telefono") val telefono: String?,
    @SerializedName("soporte_whatsapp") val whatsapp: String?
)
