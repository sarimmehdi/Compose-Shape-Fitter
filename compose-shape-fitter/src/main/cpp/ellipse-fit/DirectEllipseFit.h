#ifndef DIRECTELLIPSEFIT_H
#define DIRECTELLIPSEFIT_H

#include <Eigen/Dense>
#include <Eigen/Eigenvalues>
#include <vector>
#include <cmath>
#include <iostream>

#ifndef M_PI_2
#define M_PI_2 1.57079632679489661923
#endif
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif


class Ellipse
{
public:
    Ellipse();
    void alge2geom();

public:
    //algebraic parameters
    float a, b, c, d, e, f;
    bool algeFlag;

    //geometric parameters
    float cx;
    float cy;
    float rl;
    float rs;
    float phi;
    bool geomFlag;
};

template <typename T>
class DirectEllipseFit
{
public:
    DirectEllipseFit(const Eigen::Vector<T, Eigen::Dynamic>& xData,
                     const Eigen::Vector<T, Eigen::Dynamic>& yData);
    Ellipse doEllipseFit();

private:
    T getMeanValue(const Eigen::Vector<T, Eigen::Dynamic>& data);
    T getMaxValue(const Eigen::Vector<T, Eigen::Dynamic>& data);
    T getMinValue(const Eigen::Vector<T, Eigen::Dynamic>& data);
    T getScaleValue(const Eigen::Vector<T, Eigen::Dynamic>& data);
    Eigen::Vector<T, Eigen::Dynamic> symmetricNormalize(const Eigen::Vector<T, Eigen::Dynamic>& data);

    Eigen::Matrix<T, Eigen::Dynamic, 6> getDesignMatrix(
            const Eigen::Vector<T, Eigen::Dynamic>& xData,
            const Eigen::Vector<T, Eigen::Dynamic>& yData);

    Eigen::Matrix<T, 6, 6> getConstraintMatrix();

    Eigen::Matrix<T, 6, 6> getScatterMatrix(
            const Eigen::Matrix<T, Eigen::Dynamic, 6>& dMtrx);

    bool solveGeneralEigens(
            const Eigen::Matrix<T, 6, 6>& sMtrx,
            const Eigen::Matrix<T, 6, 6>& cMtrx,
            Eigen::Matrix<T, Eigen::Dynamic, Eigen::Dynamic>& eigVV_out); // Output: col 0 eigenvalues, rest eigenvectors

    Ellipse calcEllipsePara(
            const Eigen::Matrix<T, Eigen::Dynamic, Eigen::Dynamic>& eigVV);

private:
    Eigen::Vector<T, Eigen::Dynamic> m_xData, m_yData;
};

#endif
