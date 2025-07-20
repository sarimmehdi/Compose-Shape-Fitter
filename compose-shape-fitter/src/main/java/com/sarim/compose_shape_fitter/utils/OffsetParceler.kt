package com.sarim.compose_shape_fitter.utils

import android.os.Parcel
import androidx.compose.ui.geometry.Offset
import kotlinx.parcelize.Parceler

internal object OffsetParceler : Parceler<Offset> {
    override fun create(parcel: Parcel): Offset {
        return Offset(
            x = parcel.readFloat(),
            y = parcel.readFloat()
        )
    }

    override fun Offset.write(parcel: Parcel, flags: Int) {
        parcel.writeFloat(x)
        parcel.writeFloat(y)
    }
}