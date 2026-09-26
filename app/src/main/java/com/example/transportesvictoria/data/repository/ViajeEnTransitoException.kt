package com.pointguatemala.transportesvictoria.data.repository

/**
 * Se lanza cuando el backend rechaza la creación de una solicitud
 * porque el usuario ya tiene un viaje activo / en tránsito.
 *
 * @param apiMessage Mensaje exacto devuelto por el servidor (se muestra en el diálogo).
 */
class ViajeEnTransitoException(
    apiMessage: String = "Ya tienes un viaje en tránsito."
) : Exception(apiMessage)
