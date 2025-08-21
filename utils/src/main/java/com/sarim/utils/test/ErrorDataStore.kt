package com.sarim.utils.test

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class ErrorDataStore<T>(
    private val dataStoreName: String
) : DataStore<T> {

    override val data: Flow<T>
        get() = flow {
            throw IOException(
                "Unable to read from $dataStoreName"
            )
        }

    override suspend fun updateData(transform: suspend (t: T) -> T): T {
        throw IOException(
            "Unable to write to $dataStoreName"
        )
    }
}