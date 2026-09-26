package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

/** Respuesta genérica con solo un mensaje, p.ej. { "message": "Solicitud creada exitosamente" } */
data class MensajeResponse(
    @SerializedName("message") val message: String
)
