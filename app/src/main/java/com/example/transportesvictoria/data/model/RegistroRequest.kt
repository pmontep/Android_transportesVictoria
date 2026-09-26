package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

data class RegistroRequest(
    @SerializedName("name")           val name:          String,
    @SerializedName("lastname")       val lastname:      String,
    @SerializedName("email")          val email:         String,
    @SerializedName("tipo_usuario")   val tipoUsuario:   String,
    @SerializedName("dpi")            val dpi:           String? = null,
    @SerializedName("carnet")         val carnet:        String? = null,
    @SerializedName("empresa_idL")    val empresaIdL:    String,
    @SerializedName("departamento")   val departamento:  String,
    @SerializedName("direccion")      val direccion:     String,
    @SerializedName("telefono")       val telefono:      String,
    @SerializedName("accepted_terms") val acceptedTerms: Boolean
)
