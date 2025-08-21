package com.sarim.utils.parceler

import android.os.Parcel
import androidx.compose.ui.geometry.Offset
import kotlinx.parcelize.Parceler

object OffsetPairParceler : Parceler<Pair<Offset, Offset>> {
    private val offsetParceler = OffsetParceler

    override fun create(parcel: Parcel): Pair<Offset, Offset> {
        val first = offsetParceler.create(parcel)
        val second = offsetParceler.create(parcel)
        return Pair(first, second)
    }

    override fun Pair<Offset, Offset>.write(parcel: Parcel, flags: Int) {
        offsetParceler.apply { first.write(parcel, flags) }
        offsetParceler.apply { second.write(parcel, flags) }
    }
}
