package com.sarim.example_app_data.dto.shape

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object ShapeDtoSerializer : Serializer<ShapeDto> {
    const val SHAPE_DTO_DATA_STORE_NAME = "ShapeDto.json"
    const val SHAPE_DTO_TEST_DATA_STORE_NAME = "ShapeDtoTest.json"

    override val defaultValue: ShapeDto
        get() = ShapeDto()

    override suspend fun readFrom(input: InputStream): ShapeDto {
        try {
            return Json.decodeFromString(
                ShapeDto.serializer(),
                input.readBytes().decodeToString(),
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read ShapeDto", serialization)
        }
    }

    override suspend fun writeTo(
        t: ShapeDto,
        output: OutputStream,
    ) {
        output.write(
            Json
                .encodeToString(ShapeDto.serializer(), t)
                .encodeToByteArray(),
        )
    }
}
