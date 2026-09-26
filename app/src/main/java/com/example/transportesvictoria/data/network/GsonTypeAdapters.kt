package com.pointguatemala.transportesvictoria.data.network

import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

// ── Adapter para Int que tolera String o Int ──────────────────────────────────
object FlexibleIntDeserializer : JsonDeserializer<Int> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: com.google.gson.JsonDeserializationContext?
    ): Int {
        return when {
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
}

// ── Adapter para Int nullable que tolera String o Int ───────────────────────
object FlexibleNullableIntDeserializer : JsonDeserializer<Int?> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: com.google.gson.JsonDeserializationContext?
    ): Int? {
        return when {
            json == null || json.isJsonNull -> null
            json.isJsonPrimitive -> {
                val prim = json.asJsonPrimitive
                when {
                    prim.isNumber -> prim.asInt
                    prim.isString -> prim.asString.toIntOrNull()
                    else -> null
                }
            }
            else -> null
        }
    }
}

// ── Adapter para String que tolera String o Int/Number ──────────────────────
object FlexibleStringDeserializer : JsonDeserializer<String> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: com.google.gson.JsonDeserializationContext?
    ): String {
        return when {
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
}

// ── Adapter para String nullable que tolera String o Int/Number ──────────────
object FlexibleNullableStringDeserializer : JsonDeserializer<String?> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: com.google.gson.JsonDeserializationContext?
    ): String? {
        return when {
            json == null || json.isJsonNull -> null
            json.isJsonPrimitive -> {
                val prim = json.asJsonPrimitive
                when {
                    prim.isString -> prim.asString
                    prim.isNumber -> prim.asNumber.toString()
                    else -> null
                }
            }
            else -> null
        }
    }
}
