package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

data class TerminosResponse(
    @SerializedName("acuerdo_uso") val acuerdoUso: String?
)
