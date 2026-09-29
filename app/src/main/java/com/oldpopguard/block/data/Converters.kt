package com.oldpopguard.block.data

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        if (list == null) return "[]"
        return json.encodeToString(ListSerializer(String.serializer()), list)
    }

    @TypeConverter
    fun toStringList(str: String?): List<String> {
        if (str.isNullOrEmpty()) return emptyList()
        return try { json.decodeFromString(ListSerializer(String.serializer()), str) } catch (e: Exception) { emptyList() }
    }
}
