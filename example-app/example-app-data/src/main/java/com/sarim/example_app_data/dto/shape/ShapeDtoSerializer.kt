package com.sarim.example_app_data.dto.shape

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.IOException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class ShapeDtoSerializer(val dataStoreName: String) : Serializer<ShapeDto> {
    override val defaultValue: ShapeDto
        get() = ShapeDto()

    override suspend fun readFrom(input: InputStream): ShapeDto {
        try {
            return Json.decodeFromString(
                ShapeDto.serializer(),
                input.readBytes().decodeToString(),
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException(
                "Unable to read ShapeDto from $dataStoreName",
                serialization
            )
        }
    }

    override suspend fun writeTo(
        t: ShapeDto,
        output: OutputStream,
    ) {
        try {
            output.write(
                Json
                    .encodeToString(ShapeDto.serializer(), t)
                    .encodeToByteArray(),
            )
        } catch (e: IOException) {
            throw CorruptionException(
                "Unable to write ShapeDto to $dataStoreName",
                e
            )
        }
    }

    companion object {
        enum class DataStoreType(val dataStoreName: String) {
            ACTUAL("ShapeDto.json"),
            TEST("ShapeDtoTest.json"),
            TEST_ERROR("ShapeDtoTestError.json")
        }

        fun create(dataStoreName: String): ShapeDtoSerializer {
            return ShapeDtoSerializer(dataStoreName)
        }
    }
}
