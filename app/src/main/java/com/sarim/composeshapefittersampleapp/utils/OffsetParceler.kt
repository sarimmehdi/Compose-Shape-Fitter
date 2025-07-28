package com.sarim.composeshapefittersampleapp.utils

import android.os.Parcel
import androidx.compose.ui.geometry.Offset
import kotlinx.parcelize.Parceler

object OffsetParceler : Parceler<Offset> {
    override fun create(parcel: Parcel): Offset =
        Offset(
            x = parcel.readFloat(),
            y = parcel.readFloat(),
        )

    override fun Offset.write(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeFloat(x)
        parcel.writeFloat(y)
    }
}
