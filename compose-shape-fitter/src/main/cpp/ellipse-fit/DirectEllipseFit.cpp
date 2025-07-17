#include "DirectEllipseFit.h"
#include <cmath>
#include <stdexcept>
#include <limits>
#include <iostream>

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#ifndef M_PI_2
#define M_PI_2 1.57079632679489661923
#endif

Ellipse::Ellipse()
{
    algeFlag = false;
    a = b = c = d = e = f = 0;

    geomFlag = false;
    cx = cy = 0;
    rl = rs = 0;
    phi = 0;
}

void Ellipse::alge2geom()
{
    if(!algeFlag)
        return;

    auto dA = static_cast<double>(a);
    auto dB = static_cast<double>(b);
    auto dC = static_cast<double>(c);
    auto dD = static_cast<double>(d);
    auto dE = static_cast<double>(e);
    auto dF = static_cast<double>(f);

    double tmp1_denom = dB*dB - 4*dA*dC;

    if (std::abs(tmp1_denom) < 1e-12) {
        geomFlag = false;
        return;
    }

    // For an ellipse, B^2 - 4AC < 0, so tmp1_denom should be negative.
    // If it's positive, it's a hyperbola or degenerate.
    if (tmp1_denom > 1e-9 && !(dA==0 && dC==0)) { // Check if it's positive and not a parabola (A=C=0 case)
        // This indicates it might not be an ellipse if not handled carefully
         std::cerr << "Warning: alge2geom tmp1_denom (B^2-4AC) is positive: " << tmp1_denom << std::endl;
         geomFlag = false;
         return;
    }


    double term_in_sqrt_for_tmp2 = (dA-dC)*(dA-dC)+dB*dB;
    if(term_in_sqrt_for_tmp2 < 0) { // Should not happen with real coefficients
        geomFlag = false;
        return;
    }
    double tmp2_sqrt_val = std::sqrt(term_in_sqrt_for_tmp2);

    // Numerator term for radii: 2 * (A E^2 + C D^2 - B D E + (B^2-4AC)F)
    double common_numerator_term_for_radii_sq = 2 * (dA*dE*dE + dC*dD*dD - dB*dD*dE + tmp1_denom*dF);
    double r1_num_sqrt_arg = common_numerator_term_for_radii_sq * (dA + dC + tmp2_sqrt_val);
    double r2_num_sqrt_arg = common_numerator_term_for_radii_sq * (dA + dC - tmp2_sqrt_val);

    if (r1_num_sqrt_arg < -1e-9 || r2_num_sqrt_arg < -1e-9) { // Allow small negative due to precision
        geomFlag = false;
        return;
    }

    // Ensure arguments are non-negative before sqrt
    r1_num_sqrt_arg = std::max(0.0, r1_num_sqrt_arg);
    r2_num_sqrt_arg = std::max(0.0, r2_num_sqrt_arg);


    double r_cand1 = -std::sqrt(std::max(0.0, r1_num_sqrt_arg)) / tmp1_denom; // From +tmp2_sqrt_val path
    double r_cand2 = -std::sqrt(std::max(0.0, r2_num_sqrt_arg)) / tmp1_denom; // From -tmp2_sqrt_val path

    // Calculate the raw orientation angle. This angle is generally associated
    // with the axis corresponding to r_cand1.
    auto angle_for_rcand1 = static_cast<float>(0.5 * std::atan2(dB, dA - dC));

    // Assign rl (semi-major) and rs (semi-minor), and set phi to be the angle of rl.
    if (r_cand1 >= r_cand2) {
        rl = static_cast<float>(r_cand1);
        rs = static_cast<float>(r_cand2);
        phi = angle_for_rcand1; // r_cand1 is major, and angle_for_rcand1 is its angle
    } else {
        rl = static_cast<float>(r_cand2); // r_cand2 is major
        rs = static_cast<float>(r_cand1);
        // angle_for_rcand1 was for r_cand1 (now minor). The major axis (r_cand2) is perpendicular.
        phi = angle_for_rcand1 + static_cast<float>(M_PI_2);
    }

    // Validity checks for radii
    if (std::isinf(rl) || std::isnan(rl) || std::isinf(rs) || std::isnan(rs) || rs < -1e-6f || rl < -1e-6f) {
        geomFlag = false;
        return;
    }
    if (rl < 0.0f) rl = 0.0f;
    if (rs < 0.0f) rs = 0.0f;

    // Ensure rl >= rs (should hold from above, but a safeguard)
    if (rs > rl) { // This case implies r_cand1 was actually < r_cand2 initially
        // And the 'else' block above was taken, setting phi = angle_for_rcand1 + M_PI_2
        // If we then swap rl and rs, it means the new rl (old rs) had angle (angle_for_rcand1 + M_PI_2)
        // and the new rs (old rl) had angle (angle_for_rcand1).
        // This safeguard should ideally not be hit if the logic is correct.
        std::swap(rl, rs);
        // If swapped, the original angle_for_rcand1 was for the *new* rs (old rl).
        // The *new* rl (old rs) was perpendicular to angle_for_rcand1.
        // So, phi should still be angle_for_rcand1 + M_PI_2 if rs>rl initially, or angle_for_rcand1 if rl>rs initially.
        // The current phi value before swap would be angle_for_rcand1 + M_PI_2 (if r_cand1 < r_cand2)
        // or angle_for_rcand1 (if r_cand1 >= r_cand2).
        // If we swap rl and rs here, the phi needs to be the angle of the *new* rl.
        // This makes the safeguard tricky. Let's simplify the safeguard: if a swap happens, assume the current phi is wrong by PI/2.
        phi += static_cast<float>(M_PI_2); // This is a common adjustment if axes are unexpectedly swapped.
    }


    cx = static_cast<float>((2*dC*dD - dB*dE) / tmp1_denom);
    cy = static_cast<float>((2*dA*dE - dB*dD) / tmp1_denom);

    // Normalize phi to [0, PI)
    while (phi >= static_cast<float>(M_PI)) {
        phi -= static_cast<float>(M_PI);
    }
    while (phi < 0.0f) {
        phi += static_cast<float>(M_PI);
    }

    geomFlag = true;
}

