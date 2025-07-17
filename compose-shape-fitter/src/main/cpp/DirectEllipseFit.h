/*******************************************************************************
 * ProName: DirectEllipseFit.h
 * Author:  Zhenyu Yuan
 * Data:    2016/7/27
 * Modified: [Your Name/Date] - Replaced QVector and clapack
 * -----------------------------------------------------------------------------
 * INSTRUCTION: Perform ellipse fitting by direct method
 * DEPENDANCE:  Eigen (for linear algebra)
 * REFERENCE:
 *      (1) Fitzgibbon, A., et al. (1999). "Direct least square fitting of ellipses."
 *          IEEE Transactions on pattern analysis and machine intelligence 21(5):
 *          476-480.
 *      (2) http://homepages.inf.ed.ac.uk/rbf/CVonline/LOCAL_COPIES/FITZGIBBON/ELLIPSE/
 * CONVENTION:
 *      (1) Matrix expressed as std::vector<std::vector<T> >, fast direction is rows,
 *          slow direction is columns, that is, internal std::vector indicates column
 *          vector of T, external std::vector indicates row vector of column vectors
 *      (2) Matrix expressed as 1-order array, arranged in rows, that is, row by
 *          row.
 ******************************************************************************/
#ifndef DIRECTELLIPSEFIT_H
#define DIRECTELLIPSEFIT_H

#include <vector>
#include <cmath> // For std::isinf, std::sqrt, std::atan2, M_PI
#include <numeric> // For std::accumulate
#include <algorithm> // For std::min_element, std::max_element
#include <iostream> // For debugging (replace with your preferred logging)

// Make sure to include Eigen headers
// You might need to adjust the path depending on your Eigen installation
#include "Eigen/Dense"
#include "Eigen/Eigenvalues"


class Ellipse
{
public:
    Ellipse() : a(0), b(0), c(0), d(0), e(0), f(0), algeFlag(false),
                cx(0), cy(0), rl(0), rs(0), phi(0), geomFlag(false) {}

    /**
     * @brief alge2geom:    algebraic parameters to geometric parameters
     * @ref:    https://en.wikipedia.org/wiki/Ellipse#In_analytic_geometry
     *          http:homepages.inf.ed.ac.uk/rbf/CVonline/LOCAL_COPIES/FITZGIBBON/ELLIPSE/
     * @note:   The calculation of phi refer to wikipedia is not correct,
     *          refer to Bob Fisher's matlab program.
     *          What's more, the calculated geometric parameters can't back to
     *          initial algebraic parameters from geom2alge();
     */
    void alge2geom() {
        if (!algeFlag) return;

        // Ensure the conic is an ellipse: b^2 - 4ac < 0
        if (b * b - 4 * a * c >= 0) {
            geomFlag = false;
            // std::cerr << "Warning: Conic is not an ellipse." << std::endl;
            return;
        }

        // Calculate center (cx, cy)
        double common_denominator = 4 * a * c - b * b;
        if (std::abs(common_denominator) < 1e-9) { // Avoid division by zero
            geomFlag = false;
            return;
        }
        cx = (b * e - 2 * c * d) / common_denominator;
        cy = (b * d - 2 * a * e) / common_denominator;

        // Calculate semi-axes lengths (rl, rs)
        double term1 = 2 * (a * e * e + c * d * d - b * d * e + common_denominator * f / 4);
        double term2_sqrt = std::sqrt((a - c) * (a - c) + b * b);

        if (std::abs(a * c - b * b / 4) < 1e-9) { // Avoid division by zero
            geomFlag = false;
            return;
        }

        double val_rl_sq = -term1 / (common_denominator * (a + c + term2_sqrt));
        double val_rs_sq = -term1 / (common_denominator * (a + c - term2_sqrt));

        if (val_rl_sq < 0 || val_rs_sq < 0) {
            geomFlag = false;
            // std::cerr << "Warning: Non-real semi-axes lengths." << std::endl;
            return;
        }


        rl = std::sqrt(val_rl_sq);
        rs = std::sqrt(val_rs_sq);


        // Ensure rl is the semi-major axis
        if (rs > rl) {
            std::swap(rl, rs);
        }

        // Calculate orientation angle phi
        // The formula for phi can be ambiguous. This is a common one.
        if (std::abs(b) < 1e-9) {
            phi = (a < c) ? 0.0 : M_PI / 2.0;
        } else {
            phi = std::atan2(b, a - c) / 2.0;
            // Adjust phi based on the relationship between a, c and b
            // This is a common ambiguity in atan2 results for ellipse orientation
            if (a > c && b < 0) {
                phi += M_PI;
            } else if (a < c && b > 0) {
                phi += M_PI;
            }
            // Ensure phi is in [0, PI)
            while (phi < 0) phi += M_PI;
            while (phi >= M_PI) phi -= M_PI;
        }


        geomFlag = true;
    }
    /**
     * @brief geom2alge:    geometric parameters to algebraic parameters
     * @ref:    https://en.wikipedia.org/wiki/Ellipse#In_analytic_geometry
     */
    void geom2alge() {
        if (!geomFlag) return;

        double cos_phi = std::cos(phi);
        double sin_phi = std::sin(phi);
        double cos_phi_sq = cos_phi * cos_phi;
        double sin_phi_sq = sin_phi * sin_phi;

        double rl_sq = rl * rl;
        double rs_sq = rs * rs;

        if (std::abs(rl_sq) < 1e-9 || std::abs(rs_sq) < 1e-9) { // Avoid division by zero
            algeFlag = false;
            return;
        }

        a = cos_phi_sq / rl_sq + sin_phi_sq / rs_sq;
        b = 2 * cos_phi * sin_phi * (1 / rl_sq - 1 / rs_sq);
        c = sin_phi_sq / rl_sq + cos_phi_sq / rs_sq;
        d = -2 * a * cx - b * cy;
        e = -b * cx - 2 * c * cy;
        f = a * cx * cx + b * cx * cy + c * cy * cy - 1; // Assuming the ellipse equation is Ax^2 + Bxy + Cy^2 + Dx + Ey + F = 0 where F makes it = 1 for the canonical form before translation/rotation. Or set F based on desired form. For a general conic, often set to -1, which is what Fitzgibbon implies.

        algeFlag = true;
    }

public:
    //algebraic parameters as coefficients of conic section
    float a, b, c, d, e, f;
    bool algeFlag;

