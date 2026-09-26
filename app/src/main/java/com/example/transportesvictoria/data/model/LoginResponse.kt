package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("user")       val user:      User,
    @SerializedName("token")      val token:     String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("issued_at")  val issuedAt:  String? = null
)

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("lastname")
    val lastname: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("tel")
    val tel: String?,                          // nullable — puede no venir
    @SerializedName("tipo_usuario")
    val tipoUsuario: String?,
    @SerializedName("documento")
    val documento: Documento?,                 // nullable — puede no venir para reclutadores
    @SerializedName("empresa")
    val empresa: Empresa?,                     // nullable — puede no venir para todos los roles
    @SerializedName("rol_principal")
    val rolPrincipal: String,
    @SerializedName("roles")
    val roles: Roles?,
    @SerializedName("direccion_principal")
    val direccionPrincipal: DireccionPrincipal?,  // nullable — puede no venir
    @SerializedName("direccion_secundaria")
    val direccionSecundaria: DireccionPrincipal? = null // nullable — solo aplica para rol "usuario"
)

data class Documento(
    @SerializedName("tipo")
    val tipo: String?,
    @SerializedName("valor")
    val valor: String?
)

data class Empresa(
    @SerializedName("id")
    val id: String,
    @SerializedName("nombre")
    val nombre: String?,
    @SerializedName("nombre_corto")
    val nombreCorto: String?
)

data class Roles(
    @SerializedName("isusuario")
    val isUsuario: Boolean,
    @SerializedName("isconductor")
    val isConductor: Boolean,
    @SerializedName("isreclutador")
    val isReclutador: Boolean
)

data class DireccionPrincipal(
    @SerializedName("direccion")
    val direccion: String?,
    @SerializedName("departamento")
    val departamento: String?,
    @SerializedName("ruta")
    val ruta: String?,
    @SerializedName("tipo_ruta")
    val tipoRuta: String?,
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lng")
    val lng: Double?
)

// Wrapper para GET /api/me — soporta { "user": {...} } y { "data": {...} }
data class MeResponse(
    @SerializedName("user") val user: User?,
    @SerializedName("data") val data: User?
) {
    val item: User? get() = user ?: data
}
