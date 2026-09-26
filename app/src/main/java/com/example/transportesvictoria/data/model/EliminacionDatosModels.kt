package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

data class EliminacionDatosRequest(
    @SerializedName("motivo") val motivo: String?
)

data class EliminacionDatosSolicitud(
    @SerializedName("id")        val id:       Int?,
    @SerializedName("estado")    val estado:   String?,
    @SerializedName("creado_en") val creadoEn: String?
)

data class EliminacionDatosResponse(
    @SerializedName("message")   val message:   String,
    @SerializedName("solicitud") val solicitud: EliminacionDatosSolicitud?
)
