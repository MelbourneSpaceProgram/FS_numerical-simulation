/* Copyright 20017-2018 Melbourne Space Program
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package msp.simulator;

import java.io.File;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;
import org.orekit.bodies.CelestialBody;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.forces.drag.DragForce;
import org.orekit.forces.drag.IsotropicDrag;
import org.orekit.forces.drag.atmosphere.HarrisPriester;
import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.orekit.forces.gravity.ThirdBodyAttraction;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.forces.radiation.IsotropicRadiationSingleCoefficient;
import org.orekit.forces.radiation.RadiationSensitive;
import org.orekit.forces.radiation.SolarRadiationPressure;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.Transform;
import org.orekit.orbits.CartesianOrbit;
import org.orekit.orbits.CircularOrbit;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.OrbitType;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.conversion.FiniteDifferencePropagatorConverter;
import org.orekit.propagation.conversion.TLEPropagatorBuilder;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinates;

/**
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 * @author Rowan SKEWES <rowan.skewes@gmail.com> Code for fitting the TLE to the propagated state is
 *     mostly taken from Orekit forum discussion post by leonardo.andreasi
 *     https://forum.orekit.org/t/conversion-of-tle/375
 */
public class Main {

  public static void main(String[] args) {

    try {
      // Get TimeScalesFactory to find the data it needs to use UTC
      File orekitData = new File("src/main/resources/orekit-data");
      DataProvidersManager manager = DataProvidersManager.getInstance();
      manager.addProvider(new DirectoryCrawler(orekitData));

      ////////////////////
      ////////////////////
      ////////////////////
      // Pre-launch state vector at ACRUX-1 separation, received June 10 2019
      ////////////////////
      // The launch window opens 2019-06-27 16:30 Z+12 (NZT)
      // with separation predicted to be 2983 seconds after launch, or 49 minutes and 53 seconds
      ////////////////////

      final Vector3D initialPositionItrf =
          new Vector3D(5151.1899e3, -1976.6508e3, 4022.4817e3); // m
      final Vector3D initialVelocityItrf = new Vector3D(0.2142e3, 6.6444e3, 2.9896e3); // m/s
      final AbsoluteDate launchDate =
          new AbsoluteDate(2019, 06, 27, 5, 19, 53, TimeScalesFactory.getUTC());

      // Old state vector
      // final Vector3D initialPositionItrf = new Vector3D(5149.3374e3, -1378.3296e3, 4264.4110e3);
      // // m
      // final Vector3D initialVelocityItrf = new Vector3D(-0.2632e3, 6.8362e3, 2.5275e3); // m/s
      // final AbsoluteDate launchDate =
      //   new AbsoluteDate(2019, 06, 25, 5, 17, 14, TimeScalesFactory.getUTC());

      // Cook it for low eccentricity
      // final Vector3D initialPositionItrf = new Vector3D(5151.1899e3, -1976.6508e3, 4022.4817e3);
      // // m
      // final Vector3D initialVelocityItrf = new Vector3D(0.2151e3, 6.6444e3, 2.9896e3); // m/s

      // Test state vector from UNSW
      // final Vector3D initialPositionItrf = new Vector3D(-3495.024522706921e3,
      // -5028.062565463392e3, 2934.830111996512e3); // m
      // final Vector3D initialVelocityItrf = new Vector3D(-2.707965441965147e3,
      // -2.1225833611150033e3, -6.850800844580249e3); // m/s
      // final AbsoluteDate launchDate =
      //    new AbsoluteDate(2019, 06, 17, 22, 0, 0, TimeScalesFactory.getUTC());

      // Other ACRUX-1 params
      double acrux1Weight = 0.969; // kg

      // BStar drag term taken from MAYA-1, a 1U subesat at ~400km altitude
      // and ~51 degrees inclination
      // TLE downloaded 2019-06-20-08:46 Z+10 (AEST):
      // MAYA-1
      // 1 43590U 98067PE  19170.49852205  .00008512  00000-0  10229-3 0  9997
      // 2 43590  51.6369 338.6737 0005848 155.4008 204.7266 15.61781407 48805
      double bStar = .10229e-3;

      ////////////////////
      ////////////////////
      ////////////////////
      ////////////////////
      ////////////////////
      ////////////////////
      ////////////////////

      // Convert ITRF (basically ECEF) to TEME
      final PVCoordinates initialStateItrf =
          new PVCoordinates(initialPositionItrf, initialVelocityItrf);

      final Transform itrfToTeme =
          FramesFactory.getITRF(IERSConventions.IERS_2010, false)
              .getTransformTo(FramesFactory.getTEME(), launchDate);

      final PVCoordinates initialStateTeme = itrfToTeme.transformPVCoordinates(initialStateItrf);

      final double muEarth = 3.986_004_415e14;
      final CartesianOrbit initialOrbit =
          new CartesianOrbit(initialStateTeme, FramesFactory.getTEME(), launchDate, muEarth);

      final CircularOrbit currentCircularOrbit =
          (CircularOrbit) OrbitType.CIRCULAR.convertType(initialOrbit);
      final KeplerianOrbit currentKeplerianOrbit =
          (KeplerianOrbit) OrbitType.KEPLERIAN.convertType(initialOrbit);

      /** TODO: Update the balistic coefficient with the drag coefficient. */

      /* Current Approximations of the TLE. */
      int tleNumber = 1;
      String launchPiece = "A";
      int launchNumber = 1;
      char classification = 'U';
      int satelliteNumber = 1;
      double meanMotionFirstDer = 0;
      double meanMotionSecondDer = 0;
      int currentRevolutionNumber = 0;

      /*  Build the TLE Element to start the numerical fit from */
      TLE inputTle =
          new TLE(
              satelliteNumber,
              classification,
              2018,
              launchNumber,
              launchPiece,
              TLE.SGP4,
              tleNumber,
              launchDate,
              currentKeplerianOrbit.getKeplerianMeanMotion(),
              meanMotionFirstDer,
              meanMotionSecondDer,
              currentKeplerianOrbit.getE(),
              MathUtils.normalizeAngle(currentCircularOrbit.getI(), FastMath.PI),
              MathUtils.normalizeAngle(currentKeplerianOrbit.getPerigeeArgument(), FastMath.PI),
              MathUtils.normalizeAngle(
                  currentKeplerianOrbit.getRightAscensionOfAscendingNode(), FastMath.PI),
              MathUtils.normalizeAngle(currentKeplerianOrbit.getMeanAnomaly(), FastMath.PI),
              currentRevolutionNumber,
              bStar);

      final double dP = 0.001;
      final double minStep = 0.001;
      final double maxStep = 500;
      final double initStep = 60;

      final double[][] tolerance =
          NumericalPropagator.tolerances(dP, initialOrbit, OrbitType.CARTESIAN);

      AdaptiveStepsizeIntegrator integrator =
          new DormandPrince853Integrator(minStep, maxStep, tolerance[0], tolerance[1]);

      integrator.setInitialStepSize(initStep);

      NumericalPropagator propagator = new NumericalPropagator(integrator);

      propagator.setInitialState(new SpacecraftState(initialOrbit, acrux1Weight));

      // Create Earth
      OneAxisEllipsoid earth =
          new OneAxisEllipsoid(
              Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
              Constants.WGS84_EARTH_FLATTENING,
              FramesFactory.getITRF(IERSConventions.IERS_2010, false));

      // Earth Geopotential
      NormalizedSphericalHarmonicsProvider provider =
          GravityFieldFactory.getNormalizedProvider(4, 0);
      HolmesFeatherstoneAttractionModel geopotentialForce =
          new HolmesFeatherstoneAttractionModel(earth.getBodyFrame(), provider);

      // Solar Radiation Pressure
      CelestialBody sun = CelestialBodyFactory.getSun();
      double solarRadiationPressureCoefficient = 1.4;
      double solarRadiationPressureArea = 10;
      RadiationSensitive radiationSensitive =
          new IsotropicRadiationSingleCoefficient(
              solarRadiationPressureArea, solarRadiationPressureCoefficient);
      SolarRadiationPressure srp =
          new SolarRadiationPressure(sun, earth.getEquatorialRadius(), radiationSensitive);

      // Third Bodies
      ThirdBodyAttraction thirdbodyMoon = new ThirdBodyAttraction(CelestialBodyFactory.getMoon());
      ThirdBodyAttraction thirdbodySun = new ThirdBodyAttraction(sun);

      // Drag
      HarrisPriester atmosphere = new HarrisPriester(sun, earth);
      double cd = 1.2;
      double area = 5;
      IsotropicDrag spacecraft = new IsotropicDrag(area, cd);
      DragForce drag = new DragForce(atmosphere, spacecraft);

      /*
       * Add force models
       */
      propagator.addForceModel(drag);
      propagator.addForceModel(geopotentialForce);
      propagator.addForceModel(srp);
      propagator.addForceModel(thirdbodyMoon);
      propagator.addForceModel(thirdbodySun);

      TLEPropagatorBuilder builder = new TLEPropagatorBuilder(inputTle, PositionAngle.TRUE, 1.0);

      FiniteDifferencePropagatorConverter tleFitter =
          new FiniteDifferencePropagatorConverter(builder, 1e-3, 1000);

      double duration = 86400.0;
      double stepSize = 300.0;
      Double points = duration / stepSize;

      System.out.println("START TLE PROPAGATOR FITTING");
      tleFitter.convert(propagator, duration, points.intValue(), TLEPropagatorBuilder.B_STAR);
      System.out.println("END TLE PROPAGATORFITTING");

      TLEPropagator propTLE = (TLEPropagator) tleFitter.getAdaptedPropagator();

      TLE outputTLE = propTLE.getTLE();

      System.out.println("INPUT TLE");
      System.out.println(inputTle);
      System.out.println("OUTPUT TLE");
      System.out.println(outputTLE);

    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
  }
}
