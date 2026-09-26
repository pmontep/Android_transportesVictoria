package com.pointguatemala.transportesvictoria.data.network

import android.util.Log
import com.pointguatemala.transportesvictoria.BuildConfig
import com.pointguatemala.transportesvictoria.data.session.SessionManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val TAG = "RetrofitClient"

    // La URL se define automáticamente según el buildType:
    //   debug   → http://10.0.2.2:8080/   (localhost del PC en el emulador)
    //   release → https://tu-dominio.com/ (servidor de producción)
    private val BASE_URL = BuildConfig.BASE_URL

    // ── OkHttpClient con logging en debug ─────────────────────────────────────
    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()

        // Interceptor global: garantiza que toda petición lleve Accept: application/json.
        // Sin este header, Laravel puede devolver HTML o un 400 en rutas sin body (DELETE, GET).
        builder.addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Accept", "application/json")
                .build()
            chain.proceed(request)
        })

        // Interceptor global de 401: si cualquier request autenticada falla con 401 y el
        // backend devuelve AUTH_TOKEN_EXPIRED_INACTIVITY o AUTH_UNAUTHORIZED, limpia la
        // sesión local y emite el evento de logout para que la UI redirija al login.
        builder.addInterceptor(Interceptor { chain ->
            val request  = chain.request()
            val response = chain.proceed(request)
            if (response.code == 401 && request.header("Authorization") != null) {
                try {
                    val raw  = response.peekBody(Long.MAX_VALUE).string()
                    val code = JsonParser.parseString(raw).asJsonObject.get("error_code")?.asString
                    if (code == "AUTH_TOKEN_EXPIRED_INACTIVITY" || code == "AUTH_UNAUTHORIZED") {
                        SessionManager.triggerLogout(code)
                    }
                } catch (_: Exception) { /* ignora errores de parseo */ }
            }
            response
        })

        // Solo en debug: loguea headers + body completo en Logcat (tag = "OkHttp")
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor { message ->
                Log.d("OkHttp", message)
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(logging)
        }

        builder.build()
    }

    private val gson: Gson by lazy {
        val intDeserializer = JsonDeserializer { json, _, _ ->
            when {
                json == null || json.isJsonNull -> 0
                json.isJsonPrimitive -> {
                    val prim = json.asJsonPrimitive
                    when {
                        prim.isNumber -> prim.asInt
                        prim.isString -> prim.asString.toIntOrNull() ?: 0
                        else -> 0
                    }
                }
                else -> 0
            }
        }

        val stringDeserializer = JsonDeserializer { json, _, _ ->
            when {
                json == null || json.isJsonNull -> ""
                json.isJsonPrimitive -> {
                    val prim = json.asJsonPrimitive
                    when {
                        prim.isString -> prim.asString
                        prim.isNumber -> prim.asNumber.toString()
                        else -> ""
                    }
                }
                else -> ""
            }
        }

        GsonBuilder()
            .registerTypeAdapter(Int::class.java, intDeserializer)
            .registerTypeAdapter(String::class.java, stringDeserializer)
            .create()
    }

    val apiService: ApiService by lazy {
        Log.d(TAG, "BASE_URL = $BASE_URL")
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}
