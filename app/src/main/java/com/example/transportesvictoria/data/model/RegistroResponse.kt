package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

data class RegistroResponse(
    @SerializedName("message")    val message:   String,
    @SerializedName("error_code") val errorCode: String? = null
)
