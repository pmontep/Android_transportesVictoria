package com.pointguatemala.transportesvictoria.data.repository

import android.util.Log
import com.pointguatemala.transportesvictoria.data.model.CalificarRequest
import com.pointguatemala.transportesvictoria.data.model.DetalleSolicitudResponse
import com.pointguatemala.transportesvictoria.data.model.PaginatedResponse
import com.pointguatemala.transportesvictoria.data.model.Ruta
import com.pointguatemala.transportesvictoria.data.model.SolicitudRequest
import com.pointguatemala.transportesvictoria.data.model.SolicitudViaje
import com.pointguatemala.transportesvictoria.data.network.RetrofitClient
import com.pointguatemala.transportesvictoria.data.session.SessionManager
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException

class ViajeRepository {

    private val tag = "ViajeRepository"
    private val apiService = RetrofitClient.apiService

    /**
     * Lista paginada de solicitudes del usuario autenticado.
     * GET /api/viajes/mis-solicitudes?page={page}&query={query}
     */
    suspend fun getMisSolicitudes(
        page: Int = 1,
        query: String = ""
    ): Result<PaginatedResponse<SolicitudViaje>> {
        return try {
            Log.d(tag, "→ getMisSolicitudes page=$page query='$query'")
            Log.d(tag, "  authHeader = '${SessionManager.authHeader}'")

            val response = apiService.getMisSolicitudes(
                authorization = SessionManager.authHeader,
                page          = page,
                query         = query
            )

            Log.d(tag, "  HTTP ${response.code()} ${response.message()}")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d(tag, "  ✓ ${body.data.size} solicitudes (total=${body.total})")
                    Result.success(body)
                } else {
                    // 200 pero body nulo → el JSON no coincide con el modelo
                    val rawBody = response.errorBody()?.string() ?: "(sin cuerpo)"
                    Log.e(tag, "  ✗ body=null. rawError='$rawBody'")
                    Log.e(tag, "  Posible causa: la estructura JSON no coincide con PaginatedResponse<SolicitudViaje>")
                    Result.failure(Exception("Respuesta vacía — revisa Logcat (tag OkHttp) para ver el JSON real"))
                }
            } else {
                val rawError = response.errorBody()?.string() ?: "(sin cuerpo)"
                Log.e(tag, "  ✗ Error HTTP ${response.code()}: $rawError")
                val errorMsg = when (response.code()) {
                    401  -> "Sin autorización (401). Token: '${SessionManager.authHeader.take(30)}…'"
                    403  -> "Sin permiso para ver estas solicitudes (403)."
                    404  -> "Endpoint no encontrado (404). Verifica la URL."
                    422  -> "Datos inválidos (422): $rawError"
                    500  -> "Error interno del servidor (500)."
                    else -> "Error ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: JsonSyntaxException) {
            Log.e(tag, "  ✗ JsonSyntaxException: ${e.message}")
            Result.failure(Exception("No se pudieron cargar tus solicitudes. Desliza para reintentar o vuelve a entrar."))
        } catch (e: Exception) {
            Log.e(tag, "  ✗ Excepción: ${e::class.simpleName} — ${e.message}")
            val msg = when {
                e.message?.contains("Unable to resolve host") == true ||
                e.message?.contains("Failed to connect") == true ->
                    "No se pudo conectar al servidor. Verifica tu conexión."
                else -> "Error: ${e.message ?: "desconocido"}"
            }
            Result.failure(Exception(msg))
        }
    }

