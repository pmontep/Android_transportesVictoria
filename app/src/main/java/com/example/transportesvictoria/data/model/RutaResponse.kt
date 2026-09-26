package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

/** Respuesta del GET /api/viajes/mis-rutas */
data class RutasResponse(
    @SerializedName("rutas") val rutas: List<Ruta>
)

data class Ruta(
    @SerializedName("nombre")       val nombre: String,
    @SerializedName("tipo_ruta")    val tipoRuta: String?,
    @SerializedName("direccion")    val direccion: String?,
    @SerializedName("departamento") val departamento: String?,
    @SerializedName("lat")          val lat: Double?,
    @SerializedName("lng")          val lng: Double?,
    @SerializedName("es_principal") val esPrincipal: Boolean,
    @SerializedName("horarios")     val horarios: List<String>
)
