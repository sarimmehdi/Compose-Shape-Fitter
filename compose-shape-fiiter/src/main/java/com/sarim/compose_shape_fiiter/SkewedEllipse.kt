package com.sarim.compose_shape_fiiter

import androidx.compose.ui.geometry.Offset

internal object DrawingScreenNatives {
    init {
        try {
            System.loadLibrary("native-lib") // Name from CMakeLists.txt
        } catch (e: UnsatisfiedLinkError) {
            // Log this error, critical for debugging JNI
            println("Failed to load native library 'native-lib': ${e.message}")
            // Consider re-throwing or handling appropriately if the app cannot function without it
        }
    }

    /**
     * Processes a list of points and returns ellipse parameters.
     *
     * Input: FloatArray [x0, y0, x1, y1, ...]
     * Output: FloatArray [centerX, centerY, radiusX, radiusY, angleRad]
     *         or an empty array/null if an error occurs or no ellipse is found.
     */
    external fun processPoints(inputPoints: FloatArray): FloatArray
}

internal data class RotatedEllipse(
    val center: Offset,
    val radiusX: Float, // Semi-major axis (or just one radius if using matrix form)
    val radiusY: Float, // Semi-minor axis
    val angleRad: Float // Rotation angle in radians
)

internal fun findSmallestEnclosingSkewedEllipse(points: List<Offset>): RotatedEllipse? {
    if (points.isEmpty()) {
        // Or handle as an error, or return a default ellipse, depending on requirements
        return null
    }

    // 1. Convert List<Offset> to a flat FloatArray for JNI
    // The C++ function expects a flat array: [x0, y0, x1, y1, ...]
    val flatPoints = FloatArray(points.size * 2)
    points.forEachIndexed { index, offset ->
        flatPoints[index * 2] = offset.x
        flatPoints[index * 2 + 1] = offset.y
    }

    try {
        // 2. Call the native method via the Kotlin external function in DrawingScreenNatives
        // The native method is expected to return the ellipse parameters as a FloatArray:
        // [centerX, centerY, radiusX, radiusY, angleRad]
        val ellipseParams: FloatArray = DrawingScreenNatives.processPoints(flatPoints)

        // 3. Interpret the result from C++
        // Ensure the C++ side returns exactly 5 floats in the expected order.
        if (ellipseParams.size == 5) {
            val centerX = ellipseParams[0]
            val centerY = ellipseParams[1]
            val radiusX = ellipseParams[2]
            val radiusY = ellipseParams[3]
            val angleRad = ellipseParams[4]

            // Basic validation (you might want more robust checks)
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
            // Handle error: Native method returned an unexpected number of parameters
            println("Native method 'processPoints' returned an array of unexpected size: ${ellipseParams.size}. Expected 5.")
            // You might want to log the contents of ellipseParams for debugging
            return null
        }
    } catch (e: UnsatisfiedLinkError) {
        // This error means the native library wasn't loaded or the method wasn't found.
        // This is a critical JNI setup issue.
        println("JNI Error in findSmallestEnclosingSkewedEllipse: ${e.message}")
        e.printStackTrace() // Good for debugging
        return null
    } catch (e: Exception) {
        // Catch any other exceptions that might occur during the process
        println("Error during ellipse calculation: ${e.message}")
        e.printStackTrace()
        return null
    }
}