    //geometric parameters
    float cx;   //centor in x coordinate
    float cy;   //centor in y coordinate
    float rl;   //semimajor: large radius
    float rs;   //semiminor: small radius
    float phi;  //azimuth angel in radian unit
    bool geomFlag;
};

template <typename T>
class DirectEllipseFit
{
public:
    DirectEllipseFit(const std::vector<T> &xData, const std::vector<T> &yData)
            : m_xData(xData), m_yData(yData) {}

    Ellipse doEllipseFit();

private:
    T getMeanValue(const std::vector<T> &data);
    T getMaxValue(const std::vector<T> &data);
    T getMinValue(const std::vector<T> &data);
    T getScaleValue(const std::vector<T> &data);
    std::vector<T> symmetricNormalize(const std::vector<T> &data);
    //Make sure xData and yData are of same size
    std::vector<T> dotMultiply(const std::vector<T> &xData, const std::vector<T> &yData);
    //Get n*6 design matrix D, make sure xData and yData are of same size
    // Convention: matrix is vector of columns
    Eigen::Matrix<T, Eigen::Dynamic, 6> getDesignMatrixEigen(const std::vector<T> &xData,
                                                             const std::vector<T> &yData);
    //Get 6*6 constraint matrix C
    Eigen::Matrix<T, 6, 6> getConstraintMatrixEigen();


    /**
     * @brief solveGeneralEigens:   Solve generalized eigensystem S*v = lambda*C*v
     * @note        For real eiginsystem solving using Eigen.
     * @param sMtrx:    6*6 scatter matrix
     * @param cMtrx:    6*6 constraint matrix
     * @param eigVecRes: Stores the eigenvector corresponding to the "correct" eigenvalue
     * @return  success or failure status
     */
    bool solveGeneralEigensEigen(const Eigen::Matrix<T, 6, 6> &sMtrx,
                                 const Eigen::Matrix<T, 6, 6> &cMtrx,
                                 Eigen::Matrix<T, 6, 1> &eigVecRes);


