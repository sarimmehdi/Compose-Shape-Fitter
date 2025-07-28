package com.sarim.composeshapefittersampleapp.data.dto.settings

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object SettingsDtoSerializer : Serializer<SettingsDto> {
    const val SETTINGS_DTO_DATA_STORE_NAME = "SettingsDto.json"

    override val defaultValue: SettingsDto
        get() = SettingsDto()

    override suspend fun readFrom(input: InputStream): SettingsDto {
        try {
            return Json.decodeFromString(
                SettingsDto.serializer(),
                input.readBytes().decodeToString(),
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read SettingsDto", serialization)
        }
    }

    override suspend fun writeTo(
        t: SettingsDto,
        output: OutputStream,
    ) {
        output.write(
            Json
                .encodeToString(SettingsDto.serializer(), t)
                .encodeToByteArray(),
        )
    }
}
