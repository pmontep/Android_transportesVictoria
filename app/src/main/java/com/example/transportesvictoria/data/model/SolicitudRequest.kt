package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

/**
 * Cuerpo del POST /api/viajes/solicitar
 */
data class SolicitudRequest(
    @SerializedName("ruta_solicitada") val rutaSolicitada: String,
    @SerializedName("horaSolicitud")   val horaSolicitud: String,
    @SerializedName("observaciones")   val observaciones: String? = null
)

/**
 * Cuerpo del PATCH /api/viajes/mis-solicitudes/{idL}/calificar
 */
data class CalificarRequest(
    @SerializedName("calificacion")             val calificacion: Int,
    @SerializedName("comentario_calificacion")  val comentarioCalificacion: String? = null
)
