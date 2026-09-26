package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

data class ReclutadorUsuario(
    @SerializedName("id")           val id: Int,
    @SerializedName("name")         val name: String?,
    @SerializedName("lastname")     val lastname: String?,
    @SerializedName("email")        val email: String?,
    @SerializedName("tel")          val tel: String?,
    @SerializedName("departamento") val departamento: String?,
    @SerializedName("direccion1")   val direccion1: String?,
    @SerializedName("ruta")         val ruta: String?,
    @SerializedName("ruta2")        val ruta2: String?,
    @SerializedName("tipo_usuario") val tipoUsuario: String?,
    @SerializedName("dpi")          val dpi: String?,
    @SerializedName("codigocarnet") val codigoCarnet: String?,
    @SerializedName("status")       val status: String?,
    @SerializedName("empresa_id")   val empresaId: Int?,
    @SerializedName("created_at")   val createdAt: String?
)

data class ToggleStatusResponse(
    @SerializedName("success")    val success: Boolean,
    @SerializedName("message")    val message: String?,
    @SerializedName("new_status") val newStatus: String?
)
