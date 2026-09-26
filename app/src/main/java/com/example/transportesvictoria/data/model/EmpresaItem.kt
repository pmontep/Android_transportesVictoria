package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

data class EmpresaItem(
    @SerializedName("idL")          val idL:         String,
    @SerializedName("nombre_corto") val nombreCorto: String
)

data class EmpresasResponse(
    @SerializedName("empresas") val empresas: List<EmpresaItem>
)
