package com.pointguatemala.transportesvictoria.data.session

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

object SessionManager {

    private lateinit var prefs: SharedPreferences

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    // Emite el error_code cuando cualquier request recibe un 401 fatal
    private val _logoutEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val logoutEvent: SharedFlow<String> = _logoutEvent.asSharedFlow()

    // Un solo refresh puede estar en curso a la vez
    val refreshMutex = Mutex()

    fun init(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        prefs = EncryptedSharedPreferences.create(
            context,
            "tv_session",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        _isAuthenticated.value = isLoggedIn
    }

    val authHeader: String
        get() {
            val token     = prefs.getString("token", "") ?: ""
            val tokenType = prefs.getString("token_type", "Bearer") ?: "Bearer"
            return "$tokenType $token"
        }

    val isLoggedIn: Boolean
        get() = !prefs.getString("token", null).isNullOrBlank()

    val issuedAt: String?
        get() = prefs.getString("issued_at", null)

    fun saveToken(token: String, tokenType: String = "Bearer", issuedAt: String? = null) {
        prefs.edit()
            .putString("token", token)
            .putString("token_type", tokenType)
            .apply { if (issuedAt != null) putString("issued_at", issuedAt) }
            .apply()
        _isAuthenticated.value = true
    }

    /** Devuelve true si pasaron ≥72h desde el issued_at guardado. */
    fun shouldRefresh(): Boolean {
        val raw = issuedAt ?: return false
        return try {
            ChronoUnit.HOURS.between(OffsetDateTime.parse(raw), OffsetDateTime.now()) >= 72
        } catch (_: Exception) { false }
    }

    fun clear() {
        prefs.edit().clear().apply()
        _isAuthenticated.value = false
    }

    /** Llamado por el interceptor de OkHttp al detectar un 401 fatal. */
    fun triggerLogout(errorCode: String = "AUTH_UNAUTHORIZED") {
        clear()
        _logoutEvent.tryEmit(errorCode)
    }
}
