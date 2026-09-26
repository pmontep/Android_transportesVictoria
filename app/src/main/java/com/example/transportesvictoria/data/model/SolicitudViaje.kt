package com.pointguatemala.transportesvictoria.data.model

import com.google.gson.annotations.SerializedName

// ── Paginación anidada (campo "pagination" del JSON de Laravel) ────────────────
data class Pagination(
    @SerializedName("total")        val total:       Int,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("last_page")    val lastPage:    Int,
    @SerializedName("per_page")     val perPage:     Int
)

// ── Respuesta paginada genérica ────────────────────────────────────────────────
// El JSON real tiene la forma:
//   { "data": [...], "pagination": { "total": 3, "current_page": 1, ... } }
data class PaginatedResponse<T>(
    @SerializedName("data")       val data:       List<T>,
    @SerializedName("pagination") val pagination: Pagination
) {
    // Propiedades de conveniencia para no tener que cambiar el ViewModel
    val currentPage get() = pagination.currentPage
    val lastPage    get() = pagination.lastPage
    val total       get() = pagination.total
    val perPage     get() = pagination.perPage
}

// ── Wrapper del endpoint de detalle ───────────────────────────────────────────
// Soporta las dos formas que puede devolver el backend:
//   { "solicitud": { ... } }  ó  { "data": { ... } }
data class DetalleSolicitudResponse(
    @SerializedName("solicitud") val solicitud: SolicitudViaje?,
    @SerializedName("data")      val data:      SolicitudViaje?
) {
    // Devuelve el objeto sea cual sea la clave que usó el backend
    val item: SolicitudViaje? get() = solicitud ?: data
}

// ── Solicitud de viaje (ítem de la lista) ──────────────────────────────────────
// Mapeado 1-a-1 con el JSON real del endpoint /api/viajes/mis-solicitudes
data class SolicitudViaje(
    @SerializedName("id")
    val id: Int,

    @SerializedName("idL")
    val idL: String?,

    @SerializedName("idUsuario")
    val idUsuario: String?,

    @SerializedName("idViaje")
    val idViaje: Int?,

    @SerializedName("empresa_id")
    val empresaId: Int?,

    /** Nombre de la ruta tal como la ingresó el usuario, p.ej. "centro - zona 18" */
    @SerializedName("ruta_solicitada")
    val rutaSolicitada: String?,

    @SerializedName("fechaSolicitud")
    val fechaSolicitud: String?,

    @SerializedName("horaSolicitud")
    val horaSolicitud: String?,

    /** Estado de la solicitud, p.ej. "Activa" */
    @SerializedName("estadoSolicitud")
    val estadoSolicitud: String?,

    /** 0 = no abordado, 1 = abordado (puede llegar como "0"/"1" o 0/1) */
    @SerializedName("abordado")
    val abordado: String?,

    @SerializedName("hora_abordaje")
    val horaAbordaje: String?,

    @SerializedName("observaciones")
    val observaciones: String?,

    /** 1 = activo, 0 = inactivo */
    @SerializedName("is_active")
    val isActive: Int?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?,

    /** Estado del viaje asociado, p.ej. "Por programar", "Programado", "En curso", "Finalizado" */
    @SerializedName("estadoViaje")
    val estadoViaje: String?,

    @SerializedName("fechaHoraInicio")
    val fechaHoraInicio: String?,

    @SerializedName("fechaHoraFin")
    val fechaHoraFin: String?,

    @SerializedName("horaViaje_fin")
    val horaViajeFin: String?,

    /** Nombre completo del conductor asignado (null si aún no hay conductor) */
    @SerializedName("conductor_nombre")
    val conductorNombre: String?,

    /** Path relativo de la foto del conductor, ej: "profile-photos/user-5.jpg" */
    @SerializedName("profile_photo_path")
    val profilePhotoPath: String?,

    /** Calificación del viaje (1-5), null si no ha calificado aún */
    @SerializedName("calificacion")
    val calificacion: Int?,

    /** Comentario opcional de la calificación */
    @SerializedName("comentario_calificacion")
    val comentarioCalificacion: String?
)