    /**
     * @brief calcEllipsePara:  calculate ellipse parameter form eigen information
     * @param eigVec:    The specific eigenvector
     * @return ellipse parameter
     */
    Ellipse calcEllipsePara(const Eigen::Matrix<T, 6, 1> &eigVec);

private:
    std::vector<T> m_xData, m_yData;
};

/*******************************************************************************
 * Template Class Defination
 ******************************************************************************/
template <typename T>
Ellipse DirectEllipseFit<T>::doEllipseFit()
{
    if (m_xData.empty() || m_xData.size() != m_yData.size()) {
        std::cerr << "Error: Input data is empty or sizes mismatch." << std::endl;
        return Ellipse(); // Return an invalid ellipse
    }
    //Data preparation: normalize data
    std::vector<T> xData_norm = symmetricNormalize(m_xData);
    std::vector<T> yData_norm = symmetricNormalize(m_yData);

    //Build n*6 design matrix, n is size of xData or yData
    Eigen::Matrix<T, Eigen::Dynamic, 6> dMtrx = getDesignMatrixEigen(xData_norm, yData_norm);

    //Build 6*6 scatter matrix S = D_transpose * D
    Eigen::Matrix<T, 6, 6> sMtrx = dMtrx.transpose() * dMtrx;

    //Build 6*6 constraint matrix
    Eigen::Matrix<T, 6, 6> cMtrx = getConstraintMatrixEigen();

    //Solve eigensystem
    Eigen::Matrix<T, 6, 1> an; // To store the resulting eigenvector
    bool flag = solveGeneralEigensEigen(sMtrx, cMtrx, an);
    if(!flag) {
        std::cerr<<"Eigensystem solving failure!" << std::endl;
        Ellipse errEllipse;
        errEllipse.algeFlag = false;
        errEllipse.geomFlag = false;
        return errEllipse;
    }

    Ellipse ellip = calcEllipsePara(an);

    return ellip;
}

template <typename T>
T DirectEllipseFit<T>::getMeanValue(const std::vector<T> &data)
{
    if (data.empty()) return 0;
    return std::accumulate(data.begin(), data.end(), static_cast<T>(0.0)) / data.size();
}

template <typename T>
T DirectEllipseFit<T>::getMaxValue(const std::vector<T> &data)
{
    if (data.empty()) return 0; // Or throw an exception
    return *std::max_element(data.begin(), data.end());
}

template <typename T>
T DirectEllipseFit<T>::getMinValue(const std::vector<T> &data)
{
    if (data.empty()) return 0; // Or throw an exception
    return *std::min_element(data.begin(), data.end());
}

template <typename T>
T DirectEllipseFit<T>::getScaleValue(const std::vector<T> &data)
{
    if (data.size() < 2) return 1; // Avoid division by zero or meaningless scale
    return (static_cast<T>(0.5) * (getMaxValue(data) - getMinValue(data)));
}

template <typename T>
std::vector<T> DirectEllipseFit<T>::symmetricNormalize(const std::vector<T> &data)
{
    if (data.empty()) return {};
    T mean = getMeanValue(data);
    T normScale = getScaleValue(data);
    if (std::abs(normScale) < 1e-9) { // Avoid division by zero if all points are the same
        return std::vector<T>(data.size(), static_cast<T>(0.0));
    }

    std::vector<T> symData;
    symData.reserve(data.size());
    for(const T& val : data)
        symData.push_back((val - mean) / normScale);

    return symData;
}

template <typename T>
std::vector<T> DirectEllipseFit<T>::dotMultiply(const std::vector<T> &xData,
                                                const std::vector<T> &yData)
{
    if (xData.size() != yData.size()) {
        // Handle error: sizes must match
        std::cerr << "Error: Dot product arguments have different sizes." << std::endl;
        return {};
    }
    std::vector<T> product;
    product.reserve(xData.size());
    for(size_t i=0; i<xData.size(); ++i)
        product.push_back(xData[i]*yData[i]);

    return product;
}


template <typename T>
Eigen::Matrix<T, Eigen::Dynamic, 6> DirectEllipseFit<T>::getDesignMatrixEigen(
        const std::vector<T> &xData, const std::vector<T> &yData)
{
    long nPoints = xData.size();
    Eigen::Matrix<T, Eigen::Dynamic, 6> designMtrx(nPoints, 6);

    for (long i = 0; i < nPoints; ++i) {
        designMtrx(i, 0) = xData[i] * xData[i]; // x^2
        designMtrx(i, 1) = xData[i] * yData[i]; // xy
        designMtrx(i, 2) = yData[i] * yData[i]; // y^2
        designMtrx(i, 3) = xData[i];            // x
        designMtrx(i, 4) = yData[i];            // y
        designMtrx(i, 5) = static_cast<T>(1.0); // 1
    }
    return designMtrx;
}


