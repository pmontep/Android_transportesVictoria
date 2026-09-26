package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

data class ConductorViajesResponse(
    @SerializedName("data")
    val data: List<ConductorViaje>
)

data class ConductorViaje(
    @SerializedName("id")
    val id: String?,
    @SerializedName("nombreRuta")
    val nombreRuta: String?,
    @SerializedName("fechaViaje")
    val fechaViaje: String?,
    @SerializedName("horaViaje_inicio")
    val horaViajeInicio: String?,
    @SerializedName("estadoViaje")
    val estadoViaje: String?,
    @SerializedName("fechaHoraInicio")
    val fechaHoraInicio: String?,
    @SerializedName("fechaHoraFin")
    val fechaHoraFin: String?,
    @SerializedName("total_pasajeros")
    val totalPasajeros: Int?,
    @SerializedName("pasajeros")
    val pasajeros: List<PasajeroViaje>?
)

data class PasajeroViaje(
    @SerializedName("solicitud_id")
    val solicitudId: String?,
    @SerializedName("estado")
    val estado: String?,
    @SerializedName("observaciones")
    val observaciones: String?,
    @SerializedName("abordado")
    val abordado: Boolean?,
    @SerializedName("hora_abordaje")
    val horaAbordaje: String?,
    @SerializedName("usuario")
    val usuario: UsuarioPasajero?
)

data class UsuarioPasajero(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("lastname")
    val lastname: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("direccion1")
    val direccion1: String?,
    @SerializedName("departamento")
    val departamento: String?,
    @SerializedName("tipo_usuario")
    val tipoUsuario: String?,
    @SerializedName("dpi")
    val dpi: String?,
    @SerializedName("codigocarnet")
    val codigoCarnet: String?,
    @SerializedName("tel")
    val tel: String? = null,
    @SerializedName("lat")
    val lat: Double? = null,
    @SerializedName("lng")
    val lng: Double? = null
)

data class IniciarViajeResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("fechaHoraInicio")
    val fechaHoraInicio: String?
)

data class FinalizarViajeResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("fechaHoraFin")
    val fechaHoraFin: String?
)

data class AbordajeResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("abordado")
    val abordado: Boolean?,
    @SerializedName("hora_abordaje")
    val horaAbordaje: String?
)