template <typename T>
DirectEllipseFit<T>::DirectEllipseFit(const Eigen::Vector<T, Eigen::Dynamic>& xData,
                                      const Eigen::Vector<T, Eigen::Dynamic>& yData)
        : m_xData(xData), m_yData(yData)
{
    if (xData.size() != yData.size()) {
        throw std::invalid_argument("DirectEllipseFit: xData and yData must have the same size.");
    }
    if (xData.size() < 6) { // Need at least 5 points for an ellipse, 6 for general conic. More for robust fit.
        throw std::invalid_argument("DirectEllipseFit: Not enough data points to fit an ellipse. Need at least 6 for this method.");
    }
}

template <typename T>
Ellipse DirectEllipseFit<T>::doEllipseFit()
{
    //Data preparation: normalize data
    Eigen::Vector<T, Eigen::Dynamic> xData_norm = symmetricNormalize(m_xData);
    Eigen::Vector<T, Eigen::Dynamic> yData_norm = symmetricNormalize(m_yData);

    Eigen::Matrix<T, Eigen::Dynamic, 6> dMtrx = getDesignMatrix(xData_norm, yData_norm);
    Eigen::Matrix<T, 6, 6> sMtrx = getScatterMatrix(dMtrx);
    Eigen::Matrix<T, 6, 6> cMtrx = getConstraintMatrix();

    Eigen::Matrix<T, Eigen::Dynamic, Eigen::Dynamic> eigVV; // Eigenvalues and Eigenvectors
    bool flag = solveGeneralEigens(sMtrx, cMtrx, eigVV);

    if (!flag) {
        std::cerr << "DirectEllipseFit::doEllipseFit: Eigensystem solving failure!" << std::endl;
        return Ellipse{}; // Return default/invalid ellipse
    }

    Ellipse ellip = calcEllipsePara(eigVV);
    return ellip;
}

template <typename T>
T DirectEllipseFit<T>::getMeanValue(const Eigen::Vector<T, Eigen::Dynamic>& data)
{
    if (data.size() == 0) return static_cast<T>(0);
    return data.mean();
}

template <typename T>
T DirectEllipseFit<T>::getMaxValue(const Eigen::Vector<T, Eigen::Dynamic>& data)
{
    if (data.size() == 0) return static_cast<T>(0);
    return data.maxCoeff();
}

template <typename T>
T DirectEllipseFit<T>::getMinValue(const Eigen::Vector<T, Eigen::Dynamic>& data)
{
    if (data.size() == 0) return static_cast<T>(0);
    return data.minCoeff();
}