template <typename T>
Eigen::Matrix<T, 6, 6> DirectEllipseFit<T>::getConstraintMatrixEigen()
{
    Eigen::Matrix<T, 6, 6> consMtrx = Eigen::Matrix<T, 6, 6>::Zero();
    // Constraint: b^2 - 4ac = 1 (or normalized version)
    // Fitzgibbon's paper uses the constraint 4ac - b^2 = 1
    // which corresponds to:
    // C(0,2) = 2
    // C(1,1) = -1
    // C(2,0) = 2
    // The provided code used:
    // consMtrx[1][1] = 1;  (for b^2 term, index 1)
    // consMtrx[0][2] = -2; (for ac term, index 0 and 2)
    // consMtrx[2][0] = -2; (for ca term, index 2 and 0)
    // This seems to implement -2ac -2ca + b^2 = K (or b^2 - 4ac = K)
    // Let's stick to Fitzgibbon's direct constraint 4ac - b^2 = 1, which leads to a vector [a b c d e f]
    // The quadratic form is A' C A = 1
    // A = [a b c d e f]^T
    // C = [[0 0 2 0 0 0],
    //      [0 -1 0 0 0 0],
    //      [2 0 0 0 0 0],
    //      [0 0 0 0 0 0],
    //      [0 0 0 0 0 0],
    //      [0 0 0 0 0 0]]
    consMtrx(0, 2) = static_cast<T>(2.0);
    consMtrx(1, 1) = static_cast<T>(-1.0);
    consMtrx(2, 0) = static_cast<T>(2.0);

    return consMtrx;
}


