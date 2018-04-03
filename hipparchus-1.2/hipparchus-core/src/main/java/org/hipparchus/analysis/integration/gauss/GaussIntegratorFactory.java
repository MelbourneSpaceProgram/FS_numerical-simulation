/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This is not the original file distributed by the Apache Software Foundation
 * It has been modified by the Hipparchus project
 */
package org.hipparchus.analysis.integration.gauss;

import java.math.BigDecimal;

import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.util.Pair;

/**
 * Class that provides different ways to compute the nodes and weights to be
 * used by the {@link GaussIntegrator Gaussian integration rule}.
 */
public class GaussIntegratorFactory {
    /** Generator of Gauss-Legendre integrators. */
    private final BaseRuleFactory<Double> legendre = new LegendreRuleFactory();
    /** Generator of Gauss-Legendre integrators. */
    private final BaseRuleFactory<BigDecimal> legendreHighPrecision = new LegendreHighPrecisionRuleFactory();
    /** Generator of Gauss-Hermite integrators. */
    private final BaseRuleFactory<Double> hermite = new HermiteRuleFactory();
    /** Generator of Gauss-Laguerre integrators. */
    private final BaseRuleFactory<Double> laguerre = new LaguerreRuleFactory();

    /**
     * Creates a Gauss-Laguerre integrator of the given order.
     * The call to the
     * {@link GaussIntegrator#integrate(org.hipparchus.analysis.UnivariateFunction)
     * integrate} method will perform an integration on the interval
     * \([0, +\infty)\): the computed value is the improper integral of
     * \(e^{-x} f(x)\)
     * where \(f(x)\) is the function passed to the
     * {@link SymmetricGaussIntegrator#integrate(org.hipparchus.analysis.UnivariateFunction)
     * integrate} method.
     *
     * @param numberOfPoints Order of the integration rule.
     * @return a Gauss-Legendre integrator.
     */
    public GaussIntegrator laguerre(int numberOfPoints) {
        return new GaussIntegrator(getRule(laguerre, numberOfPoints));
    }

    /**
     * Creates a Gauss-Legendre integrator of the given order.
     * The call to the
     * {@link GaussIntegrator#integrate(org.hipparchus.analysis.UnivariateFunction)
     * integrate} method will perform an integration on the natural interval
     * {@code [-1 , 1]}.
     *
     * @param numberOfPoints Order of the integration rule.
     * @return a Gauss-Legendre integrator.
     */
    public GaussIntegrator legendre(int numberOfPoints) {
        return new GaussIntegrator(getRule(legendre, numberOfPoints));
    }

    /**
     * Creates a Gauss-Legendre integrator of the given order.
     * The call to the
     * {@link GaussIntegrator#integrate(org.hipparchus.analysis.UnivariateFunction)
     * integrate} method will perform an integration on the given interval.
     *
     * @param numberOfPoints Order of the integration rule.
     * @param lowerBound Lower bound of the integration interval.
     * @param upperBound Upper bound of the integration interval.
     * @return a Gauss-Legendre integrator.
     * @throws MathIllegalArgumentException if number of points is not positive
     */
    public GaussIntegrator legendre(int numberOfPoints,
                                    double lowerBound,
                                    double upperBound)
        throws MathIllegalArgumentException {
        return new GaussIntegrator(transform(getRule(legendre, numberOfPoints),
                                             lowerBound, upperBound));
    }

    /**
     * Creates a Gauss-Legendre integrator of the given order.
     * The call to the
     * {@link GaussIntegrator#integrate(org.hipparchus.analysis.UnivariateFunction)
     * integrate} method will perform an integration on the natural interval
     * {@code [-1 , 1]}.
     *
     * @param numberOfPoints Order of the integration rule.
     * @return a Gauss-Legendre integrator.
     * @throws MathIllegalArgumentException if number of points is not positive
     */
    public GaussIntegrator legendreHighPrecision(int numberOfPoints)
        throws MathIllegalArgumentException {
        return new GaussIntegrator(getRule(legendreHighPrecision, numberOfPoints));
    }

    /**
     * Creates an integrator of the given order, and whose call to the
     * {@link GaussIntegrator#integrate(org.hipparchus.analysis.UnivariateFunction)
     * integrate} method will perform an integration on the given interval.
     *
     * @param numberOfPoints Order of the integration rule.
     * @param lowerBound Lower bound of the integration interval.
     * @param upperBound Upper bound of the integration interval.
     * @return a Gauss-Legendre integrator.
     * @throws MathIllegalArgumentException if number of points is not positive
     */
    public GaussIntegrator legendreHighPrecision(int numberOfPoints,
                                                 double lowerBound,
                                                 double upperBound)
        throws MathIllegalArgumentException {
        return new GaussIntegrator(transform(getRule(legendreHighPrecision, numberOfPoints),
                                             lowerBound, upperBound));
    }

    /**
     * Creates a Gauss-Hermite integrator of the given order.
     * The call to the
     * {@link SymmetricGaussIntegrator#integrate(org.hipparchus.analysis.UnivariateFunction)
     * integrate} method will perform a weighted integration on the interval
     * \([-\infty, +\infty]\): the computed value is the improper integral of
     * \(e^{-x^2}f(x)\)
     * where \(f(x)\) is the function passed to the
     * {@link SymmetricGaussIntegrator#integrate(org.hipparchus.analysis.UnivariateFunction)
     * integrate} method.
     *
     * @param numberOfPoints Order of the integration rule.
     * @return a Gauss-Hermite integrator.
     */
    public SymmetricGaussIntegrator hermite(int numberOfPoints) {
        return new SymmetricGaussIntegrator(getRule(hermite, numberOfPoints));
    }

    /**
     * @param factory Integration rule factory.
     * @param numberOfPoints Order of the integration rule.
     * @return the integration nodes and weights.
     * @throws MathIllegalArgumentException if number of points is not positive
     * @throws MathIllegalArgumentException if the elements of the rule pair do not
     * have the same length.
     */
    private static Pair<double[], double[]> getRule(BaseRuleFactory<? extends Number> factory,
                                                    int numberOfPoints)
        throws MathIllegalArgumentException {
        return factory.getRule(numberOfPoints);
    }

    /**
     * Performs a change of variable so that the integration can be performed
     * on an arbitrary interval {@code [a, b]}.
     * It is assumed that the natural interval is {@code [-1, 1]}.
     *
     * @param rule Original points and weights.
     * @param a Lower bound of the integration interval.
     * @param b Lower bound of the integration interval.
     * @return the points and weights adapted to the new interval.
     */
    private static Pair<double[], double[]> transform(Pair<double[], double[]> rule,
                                                      double a,
                                                      double b) {
        final double[] points = rule.getFirst();
        final double[] weights = rule.getSecond();

        // Scaling
        final double scale = (b - a) / 2;
        final double shift = a + scale;

        for (int i = 0; i < points.length; i++) {
            points[i] = points[i] * scale + shift;
            weights[i] *= scale;
        }

        return new Pair<double[], double[]>(points, weights);
    }
}