template <typename T>
T DirectEllipseFit<T>::getScaleValue(const Eigen::Vector<T, Eigen::Dynamic>& data)
{
    if (data.size() < 2) return static_cast<T>(1.0);
    T val = static_cast<T>(0.5) * (getMaxValue(data) - getMinValue(data));
    if (std::abs(val) < std::numeric_limits<T>::epsilon()){
        return static_cast<T>(1.0); // Avoid division by zero if all points are (nearly) identical
    }
    return val;
}

template <typename T>
Eigen::Vector<T, Eigen::Dynamic> DirectEllipseFit<T>::symmetricNormalize(const Eigen::Vector<T, Eigen::Dynamic>& data)
{
    if (data.size() == 0) return data;
    T mean = getMeanValue(data);
    T normScale = getScaleValue(data); // This now returns 1.0 if scale is too small

    // normScale should not be zero due to getScaleValue modification
    return (data.array() - mean) / normScale;
}

template <typename T>
Eigen::Matrix<T, Eigen::Dynamic, 6> DirectEllipseFit<T>::getDesignMatrix(
        const Eigen::Vector<T, Eigen::Dynamic>& xData,
        const Eigen::Vector<T, Eigen::Dynamic>& yData)
{
    long n = xData.size();
    Eigen::Matrix<T, Eigen::Dynamic, 6> designMtrx(n, 6);

    designMtrx.col(0) = xData.array().square();         // xn^2 term
    designMtrx.col(1) = xData.array() * yData.array();  // xn*yn term
    designMtrx.col(2) = yData.array().square();         // yn^2 term
    designMtrx.col(3) = xData;                          // xn term
    designMtrx.col(4) = yData;                          // yn term
    designMtrx.col(5) = Eigen::Vector<T, Eigen::Dynamic>::Ones(n); // constant term related

    return designMtrx;
}

template <typename T>
Eigen::Matrix<T, 6, 6> DirectEllipseFit<T>::getConstraintMatrix()
{
    Eigen::Matrix<T, 6, 6> consMtrx = Eigen::Matrix<T, 6, 6>::Zero();
    consMtrx(1, 1) = static_cast<T>(1.0);
    consMtrx(0, 2) = static_cast<T>(-2.0);
    consMtrx(2, 0) = static_cast<T>(-2.0);
    return consMtrx;
}

template <typename T>
Eigen::Matrix<T, 6, 6> DirectEllipseFit<T>::getScatterMatrix(
        const Eigen::Matrix<T, Eigen::Dynamic, 6>& dMtrx)
{
    return dMtrx.transpose() * dMtrx;
}

template <typename T>
bool DirectEllipseFit<T>::solveGeneralEigens(
        const Eigen::Matrix<T, 6, 6>& sMtrx,
        const Eigen::Matrix<T, 6, 6>& cMtrx,
        Eigen::Matrix<T, Eigen::Dynamic, Eigen::Dynamic>& eigVV_out)
{
    // Eigen's GeneralizedEigenSolver prefers double or float.
    const auto& sMtrx_d = sMtrx.template cast<double>();
    const auto& cMtrx_d = cMtrx.template cast<double>();

    Eigen::GeneralizedEigenSolver<Eigen::Matrix<double, 6, 6>> ges;
    ges.compute(sMtrx_d, cMtrx_d);

    if (ges.info() != Eigen::Success) {
        std::cerr << "DirectEllipseFit::solveGeneralEigens: Eigenvalue computation failed. ges.info(): " << ges.info() << std::endl;
        return false;
    }

    Eigen::VectorXcd eigenvalues_complex = ges.eigenvalues();
    Eigen::MatrixXcd eigenvectors_complex = ges.eigenvectors();

    eigVV_out.resize(6, 7); // 6 rows. Col 0 for eigenvalues (real part), Cols 1-6 for eigenvectors (real part).

    for (int i = 0; i < 6; ++i) {
        if (std::isinf(eigenvalues_complex(i).real()) || std::isnan(eigenvalues_complex(i).real())) {
            std::cerr << "DirectEllipseFit::solveGeneralEigens: NaN or Inf eigenvalue encountered at index " << i << std::endl;
        }
        eigVV_out(i, 0) = static_cast<T>(eigenvalues_complex(i).real());
        for (int j = 0; j < 6; ++j) {
            if (std::isinf(eigenvectors_complex(j,i).real()) || std::isnan(eigenvectors_complex(j,i).real())) {
                std::cerr << "DirectEllipseFit::solveGeneralEigens: NaN or Inf eigenvector component encountered at (" << j << "," << i << ")" << std::endl;
            }
            eigVV_out(j, i + 1) = static_cast<T>(eigenvectors_complex(j, i).real());
        }
    }
    return true;
}

