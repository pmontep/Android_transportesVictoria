package com.pointguatemala.transportesvictoria.ui.dashboard

import androidx.compose.ui.graphics.Color

/**
 * Devuelve el color asociado al estado de la solicitud o del viaje.
 * Cubre los valores reales que devuelve la API.
 */
fun colorEstado(estado: String?): Color = when (estado?.lowercase()?.trim()) {
    // ── Estado solicitud ───────────────────────────────────────────────────────
    "activa", "confirmado", "aprobado", "aceptado" -> Color(0xFF66BB6A)   // Verde
    "pendiente"                                    -> Color(0xFFFFA726)   // Naranja
    "cancelado", "rechazado", "inactiva"           -> Color(0xFFEF5350)   // Rojo

    // ── Estado viaje ───────────────────────────────────────────────────────────
    "programado"                                   -> Color(0xFF42A5F5)   // Azul
    "por programar"                                -> Color(0xFFFFA726)   // Naranja
    "en curso", "en camino", "en_camino", "activo" -> Color(0xFF26C6DA)   // Cian
    "completado", "finalizado"                     -> Color(0xFF78909C)   // Gris

    else                                           -> Color(0xFF9E9E9E)   // Gris neutro
}

/** Capitaliza la primera letra del estado o devuelve "Sin estado". */
fun etiquetaEstado(estado: String?): String =
    estado?.replaceFirstChar { it.uppercase() } ?: "Sin estado"
