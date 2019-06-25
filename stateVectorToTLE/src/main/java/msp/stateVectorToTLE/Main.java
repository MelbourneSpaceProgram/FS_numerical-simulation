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

import org.hipparchus.complex.Quaternion;
import org.hipparchus.geometry.euclidean.threed.Vector3D;

import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;
import org.orekit.errors.OrekitException;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.CartesianOrbit;
import org.orekit.orbits.CircularOrbit;
import org.orekit.orbits.OrbitType;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.PVCoordinates;
import org.orekit.time.TimeScalesFactory;
import org.orekit.data.DirectoryCrawler;
import org.orekit.data.DataProvidersManager;
import java.io.File;


/**
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 * @author Rowan SKEWES <rowan.skewes@gmail.com>
 *
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
          // The launch window opens 2019-06-27 16:27 Z+12 (NZT)
          // with separation predicted to be 2983 seconds after launch, or 49 minutes and 53 seconds
          ////////////////////
          final Vector3D initialPosition = new Vector3D(5151.1899e3, -1976.6508e3, 4022.4817e3); // m
          final Vector3D initialVelocity = new Vector3D(0.2142e3, 6.6444e3, 2.9896e3); // m/s
          final AbsoluteDate launchDate =
              new AbsoluteDate(2019, 06, 27, 5, 19, 53, TimeScalesFactory.getUTC());
          ////////////////////
          ////////////////////
          ////////////////////
          ////////////////////
          ////////////////////
          ////////////////////
          ////////////////////

          final PVCoordinates initialState = new PVCoordinates​(initialPosition, initialVelocity);
          final double muEarth = 3.986_004_415e14;
          final CartesianOrbit initialOrbit = new CartesianOrbit(initialState, FramesFactory.getTEME(), launchDate, muEarth);

    			final CircularOrbit currentCircularOrbit = (CircularOrbit)
    					OrbitType.CIRCULAR.convertType(initialOrbit);
    			final KeplerianOrbit currentKeplerianOrbit = (KeplerianOrbit)
    					OrbitType.KEPLERIAN.convertType(initialOrbit);

    			/** TODO: Update the balistic coefficient with the drag coefficient. */

    			/* Current Approximations of the TLE. */
    			int 		tleNumber                   = 1;
    			String 	launchPiece                 = "A";
    			int 		launchNumber                = 1;
          // BStar drag term taken from MAYA-1, a 1U subesat at ~400km altitude
          // and ~51 degrees inclination
          // TLE downloaded 2019-06-20-08:46 Z+10 (AEST):
          // MAYA-1
          // 1 43590U 98067PE  19170.49852205  .00008512  00000-0  10229-3 0  9997
          // 2 43590  51.6369 338.6737 0005848 155.4008 204.7266 15.61781407 48805
    			double 	bStar                       = .10229e-3;
    			char 	  classification              = 'U';
    			int 		satelliteNumber             = 1;
    			double 	meanMotionFirstDer          = 0;
    			double 	meanMotionSecondDer         = 0;
    			int 		currentRevolutionNumber     = 0;

    			/* Build the TLE Element. */
    			TLE tle = new TLE(
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
    					MathUtils.normalizeAngle(currentKeplerianOrbit.getRightAscensionOfAscendingNode(), FastMath.PI),
    					MathUtils.normalizeAngle(currentKeplerianOrbit.getMeanAnomaly(), FastMath.PI),
    					currentRevolutionNumber,
    					bStar
    					);

          System.out.println(tle);

      } catch (Exception e) {
          e.printStackTrace();
          return;
      }
	}
}
