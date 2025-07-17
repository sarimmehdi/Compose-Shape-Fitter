package com.sarim.compose_shape_fitter

import androidx.compose.ui.geometry.Offset

// Object to manage JNI calls
internal object EllipseFitterJNI {
    init {
        // Load the native library you will create (e.g., "native-lib")
        // The name should match what you define in your CMakeLists.txt or ndk-build Android.mk
        try {
            System.loadLibrary("composeshapefittersampleapp_native") // Common name, adjust if needed
        } catch (e: UnsatisfiedLinkError) {
            // Log this error, critical for debugging JNI
            println("Failed to load native library 'native-lib': ${e.message}")
            // Consider re-throwing or handling appropriately if the app cannot function without it
        }
    }

    /**
     * Native method to perform ellipse fitting.
     *
     * @param pointsX FloatArray of X coordinates.
     * @param pointsY FloatArray of Y coordinates.
     * @return FloatArray containing [centerX, centerY, radiusX, radiusY, angleRad],
     *         or null/empty array if fitting fails or an error occurs.
     */
    external fun fitEllipseNative(pointsX: FloatArray, pointsY: FloatArray): FloatArray?
}

internal data class RotatedEllipse(
    val center: Offset,
    val radiusX: Float, // Semi-major axis (or just one radius if using matrix form)
    val radiusY: Float, // Semi-minor axis
    val angleRad: Float // Rotation angle in radians
)

internal fun findSmallestEnclosingSkewedEllipse(points: List<Offset>): RotatedEllipse? {
    if (points.isEmpty()) {
        println("Point list is empty, cannot fit ellipse.")
        return null
    }

    val pointsX = FloatArray(points.size) { points[it].x }
    val pointsY = FloatArray(points.size) { points[it].y }

    try {
        val ellipseParamsArray: FloatArray? = EllipseFitterJNI.fitEllipseNative(pointsX, pointsY)

        if (ellipseParamsArray != null && ellipseParamsArray.size == 5) {
            val centerX = ellipseParamsArray[0]
            val centerY = ellipseParamsArray[1]
            val radiusX = ellipseParamsArray[2]
            val radiusY = ellipseParamsArray[3]
            val angleRad = ellipseParamsArray[4]

            // Basic validation
            if (radiusX <= 0f || radiusY <= 0f) {
                println("Native method returned invalid ellipse radii: rX=$radiusX, rY=$radiusY")
                return null
            }

            return RotatedEllipse(
                center = Offset(centerX, centerY),
                radiusX = radiusX,
                radiusY = radiusY,
                angleRad = angleRad
            )
        } else {
            println("Native method 'fitEllipseNative' returned null or an array of unexpected size: ${ellipseParamsArray?.size ?: "null"}. Expected 5.")
            return null
        }
    } catch (e: UnsatisfiedLinkError) {
        println("JNI UnsatisfiedLinkError in findEllipseUsingJNI: ${e.message}")
        e.printStackTrace()
        return null
    } catch (e: Exception) {
        println("Exception during JNI ellipse fitting: ${e.message}")
        e.printStackTrace()
        return null
    }
}