    /**
     * Detalle de una solicitud.
     * GET /api/viajes/mis-solicitudes/{id}
     */
    suspend fun getDetalleSolicitud(idL: String): Result<SolicitudViaje> {
        return try {
            Log.d(tag, "→ getDetalleSolicitud idL='$idL'")
            Log.d(tag, "  authHeader = '${SessionManager.authHeader.take(30)}…'")

            val response = apiService.getDetalleSolicitud(
                authorization = SessionManager.authHeader,
                idL           = idL
            )

            Log.d(tag, "  HTTP ${response.code()} ${response.message()}")

            if (response.isSuccessful) {
                val body = response.body()
                val solicitud = body?.item
                if (solicitud != null) {
                    Log.d(tag, "  ✓ solicitud id=${solicitud.id} ruta=${solicitud.rutaSolicitada} estado=${solicitud.estadoSolicitud} estadoViaje=${solicitud.estadoViaje}")
                    Result.success(solicitud)
                } else {
                    // body nulo o clave wrapper no reconocida — loguear qué vino
                    Log.e(tag, "  ✗ body.item=null (body=$body). Claves reconocidas: 'solicitud', 'data'. Revisa Logcat tag OkHttp para ver el JSON real.")
                    Result.failure(Exception("El servidor devolvió un formato inesperado. Revisa Logcat (tag OkHttp)."))
                }
            } else {
                val rawError = response.errorBody()?.string() ?: ""
                Log.e(tag, "  ✗ Error HTTP ${response.code()}: $rawError")
                val apiMsg  = parsearMensajeApi(rawError)
                val errorMsg = when (response.code()) {
                    401  -> apiMsg ?: "Sin autorización (401)."
                    403  -> apiMsg ?: "Sin permiso para ver esta solicitud (403)."
                    404  -> apiMsg ?: "Solicitud '$idL' no encontrada (404)."
                    500  -> apiMsg ?: "Error interno del servidor (500)."
                    else -> apiMsg ?: "Error ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "  ✗ Excepción: ${e::class.simpleName} — ${e.message}")
            val msg = when {
                e.message?.contains("Unable to resolve host") == true ||
                e.message?.contains("Failed to connect") == true ->
                    "No se pudo conectar al servidor."
                e is JsonSyntaxException ->
                    "Error al procesar la respuesta del servidor. Revisa Logcat (tag OkHttp)."
                else -> "Error: ${e.message ?: "desconocido"}"
            }
            Result.failure(Exception(msg))
        }
    }

    /**
     * Rutas asignadas al usuario con sus horarios disponibles.
     * GET /api/viajes/mis-rutas
     */
    suspend fun getMisRutas(): Result<List<Ruta>> {
        return try {
            Log.d(tag, "→ getMisRutas")
            val response = apiService.getMisRutas(authorization = SessionManager.authHeader)
            Log.d(tag, "  HTTP ${response.code()} ${response.message()}")

            if (response.isSuccessful) {
                val rutas = response.body()?.rutas ?: emptyList()
                Log.d(tag, "  ✓ ${rutas.size} rutas")
                Result.success(rutas)
            } else {
                val errorMsg = when (response.code()) {
                    401  -> "Sin autorización (401)."
                    else -> "Error ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "  ✗ ${e::class.simpleName} — ${e.message}")
            Result.failure(Exception("No se pudo conectar al servidor."))
        }
    }

    /**
     * Crea una nueva solicitud de viaje.
     * POST /api/viajes/solicitar
     * @return mensaje de éxito o error
     */
    suspend fun crearSolicitud(request: SolicitudRequest): Result<String> {
        return try {
            Log.d(tag, "→ crearSolicitud ruta='${request.rutaSolicitada}'")

            val response = apiService.crearSolicitud(
                authorization = SessionManager.authHeader,
                request       = request
            )

            Log.d(tag, "  HTTP ${response.code()} ${response.message()}")

            if (response.isSuccessful) {
                val mensaje = response.body()?.message ?: "Solicitud creada exitosamente"
                Log.d(tag, "  ✓ $mensaje")
                Result.success(mensaje)
            } else {
                val rawError = response.errorBody()?.string() ?: "(sin cuerpo)"
                Log.e(tag, "  ✗ Error HTTP ${response.code()}: $rawError")

                // Detectar viaje en tránsito: 409 Conflict o mensaje del servidor
                if (response.code() == 409 || esViajeEnTransito(rawError)) {
                    val apiMsg = parsearMensajeApi(rawError)
                        ?: "Ya tienes un viaje en tránsito. Espera a que finalice para solicitar uno nuevo."
                    Log.w(tag, "  ⚠ Viaje en tránsito: $apiMsg")
                    return Result.failure(ViajeEnTransitoException(apiMsg))
                }

                val errorMsg = when (response.code()) {
                    401  -> "Sin autorización (401). Por favor inicia sesión de nuevo."
                    422  -> parsearErrores422(rawError)
                    500  -> "Error interno del servidor (500)."
                    else -> parsearMensajeApi(rawError) ?: "Error ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "  ✗ Excepción: ${e::class.simpleName} — ${e.message}")
            val msg = when {
                e.message?.contains("Unable to resolve host") == true ||
                e.message?.contains("Failed to connect") == true ->
                    "No se pudo conectar al servidor. Verifica tu conexión."
                else -> "Error: ${e.message ?: "desconocido"}"
            }
            Result.failure(Exception(msg))
        }
    }

    /**
     * Califica un viaje completado.
     * PATCH /api/viajes/mis-solicitudes/{idL}/calificar
     */
    suspend fun calificarViaje(
        idL: String,
        calificacion: Int,
        comentario: String?
    ): Result<String> {
        return try {
            Log.d(tag, "→ calificarViaje idL='$idL' calificacion=$calificacion")
            val response = apiService.calificarViaje(
                authorization = SessionManager.authHeader,
                idL           = idL,
                request       = CalificarRequest(
                    calificacion            = calificacion,
                    comentarioCalificacion  = comentario?.takeIf { it.isNotBlank() }
                )
            )
            Log.d(tag, "  HTTP ${response.code()} ${response.message()}")
            if (response.isSuccessful) {
                val mensaje = response.body()?.message ?: "Calificación enviada"
                Log.d(tag, "  ✓ $mensaje")
                Result.success(mensaje)
            } else {
                val rawError = response.errorBody()?.string() ?: ""
                Log.e(tag, "  ✗ Error HTTP ${response.code()}: $rawError")
                val errorMsg = when (response.code()) {
                    401  -> "Sin autorización (401)."
                    403  -> "No tienes permiso para calificar este viaje."
                    404  -> "Solicitud no encontrada."
                    422  -> parsearMensajeApi(rawError) ?: "Datos inválidos."
                    else -> parsearMensajeApi(rawError) ?: "Error ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "  ✗ ${e::class.simpleName} — ${e.message}")
            Result.failure(Exception("No se pudo conectar al servidor."))
        }
    }

    /**
     * Cancela una solicitud de viaje del usuario.
     * DELETE /api/viajes/mis-solicitudes/{id}
     */
    suspend fun cancelarSolicitud(idL: String): Result<String> {
        return try {
            Log.d(tag, "→ cancelarSolicitud idL='$idL'")

            val response = apiService.cancelarSolicitud(
                authorization = SessionManager.authHeader,
                idL           = idL
            )

            Log.d(tag, "  HTTP ${response.code()} ${response.message()}")

            if (response.isSuccessful) {
                val mensaje = response.body()?.message ?: "Solicitud cancelada"
                Log.d(tag, "  ✓ $mensaje")
                Result.success(mensaje)
            } else {
                val rawError = response.errorBody()?.string() ?: ""
                Log.e(tag, "  ✗ Error HTTP ${response.code()}: $rawError")
                val errorMsg = when (response.code()) {
                    401  -> "Sin autorización (401)."
                    403  -> "No tienes permiso para cancelar esta solicitud."
                    404  -> "Solicitud no encontrada."
                    409  -> "No se puede cancelar: el viaje ya está en curso o finalizado."
                    else -> "Error ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "  ✗ ${e::class.simpleName} — ${e.message}")
            Result.failure(Exception("No se pudo conectar al servidor."))
        }
    }

    /** Extrae el primer mensaje de validación de una respuesta 422 de Laravel usando Gson. */
    private fun parsearErrores422(rawJson: String): String {
        return try {
            val obj = JsonParser.parseString(rawJson).asJsonObject
            // Laravel devuelve { "errors": { "campo": ["mensaje", ...] } }
            val errors = obj.getAsJsonObject("errors")
            errors?.entrySet()?.firstOrNull()
                ?.value?.asJsonArray?.get(0)?.asString
                ?: obj.get("message")?.asString
                ?: "Datos inválidos. Revisa los campos del formulario."
        } catch (_: Exception) {
            "Datos inválidos. Revisa los campos del formulario."
        }
    }

    /**
     * Detecta si el cuerpo del error indica que el usuario ya tiene un viaje activo.
     * Cubre distintas formas en que el backend puede expresarlo.
     */
    private fun esViajeEnTransito(rawJson: String): Boolean {
        val lower = rawJson.lowercase()
        return lower.contains("tránsito")   ||
               lower.contains("transito")   ||
               lower.contains("en curso")   ||
               lower.contains("viaje activo") ||
               lower.contains("ya tienes")
    }

    /**
     * Extrae el campo "message" del JSON usando Gson para que los escapes
     * unicode (í, etc.) se decodifiquen correctamente a caracteres reales.
     */
    private fun parsearMensajeApi(rawJson: String): String? {
        return try {
            JsonParser.parseString(rawJson)
                .asJsonObject
                .get("message")
                ?.asString
        } catch (_: Exception) { null }
    }
}
