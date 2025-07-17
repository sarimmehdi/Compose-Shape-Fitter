#include <jni.h>
#include <vector>
#include <string> // For std::string in exception messages
#include <stdexcept> // For std::runtime_error (example)
#include <android/log.h> // For logging to logcat
#include <algorithm> // For std::max and std::min (if needed)

// IMPORTANT: Make sure this path is correct for your project structure
// Assuming DirectEllipseFit.h is in the same directory (or reachable via include paths in CMake)
#include "DirectEllipseFit.h"

// Helper for logging
#define LOG_TAG "EllipseFitterJNI"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// JNI Function Implementation
// Replace com_sarim_compose_shape_fitter with your actual package name, underscores instead of dots.
// Also, ensure the class name "EllipseFitterJNI" matches your Kotlin/Java native class.
extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_sarim_compose_1shape_1fitter_EllipseFitterJNI_fitEllipseNative(
        JNIEnv *env,
        jobject /* this */, // Reference to the EllipseFitterJNI object instance
        jfloatArray pointsX_jni,
        jfloatArray pointsY_jni) {

    // 1. Convert JNI jfloatArray to C++ std::vector<double>
    //    DirectEllipseFit<T> is templated. Using double for potentially better precision.
    jsize lenX = env->GetArrayLength(pointsX_jni);
    jsize lenY = env->GetArrayLength(pointsY_jni);

    // Basic validation: need at least a few points for a meaningful fit (e.g., Fitzgibbon needs >= 5, ideally more)
    // The DirectEllipseFit class itself might have stricter requirements (e.g. 6 points).
    if (lenX != lenY || lenX < 6) { // Adjusted minimum length based on common requirements for conic fitting
        LOGE("Input point arrays have different lengths, are empty, or too few points (need at least 6). Length: %d", lenX);
        return nullptr;
    }

    jfloat *x_elements_ptr = env->GetFloatArrayElements(pointsX_jni, nullptr);
    jfloat *y_elements_ptr = env->GetFloatArrayElements(pointsY_jni, nullptr);

    if (x_elements_ptr == nullptr || y_elements_ptr == nullptr) {
        LOGE("Failed to get array elements from JNI.");
        // Release any acquired arrays if one failed
        if (x_elements_ptr) env->ReleaseFloatArrayElements(pointsX_jni, x_elements_ptr, JNI_ABORT);
        if (y_elements_ptr) env->ReleaseFloatArrayElements(pointsY_jni, y_elements_ptr, JNI_ABORT);
        return nullptr;
    }

    std::vector<double> xVec(lenX);
    std::vector<double> yVec(lenX);
    for (jsize i = 0; i < lenX; ++i) {
        xVec[i] = static_cast<double>(x_elements_ptr[i]);
        yVec[i] = static_cast<double>(y_elements_ptr[i]);
    }

    // Release JNI array elements as we have copied the data
    env->ReleaseFloatArrayElements(pointsX_jni, x_elements_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(pointsY_jni, y_elements_ptr, JNI_ABORT);

    // 2. Use your DirectEllipseFit class
    Ellipse fittedCppEllipse;
    try {
        // Instantiate DirectEllipseFit with double type
        DirectEllipseFit<double> ellipFit(xVec, yVec);
        fittedCppEllipse = ellipFit.doEllipseFit();

        // 3. Check if fitting was successful and extract parameters
        if (!fittedCppEllipse.geomFlag || !fittedCppEllipse.algeFlag) {
            LOGE("Ellipse fitting failed or resulted in invalid geometric/algebraic parameters.");
            // fittedCppEllipse.rl and .rs might be 0 or negative if geomFlag is false.
            // Check algeFlag as well, as geom params depend on alge params.
            return nullptr;
        }

        // Parameters from the Ellipse class (defined in DirectEllipseFit.h)
        // These are already floats in the Ellipse class definition you provided earlier.
        // If they were double in Ellipse, you'd static_cast here.
        float centerX = fittedCppEllipse.cx;
        float centerY = fittedCppEllipse.cy;
        float radiusA = fittedCppEllipse.rl; // rl is semi-major axis
        float radiusB = fittedCppEllipse.rs; // rs is semi-minor axis
        float angleRad = fittedCppEllipse.phi; // phi is rotation in radians

        // Ensure radii are positive (geomFlag check should cover most issues, but an explicit check is safe)
        // Our Ellipse::alge2geom has checks, and rs <= rl. rl > 0 is important.
        if (radiusA <= 0 || radiusB < 0) { // radiusB can be 0 for a line, but typically > 0 for an ellipse
            LOGE("Ellipse fitting resulted in non-positive or invalid radii: rA=%.3f, rB=%.3f", radiusA, radiusB);
            return nullptr;
        }
        // Ensure angle is in a consistent range if necessary (phi from alge2geom is [0, PI))
        // LOGI("Fitted Ellipse: cx=%.2f, cy=%.2f, rA=%.2f, rB=%.2f, phi=%.2f rad", centerX, centerY, radiusA, radiusB, angleRad);


        // 4. Create a jfloatArray to return to Kotlin
        // The order should match what Kotlin expects: centerX, centerY, radiusX, radiusY, angleRadians
        jfloatArray resultArray = env->NewFloatArray(5);
        if (resultArray == nullptr) {
            LOGE("Failed to allocate new float array for JNI result.");
            return nullptr; // JVM out of memory
        }

        jfloat fill[5];
        fill[0] = centerX;
        fill[1] = centerY;
        fill[2] = radiusA; // Typically radiusA (rl) is considered radiusX for the primary axis
        fill[3] = radiusB; // And radiusB (rs) is radiusY
        fill[4] = angleRad;

        env->SetFloatArrayRegion(resultArray, 0, 5, fill);

        return resultArray;

    } catch (const std::exception& e) {
        // Log standard exceptions (e.g., from Eigen if any were to propagate, or std::bad_alloc)
        LOGE("C++ std::exception during ellipse fitting: %s", e.what());
        return nullptr;
    } catch (...) {
        // Catch any other C++ exceptions
        LOGE("Unknown C++ exception during ellipse fitting.");
        return nullptr;
    }
}
