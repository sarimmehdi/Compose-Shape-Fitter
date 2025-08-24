package com.sarim.example_app_data.dto.settings

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.IOException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class SettingsDtoSerializer(
    private val dataStoreName: String,
) : Serializer<SettingsDto> {
    override val defaultValue: SettingsDto
        get() = SettingsDto()

    override suspend fun readFrom(input: InputStream): SettingsDto {
        try {
            return Json.decodeFromString(
                SettingsDto.serializer(),
                input.readBytes().decodeToString(),
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException(
                "Unable to read SettingsDto from $dataStoreName",
                serialization,
            )
        }
    }

    override suspend fun writeTo(
        t: SettingsDto,
        output: OutputStream,
    ) {
        try {
            output.write(
                Json
                    .encodeToString(SettingsDto.serializer(), t)
                    .encodeToByteArray(),
            )
        } catch (e: IOException) {
            throw CorruptionException(
                "Unable to write SettingsDto to $dataStoreName",
                e,
            )
        }
    }

    companion object {
        enum class DataStoreType(
            val dataStoreName: String,
        ) {
            ACTUAL("SettingsDto.json"),
            TEST("SettingsDtoTest.json"),
            TEST_ERROR("SettingsDtoTestError.json"),
        }

        fun create(dataStoreName: String): SettingsDtoSerializer = SettingsDtoSerializer(dataStoreName)
    }
}