template <typename T>
bool DirectEllipseFit<T>::solveGeneralEigensEigen(const Eigen::Matrix<T, 6, 6> &sMtrx,
                                                  const Eigen::Matrix<T, 6, 6> &cMtrx,
                                                  Eigen::Matrix<T, 6, 1> &eigVecRes)
{
    // Solve the generalized eigenvalue problem S*v = lambda*C*v
    // Eigen's GeneralizedEigenSolver is for AV = lambda BV
    // We need to be careful as our C is not positive definite.
    // Fitzgibbon's method ensures that there is one positive eigenvalue
    // and the corresponding eigenvector is the solution.

    // A common way to handle S*a = lambda*C*a where C is singular or indefinite
    // is to solve S*a = lambda*C*a directly if the solver supports it,
    // or transform it.
    // However, the original paper's method relies on finding the eigenvector 'a'
    // such that a^T C a = 1 and a^T S a is minimized.
    // This is equivalent to S a = lambda C a.

    // Using Eigen::GeneralizedEigenSolver
    // Note: The stability and correctness can depend on the properties of S and C.
    // C must be positive definite for some solvers, but not necessarily for dggev which
    // the original code used via CLAPACK.
    // Eigen's GeneralizedSelfAdjointEigenSolver is for Ax = lambda Bx where B is pos-def.
    // Our C is not pos-def.
    // We can try to use a general solver, but it might be more robust to find an alternative way
    // or ensure the solver can handle the specific structure.

    // For S*a = lambda*C*a, if C is invertible, C_inv*S*a = lambda*a
    // However, our C is singular.

    // Let's try to find an implementation of the specific solution Fitzgibbon proposed.
    // The paper states "The solution is found by solving S U = lambda C U for generalized
    // eigenvectors U and finding the one which satisfies U'CU > 0".
    // "For an ellipse, C is indefinite, and it can be shown that there is only one
    //  positive eigenvalue lambda, and its corresponding eigenvector gives the ellipse parameters."

    // We need a generalized eigenvalue solver that can handle indefinite C.
    // Eigen's GeneralizedEigenSolver should work.
    Eigen::GeneralizedEigenSolver<Eigen::Matrix<T, 6, 6>> ges;
    ges.compute(sMtrx, cMtrx);

    if (ges.info() != Eigen::Success) {
        std::cerr << "Eigen: Failed to compute generalized eigenvalues." << std::endl;
        return false;
    }

    Eigen::VectorXcd eigenvalues_complex = ges.alphas().array() / ges.betas().array();
    Eigen::MatrixXcd eigenvectors_complex = ges.eigenvectors();

    int best_idx = -1;
    T max_positive_eigenvalue = static_cast<T>(-1.0); // Or some very small number

    // According to Fitzgibbon, we are looking for the eigenvector associated
    // with the unique positive eigenvalue.
    // The eigenvalues from alpha/beta can be complex if the problem is not definite.
    // However, for this specific problem, we expect real eigenvalues, and one of them
    // should lead to the correct ellipse constraint.

    for (int i = 0; i < eigenvalues_complex.size(); ++i) {
        // We are interested in real eigenvalues that are positive
        if (std::abs(eigenvalues_complex(i).imag()) < 1e-9) { // Check if eigenvalue is real
            T real_eigenvalue = eigenvalues_complex(i).real();
            Eigen::Matrix<T, 6, 1> eigVec = eigenvectors_complex.col(i).real(); // Take real part of eigenvector

            // Check the constraint: eigVec^T * C * eigVec > 0
            // More specifically, Fitzgibbon's method implies that we need the eigenvector 'a'
            // such that a'Ca = 1 (or some positive constant). The condition 4ac - b^2 > 0 is for the ellipse.
            // The constraint matrix C is constructed such that a'Ca = 4ac - b^2.
            // We need 4ac - b^2 > 0 for an ellipse.
            T constraint_val = eigVec(0) * eigVec(2) * T(4.0) - eigVec(1) * eigVec(1);

            // The paper says "the only solution vector U which satisfies U'CU > 0 is the correct one"
            // U'CU corresponds to 4ac - b^2
            if (constraint_val > 1e-9) { // Check if 4ac - b^2 > 0 (strictly positive for ellipse)
                // And this eigenvalue should be positive as per the paper.
                if (real_eigenvalue > 0) { // Check if lambda is positive
                    // If multiple satisfy, this might indicate an issue or need for refinement.
                    // The original paper claims uniqueness for the positive eigenvalue.
                    // Let's pick the one that also maximizes this positive eigenvalue,
                    // or simply the first one found that satisfies the constraint.
                    // The problem looks for minimizing a'Sa subject to a'Ca = 1.
                    // So we are looking for the smallest positive lambda.

                    // The original code was looking for a negative eigenvalue for a different formulation.
                    // Let's adapt based on Fitzgibbon's U'CU > 0 criteria.
                    // "The eigenvector U corresponding to the positive eigenvalue lambda is the solution vector a"
                    // So we need to find the eigenvector corresponding to the positive lambda,
                    // such that U'CU > 0.
                    // There should be only one such positive lambda.

                    // We need to find the eigenvector a such that S a = lambda C a and 4ac - b^2 > 0.
                    // Let's iterate through eigenvalues and check the condition on the eigenvector.
                    eigVecRes = eigVec;
                    // Normalize the eigenvector such that 4ac - b^2 = 1 (or its corresponding a'Ca=1)
                    T norm_factor = eigVecRes.transpose() * cMtrx * eigVecRes;
                    if (std::abs(norm_factor) > 1e-9 && norm_factor > 0) { // Must be positive
                        eigVecRes /= std::sqrt(norm_factor);
                        best_idx = i; // Found a candidate
                        // Typically, there is only one such eigenvalue/vector.
                        // If there are multiple, the problem might be ill-conditioned or the theory implies uniqueness.
                        // For now, take the first one that robustly meets the criteria.
                        return true; // Found the solution
                    }
                }
            }
        }
    }


    if (best_idx == -1) {
        std::cerr << "Eigen: Could not find a suitable eigenvector satisfying the constraint 4ac-b^2 > 0." << std::endl;
        // Fallback or more robust selection might be needed.
        // The original code checked for eigVV.at(i).first() < 1e-6 (close to zero or negative).
        // This suggests the eigenvalue lambda itself was being inspected, not the constraint value directly in that selection step.
        // The condition 4*a*c - b*b > 0 is what defines an ellipse from the parameters [a,b,c,d,e,f].
        // The eigenvector itself contains [a,b,c,d,e,f]. So, after getting an eigenvector, check this.

        // Retry: iterate through eigenvectors, find one that forms an ellipse (4ac-b^2 > 0)
        // and corresponds to the positive eigenvalue lambda.
        for (int i = 0; i < eigenvectors_complex.cols(); ++i) {
            if (std::abs(eigenvalues_complex(i).imag()) < 1e-9 && eigenvalues_complex(i).real() > 1e-9) { // Positive real eigenvalue
                Eigen::Matrix<T, 6, 1> current_vec = eigenvectors_complex.col(i).real();
                T test_a = current_vec(0);
                T test_b = current_vec(1);
                T test_c = current_vec(2);
                if (4 * test_a * test_c - test_b * test_b > 1e-9) { // Ellipse condition
                    eigVecRes = current_vec;
                    // Normalize it so that 4ac - b^2 = 1 (the constraint used in deriving C)
                    T scale = 4 * eigVecRes(0) * eigVecRes(2) - eigVecRes(1) * eigVecRes(1);
                    if (scale > 1e-9) { // Check scale is positive before sqrt
                        eigVecRes /= std::sqrt(scale);
                        return true;
                    }
                }
            }
        }
        std::cerr << "Eigen: Still no suitable eigenvector after retry." << std::endl;
        return false;
    }


    return true; // Should have returned true inside the loop if successful
}