template <typename T>
Ellipse DirectEllipseFit<T>::calcEllipsePara(
        const Eigen::Matrix<T, Eigen::Dynamic, Eigen::Dynamic>& eigVV)
{
    int best_eigIdx = -1;
    T quality_metric_for_best_idx = std::numeric_limits<T>::max(); // e.g. smallest absolute lambda for a valid ellipse

    for (int i = 0; i < 6; ++i) { // Iterate through the 6 eigenvalue/eigenvector pairs
        Eigen::Matrix<T, 6, 1> eigenvector = eigVV.block(0, i + 1, 6, 1); // Get i-th eigenvector
        T current_lambda = eigVV(i, 0); // The i-th eigenvalue

        T ellipse_condition_value = eigenvector(1) * eigenvector(1) -
                                    static_cast<T>(4.0) * eigenvector(0) * eigenvector(2);

        if (ellipse_condition_value < static_cast<T>(-1e-9)) { // Must be significantly less than 0 for an ellipse
            // This eigenvector corresponds to an ellipse.
            // Now, select among these based on some criteria.
            if (std::abs(current_lambda) < quality_metric_for_best_idx) {
                quality_metric_for_best_idx = std::abs(current_lambda);
                best_eigIdx = i;
            }
        }
    }

    if (best_eigIdx < 0) {
        std::cerr << "DirectEllipseFit::calcEllipsePara: No suitable eigenvector found that robustly satisfies B_norm^2 - 4*A_norm*C_norm < 0." << std::endl;
        // You could add more diagnostics here, printing all eigenvalues and their B^2-4AC values
        return Ellipse{}; // Return default/invalid ellipse
    }

    // Unnormalize: The parameters tA..tF are for the *normalized* data
    T tA = eigVV(0, best_eigIdx + 1); // Coeff of xn^2
    T tB = eigVV(1, best_eigIdx + 1); // Coeff of xn*yn
    T tC = eigVV(2, best_eigIdx + 1); // Coeff of yn^2
    T tD = eigVV(3, best_eigIdx + 1); // Coeff of xn
    T tE = eigVV(4, best_eigIdx + 1); // Coeff of yn
    T tF = eigVV(5, best_eigIdx + 1); // Constant term for normalized equation

    T mx = getMeanValue(m_xData);
    T my = getMeanValue(m_yData);
    T sx = getScaleValue(m_xData);
    T sy = getScaleValue(m_yData);

    // sx and sy should be > 0 due to getScaleValue returning 1.0 for zero range.
    // However, an explicit check for very small values might still be good practice if critical.
    if (std::abs(sx) < std::numeric_limits<T>::epsilon()) sx = static_cast<T>(1.0);
    if (std::abs(sy) < std::numeric_limits<T>::epsilon()) sy = static_cast<T>(1.0);


    Ellipse ellip;

    T sx2 = sx * sx;
    T sy2 = sy * sy;
    T sxsy = sx * sy;

    // Check for sx2, sy2, sxsy being zero if sx or sy were extremely small despite previous checks
    if (std::abs(sx2) < std::numeric_limits<T>::epsilon() ||
        std::abs(sy2) < std::numeric_limits<T>::epsilon() ||
        std::abs(sxsy) < std::numeric_limits<T>::epsilon()) {
        std::cerr << "DirectEllipseFit::calcEllipsePara: Scale factor squared is too small, potential division by zero." << std::endl;
        ellip.algeFlag = false; // Mark as invalid
        return ellip;
    }


    ellip.a = tA / sx2;
    ellip.b = tB / sxsy;
    ellip.c = tC / sy2;

    ellip.d = (tD / sx) - (static_cast<T>(2.0) * tA * mx / sx2) - (tB * my / sxsy);
    ellip.e = (tE / sy) - (static_cast<T>(2.0) * tC * my / sy2) - (tB * mx / sxsy);

    ellip.f = tF - (tD * mx / sx) - (tE * my / sy) +
              (tA * mx * mx / sx2) + (tB * mx * my / sxsy) + (tC * my * my / sy2);

    ellip.algeFlag = true;
    ellip.alge2geom();

    return ellip;
}

template class DirectEllipseFit<float>;
template class DirectEllipseFit<double>;