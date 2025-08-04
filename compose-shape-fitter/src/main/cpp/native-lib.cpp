#include "ellipse-fit/DirectEllipseFit.h"
#include <jni.h>
#include <vector>
#include <android/log.h>
#include <sstream>

#define LOG_TAG "EllipseFitterJNI"
#define LOGE(logCondition, ...) \
    if (logCondition) { __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__); }
#define LOGI(logCondition, ...) \
    if (logCondition) { __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__); }
#define LOGD(logCondition, ...) \
    if (logCondition) { __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__); }

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_sarim_compose_1shape_1fitter_shape_EllipseFitterJNI_fitEllipseNative(
        JNIEnv *env,
        jobject /* this */,
        jfloatArray pointsX_jni,
        jfloatArray pointsY_jni,
        jboolean shouldLog_jni
) {
    bool enableLoggingThisCall = (shouldLog_jni == JNI_TRUE);

    jsize num_points_x = env->GetArrayLength(pointsX_jni);
    jsize num_points_y = env->GetArrayLength(pointsY_jni);

    LOGD(enableLoggingThisCall, "Received %d X-points and %d Y-points.", num_points_x, num_points_y)
    if (num_points_x > 0 && num_points_x == num_points_y) {
        jfloat *pointsX_temp_for_log = env->GetFloatArrayElements(pointsX_jni, nullptr);
        jfloat *pointsY_temp_for_log = env->GetFloatArrayElements(pointsY_jni, nullptr);
        if (pointsX_temp_for_log && pointsY_temp_for_log) {
            std::ostringstream ss_points;
            ss_points << "Input Points (X, Y): ";
            for (jsize i = 0; i < num_points_x; ++i) {
                ss_points << "(" << pointsX_temp_for_log[i] << ", " << pointsY_temp_for_log[i] << ") ";
                if (i >= 9 && num_points_x > 10) { // Log max ~10 points
                    ss_points << "... (and " << (num_points_x - (i + 1)) << " more)";
                    break;
                }
            }
            LOGD(enableLoggingThisCall, "%s", ss_points.str().c_str())
            env->ReleaseFloatArrayElements(pointsX_jni, pointsX_temp_for_log, JNI_ABORT);
            env->ReleaseFloatArrayElements(pointsY_jni, pointsY_temp_for_log, JNI_ABORT);
        }
    }

    if (num_points_x != num_points_y) {
        LOGE(enableLoggingThisCall, "Error: X and Y point arrays must have the same length.")
        return nullptr;
    }
    if (num_points_x < 6) {
        LOGE(enableLoggingThisCall, "Error: Insufficient points to fit an ellipse (need at least 6 for DirectEllipseFit). Provided: %d", num_points_x)
        return nullptr;
    }

    jfloat *pointsX_c = env->GetFloatArrayElements(pointsX_jni, nullptr);
    if (!pointsX_c) {
        LOGE(enableLoggingThisCall, "Error: Could not get C array for X points.")
        return nullptr;
    }
    jfloat *pointsY_c = env->GetFloatArrayElements(pointsY_jni, nullptr);
    if (!pointsY_c) {
        env->ReleaseFloatArrayElements(pointsX_jni, pointsX_c, JNI_ABORT);
        LOGE(enableLoggingThisCall, "Error: Could not get C array for Y points.")
        return nullptr;
    }

    Eigen::VectorXf xData_eigen(num_points_x);
    Eigen::VectorXf yData_eigen(num_points_x);

    for (jsize i = 0; i < num_points_x; ++i) {
        xData_eigen(i) = static_cast<float>(pointsX_c[i]);
        yData_eigen(i) = static_cast<float>(pointsY_c[i]);
    }

    env->ReleaseFloatArrayElements(pointsX_jni, pointsX_c, JNI_ABORT);
    env->ReleaseFloatArrayElements(pointsY_jni, pointsY_c, JNI_ABORT);

    Ellipse fitted_ellipse;

    try {
        DirectEllipseFit<float> fitter(xData_eigen, yData_eigen);
        fitted_ellipse = fitter.doEllipseFit();

        if (!fitted_ellipse.geomFlag) {
            LOGE(enableLoggingThisCall, "Ellipse fitting succeeded but failed to convert to geometric parameters or geometric parameters are invalid.")
            if (fitted_ellipse.algeFlag) {
                LOGD(enableLoggingThisCall, "Algebraic parameters were: a=%.3f, b=%.3f, c=%.3f, d=%.3f, e=%.3f, f=%.3f",
                     fitted_ellipse.a, fitted_ellipse.b, fitted_ellipse.c,
                     fitted_ellipse.d, fitted_ellipse.e, fitted_ellipse.f)
            }
            return nullptr;
        }
    } catch (const std::invalid_argument& e) {
        LOGE(enableLoggingThisCall, "Error during ellipse fitting (invalid argument): %s", e.what())
        return nullptr;
    } catch (const std::exception& e) {
        LOGE(enableLoggingThisCall, "Error during ellipse fitting (std::exception): %s", e.what())
        return nullptr;
    } catch (...) {
        LOGE(enableLoggingThisCall, "Unknown error during ellipse fitting.")
        return nullptr;
    }

    jfloat result_params[5];
    result_params[0] = fitted_ellipse.cx;
    result_params[1] = fitted_ellipse.cy;
    result_params[2] = fitted_ellipse.rl;
    result_params[3] = fitted_ellipse.rs;
    result_params[4] = fitted_ellipse.phi;

    LOGI(enableLoggingThisCall, "Successfully fitted ellipse: cx=%.2f, cy=%.2f, r_major=%.2f, r_minor=%.2f, phi=%.2f rad",
         result_params[0], result_params[1], result_params[2], result_params[3], result_params[4])

    jfloatArray resultArray_jni = env->NewFloatArray(5);
    if (resultArray_jni == nullptr) {
        LOGE(enableLoggingThisCall, "Error: Could not create JNI result array for geometric parameters.")
        return nullptr;
    }
    env->SetFloatArrayRegion(resultArray_jni, 0, 5, result_params);

    return resultArray_jni;
}