template <typename T>
Ellipse DirectEllipseFit<T>::calcEllipsePara(const Eigen::Matrix<T, 6, 1> &eigVec)
{
    // Unnormalize and get coefficients of conic section
    // The eigenvector 'an' contains the parameters [a, b, c, d, e, f] for the *normalized* data
    T tA = eigVec(0);
    T tB = eigVec(1);
    T tC = eigVec(2);
    T tD = eigVec(3);
    T tE = eigVec(4);
    T tF = eigVec(5);

    T mx = getMeanValue(m_xData);
    T my = getMeanValue(m_yData);
    T sx = getScaleValue(m_xData);
    T sy = getScaleValue(m_yData);

    // Handle cases where scale is zero (e.g., all points are the same)
    // to avoid division by zero or NaN results.
    // If sx or sy is zero, it implies data was constant along that axis.
    // Ellipse fitting might not be meaningful or stable.
    if (std::abs(sx) < 1e-9 || std::abs(sy) < 1e-9) {
        std::cerr << "Warning: Scale factor is zero or near zero. Ellipse parameters might be unstable." << std::endl;
        // Return a default/invalid ellipse or handle as an error
        Ellipse invalidEllipse;
        invalidEllipse.algeFlag = false;
        invalidEllipse.geomFlag = false;
        return invalidEllipse;
    }


    Ellipse ellip;
    // Denormalization formulas from:
    // http://homepages.inf.ed.ac.uk/rbf/CVonline/LOCAL_COPIES/FITZGIBBON/ELLIPSE/ellipse.m
    // (Matlab code by Fitzgibbon)
    // A = p(1); B = p(2); C = p(3); D = p(4); E = p(5); F = p(6); (normalized parameters)
    // a = A*sy*sy;
    // b = B*sx*sy;
    // c = C*sx*sx;
    // d = -2*A*sy*sy*mx - B*sx*sy*my + D*sx*sy*sy;
    // e = -B*sx*sy*mx - 2*C*sx*sx*my + E*sx*sx*sy;
    // f = A*sy*sy*mx*mx + B*sx*sy*mx*my + C*sx*sx*my*my...
    //     - D*sx*sy*sy*mx - E*sx*sx*sy*my + F*sx*sx*sy*sy;

    ellip.a = tA * sy * sy;
    ellip.b = tB * sx * sy;
    ellip.c = tC * sx * sx;
    ellip.d = -2 * tA * sy * sy * mx - tB * sx * sy * my + tD * sx * sy * sy;
    ellip.e = -tB * sx * sy * mx - 2 * tC * sx * sx * my + tE * sx * sx * sy;
    ellip.f = tA * sy * sy * mx * mx + tB * sx * sy * mx * my + tC * sx * sx * my * my
              - tD * sx * sy * sy * mx - tE * sx * sx * sy * my + tF * sx * sx * sy * sy;

    ellip.algeFlag = true;
    ellip.alge2geom(); // Convert to geometric parameters

    return ellip;
}

#endif // DIRECTELLIPSEFIT_H
