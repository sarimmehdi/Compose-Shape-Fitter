package com.sarim.example_app_data.dto.settings

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object SettingsDtoSerializer : Serializer<SettingsDto> {
    const val SETTINGS_DTO_DATA_STORE_NAME = "SettingsDto.json"
    const val SETTINGS_DTO_TEST_DATA_STORE_NAME = "SettingsDtoTest.json"

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
