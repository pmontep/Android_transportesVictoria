package com.pointguatemala.transportesvictoria.data.network

import com.pointguatemala.transportesvictoria.data.model.AbordajeResponse
import com.pointguatemala.transportesvictoria.data.model.ConductorViajesResponse
import com.pointguatemala.transportesvictoria.data.model.ReclutadorUsuario
import com.pointguatemala.transportesvictoria.data.model.ToggleStatusResponse
import com.pointguatemala.transportesvictoria.data.model.DetalleSolicitudResponse
import com.pointguatemala.transportesvictoria.data.model.FinalizarViajeResponse
import com.pointguatemala.transportesvictoria.data.model.IniciarViajeResponse
import com.pointguatemala.transportesvictoria.data.model.ForgotPasswordRequest
import com.pointguatemala.transportesvictoria.data.model.ForgotPasswordResponse
import com.pointguatemala.transportesvictoria.data.model.LoginRequest
import com.pointguatemala.transportesvictoria.data.model.LoginResponse
import com.pointguatemala.transportesvictoria.data.model.ResetPasswordRequest
import com.pointguatemala.transportesvictoria.data.model.ResetPasswordResponse
import com.pointguatemala.transportesvictoria.data.model.VerifyOtpRequest
import com.pointguatemala.transportesvictoria.data.model.VerifyOtpResponse
import com.pointguatemala.transportesvictoria.data.model.MensajeResponse
import com.pointguatemala.transportesvictoria.data.model.RefreshTokenResponse
import com.pointguatemala.transportesvictoria.data.model.PaginatedResponse
import com.pointguatemala.transportesvictoria.data.model.RutasResponse
import com.pointguatemala.transportesvictoria.data.model.CalificarRequest
import com.pointguatemala.transportesvictoria.data.model.CambiarPasswordRequest
import com.pointguatemala.transportesvictoria.data.model.MeResponse
import com.pointguatemala.transportesvictoria.data.model.SolicitudRequest
import com.pointguatemala.transportesvictoria.data.model.SolicitudViaje
import com.pointguatemala.transportesvictoria.data.model.EmpresasResponse
import com.pointguatemala.transportesvictoria.data.model.TerminosResponse
import com.pointguatemala.transportesvictoria.data.model.RegistroRequest
import com.pointguatemala.transportesvictoria.data.model.RegistroResponse
import com.pointguatemala.transportesvictoria.data.model.SoporteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ── Autenticación ──────────────────────────────────────────────────────────
    @POST("api/loginapi")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/refresh-token")
    suspend fun refreshToken(
        @Header("Authorization") authorization: String
    ): Response<RefreshTokenResponse>

    @POST("api/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>

    @POST("api/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Response<VerifyOtpResponse>

    @POST("api/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ResetPasswordResponse>

    @POST("api/loginRegister")
    suspend fun registro(
        @Body request: RegistroRequest
    ): Response<RegistroResponse>

    @GET("api/empresas")
    suspend fun getEmpresas(): Response<EmpresasResponse>

    @GET("api/terminos")
    suspend fun getTerminos(): Response<TerminosResponse>

    // ── Viajes / Solicitudes ───────────────────────────────────────────────────

    /**
     * Lista paginada de solicitudes del usuario autenticado.
     * GET /api/viajes/mis-solicitudes?page=1&query=
     */
    @GET("api/viajes/mis-solicitudes")
    suspend fun getMisSolicitudes(
        @Header("Authorization") authorization: String,
        @Query("page")  page: Int    = 1,
        @Query("query") query: String = ""
    ): Response<PaginatedResponse<SolicitudViaje>>

    /**
     * Detalle de una solicitud específica.
     * GET /api/viajes/mis-solicitudes/{idL}
     */
    @GET("api/viajes/mis-solicitudes/{idL}")
    suspend fun getDetalleSolicitud(
        @Header("Authorization") authorization: String,
        @Path("idL") idL: String
    ): Response<DetalleSolicitudResponse>

    /**
     * Rutas asignadas al usuario con sus horarios disponibles.
     * GET /api/viajes/mis-rutas
     */
    @GET("api/viajes/mis-rutas")
    suspend fun getMisRutas(
        @Header("Authorization") authorization: String
    ): Response<RutasResponse>

    /**
     * Crea una nueva solicitud de viaje para el usuario autenticado.
     * POST /api/viajes/solicitar
     */
    @POST("api/viajes/solicitar")
    suspend fun crearSolicitud(
        @Header("Authorization") authorization: String,
        @Body request: SolicitudRequest
    ): Response<MensajeResponse>

    /**
     * Información de contacto de soporte de la empresa.
     * GET /api/soporte
     */
    @GET("api/soporte")
    suspend fun getSoporte(): Response<SoporteResponse>

    /**
     * Califica un viaje completado.
     * PATCH /api/viajes/mis-solicitudes/{idL}/calificar
     */
    @PATCH("api/viajes/mis-solicitudes/{idL}/calificar")
    suspend fun calificarViaje(
        @Header("Authorization") authorization: String,
        @Path("idL") idL: String,
        @Body request: CalificarRequest
    ): Response<MensajeResponse>

    /**
     * Cancela (elimina) una solicitud de viaje del usuario autenticado.
     * DELETE /api/viajes/mis-solicitudes/{idL}
     */
    @DELETE("api/viajes/mis-solicitudes/{idL}")
    suspend fun cancelarSolicitud(
        @Header("Authorization") authorization: String,
        @Path("idL") idL: String
    ): Response<MensajeResponse>

    // ── Perfil ─────────────────────────────────────────────────────────────────

    /**
     * Datos actualizados del usuario autenticado.
     * GET /api/me
     */
    @GET("api/me")
    suspend fun getMe(
        @Header("Authorization") authorization: String
    ): Response<MeResponse>

    /**
     * Cierra la sesión en el servidor (invalida el token).
     * POST /api/logoutapi
     */
    @POST("api/logoutapi")
    suspend fun logout(
        @Header("Authorization") authorization: String
    ): Response<MensajeResponse>

    /**
     * Cambia la contraseña del usuario autenticado.
     * POST /api/perfil/password
     */
    @POST("api/perfil/password")
    suspend fun cambiarPassword(
        @Header("Authorization") authorization: String,
        @Body request: CambiarPasswordRequest
    ): Response<MensajeResponse>

    // ── Conductor ──────────────────────────────────────────────────────────────

    @GET("api/conductor/viajes")
    suspend fun getConductorViajes(
        @Header("Authorization") authorization: String,
        @Query("fecha") fecha: String? = null
    ): Response<ConductorViajesResponse>

    @PATCH("api/conductor/viajes/{idL}/iniciar")
    suspend fun iniciarViaje(
        @Header("Authorization") authorization: String,
        @Path("idL") idL: String
    ): Response<IniciarViajeResponse>

    @PATCH("api/conductor/viajes/{idL}/finalizar")
    suspend fun finalizarViaje(
        @Header("Authorization") authorization: String,
        @Path("idL") idL: String
    ): Response<FinalizarViajeResponse>

    @PATCH("api/conductor/solicitud/{idL}/abordar")
    suspend fun abordarPasajero(
        @Header("Authorization") authorization: String,
        @Path("idL") idL: String
    ): Response<AbordajeResponse>

    // ── Reclutador ─────────────────────────────────────────────────────────────

    @GET("api/reclutador/usuarios")
    suspend fun getReclutadorUsuarios(
        @Header("Authorization") authorization: String,
        @Query("status") status: String = "0",
        @Query("query")  query: String  = "",
        @Query("page")   page: Int      = 1
    ): Response<PaginatedResponse<ReclutadorUsuario>>

    @PATCH("api/reclutador/usuarios/{id}/toggle-status")
    suspend fun toggleUsuarioStatus(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int
    ): Response<ToggleStatusResponse>
}
