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

package msp.simulator.groundStation;

import java.nio.ByteBuffer;

import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;
import org.orekit.errors.OrekitException;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.OrbitType;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.time.AbsoluteDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
import msp.simulator.satellite.io.MemcachedRawTranscoder;
import msp.simulator.utils.logs.CustomLoggingTools;
import net.spy.memcached.MemcachedClient;

/**
 * Primary class managing the ground station segment of the numerical
 * simulator.
 * 
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class GroundStation {

	/* ******* Public Static Attributes ******* */

	/** Periodicity of the work of the ground station. Default value is 3 hours. 
	 * (in second) */
	public static long periodicityOfWork = 60 * 60 * 3;

	/* **************************************** */

	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(GroundStation.class);

	/** Instance of the satellite in the simulation: the ground station has access
	 * to the satellite status at any time. */
	private Satellite satellite;

	/** Buffer for the next deadline of the team. */
	private AbsoluteDate nextWorkingDate;

	/** Period of a working stage of the team. */
	private long periodOfWork;

	/**
	 * Simple constructor of the ground station.
	 */
	public GroundStation(Environment environment, Satellite satellite) {
		logger.info(CustomLoggingTools.indentMsg(logger, 
				"Building the Ground Station..."));

		this.satellite = satellite;
		this.nextWorkingDate = satellite.getStates().getInitialState().getDate();
		this.periodOfWork = periodicityOfWork;
	}

	/**
	 * Execute the mission of the ground station at the specified date.
	 * @param date The specified date.
	 */
	public void executeMission(AbsoluteDate date) {
		if (this.isTeamWorking(date)) {

			/* ***** Sending the updated TLE data to the satellite. *****/

			/* Extracting the current orbit of the satellite. */
			Orbit currentOrbit = this.satellite.getStates().getCurrentState().getOrbit();
			final KeplerianOrbit currentKeplerianOrbit = (KeplerianOrbit) 
					OrbitType.KEPLERIAN.convertType(currentOrbit);

			/** TODO: Update the balistic coefficient with the drag coefficient. */
			
			/* Current Approximations of the TLE. */
			int 		tleNumber 			= 1;
			String 	launchPiece 			= "A";
			int 		launchNumber 		= 1;
			double 	balisticCoeff 		= 0;
			char 	classification 			= 'U';
			int 		satelliteNumber 			= 1;
			double 	meanMotionFirstDer 		= 0;
			double 	meanMotionSecondDer 		= 0;
			int 		currentRevolutionNumber 	= 0;

			/* Build the TLE Element. */
			TLE tle = new TLE(
					satelliteNumber,
					classification,
					2018,
					launchNumber,
					launchPiece,
					TLE.SGP4,
					tleNumber,
					currentOrbit.getDate(),
					currentKeplerianOrbit.getKeplerianMeanMotion(), 
					meanMotionFirstDer, 
					meanMotionSecondDer,
					currentKeplerianOrbit.getE(), 
					MathUtils.normalizeAngle(currentOrbit.getI(), FastMath.PI),
					MathUtils.normalizeAngle(currentKeplerianOrbit.getPerigeeArgument(), FastMath.PI),
					MathUtils.normalizeAngle(currentKeplerianOrbit.getRightAscensionOfAscendingNode(), FastMath.PI),
					MathUtils.normalizeAngle(currentKeplerianOrbit.getMeanAnomaly(), FastMath.PI),
					currentRevolutionNumber,
					balisticCoeff
					);

			/* Export to MemCached. */
			try {
				if (this.satellite.getIO().isConnectedToMemCached()) {
					MemcachedClient memcached = this.satellite.getIO().getMemcached();

					/* Because the board required the TLE format and OreKit only retrieve
					 * the parameters on a space dynamic view, we need to parse the lines.
					 */

					/* Parsing line 1. */
					byte[] epoch = MemcachedRawTranscoder.toRawByteArray(
							Double.valueOf(tle.getLine1().substring(18, 32)));

					byte[] meanMotionFirstDerivative = MemcachedRawTranscoder.toRawByteArray(
							Double.valueOf(tle.getLine1().substring(33, 43)));

					byte[] meanMotionSecondDerivative = MemcachedRawTranscoder.toRawByteArray(
							Double.valueOf(
									tle.getLine1().substring(44, 50)
									+ "e" + tle.getLine1().charAt(51)
									));

					byte[] bStar = MemcachedRawTranscoder.toRawByteArray(
							Double.valueOf(
									tle.getLine1().substring(53, 59)
									+ "e" + tle.getLine1().charAt(60)
									));

					/* Parsing Line 2. */				
					byte[] inclination = MemcachedRawTranscoder.toRawByteArray(
							Double.valueOf(tle.getLine2().substring(8, 16)));

					byte[] raan = MemcachedRawTranscoder.toRawByteArray(
							Double.valueOf(tle.getLine2().substring(17, 25)));

					byte[] eccentricity1e7 = MemcachedRawTranscoder.toRawByteArray(
							Double.valueOf(tle.getLine2().substring(26, 33)));

					byte[] argPerigee = MemcachedRawTranscoder.toRawByteArray(
							Double.valueOf(tle.getLine2().substring(34, 42)));

					byte[] meanAnomaly = MemcachedRawTranscoder.toRawByteArray(
							Double.valueOf(tle.getLine2().substring(43, 51)));

					byte[] meanMotion = MemcachedRawTranscoder.toRawByteArray(
							Double.valueOf(tle.getLine2().substring(52, 63)));

					/* Exporting the values to MemCached. */
					memcached.set("Simulation_TLE_Mean_Motion_Second_Deriv"	, 0, meanMotionSecondDerivative);
					memcached.set("Simulation_TLE_Mean_Motion_First_Deriv"	, 0, meanMotionFirstDerivative);
					memcached.set("Simulation_TLE_Mean_Motion"				, 0, meanMotion);
					memcached.set("Simulation_TLE_Argument_Perigee"		, 0, argPerigee);
					memcached.set("Simulation_TLE_Eccentricity_1e7"		, 0, eccentricity1e7);
					memcached.set("Simulation_TLE_Mean_Anomaly"			, 0, meanAnomaly);
					memcached.set("Simulation_TLE_Inclination"			, 0, inclination);
					memcached.set("Simulation_TLE_Bstar"		, 0, bStar);
					memcached.set("Simulation_TLE_Epoch"		, 0, epoch);
					memcached.set("Simulation_TLE_Raan"		, 0, raan);


					/* Logging Information. */
					String tleUpdate =
							"Sending TLE data from ground station"
									+ "\n"
									+ "Date: " + date.toString()
									+ "\n"
									+ "TLE_Mean_Motion: "
									+ ByteBuffer.wrap(meanMotion).getDouble()
									+ "\n"
									+ "TLE_Mean_Motion_First_Deriv: "
									+ ByteBuffer.wrap(meanMotionFirstDerivative).getDouble()
									+ "\n"
									+ "TLE_Mean_Motion_Second_Deriv: "
									+ ByteBuffer.wrap(meanMotionSecondDerivative).getDouble()
									+ "\n"
									+ "TLE_Mean_Anomaly: " + 
									+ ByteBuffer.wrap(meanAnomaly).getDouble()
									+ "\n"
									+ "TLE_Inclination: " + 
									+ ByteBuffer.wrap(inclination).getDouble()
									+ "\n"
									+ "TLE_Raan: " + 
									+ ByteBuffer.wrap(raan).getDouble()
									+ "\n"
									+ "TLE_Bstar: " + 
									+ ByteBuffer.wrap(bStar).getDouble()
									+ "\n"
									+ "TLE_Epoch: " + 
									+ ByteBuffer.wrap(epoch).getDouble()
									+ "\n"
									+ "TLE_Eccentricity_1e7: " + 
									+ ByteBuffer.wrap(eccentricity1e7).getDouble()
									+ "\n"
									+ "TLE_Argument_Perigee: " + 
									+ ByteBuffer.wrap(argPerigee).getDouble()
									+ "\n"
									+ "Raw: " + tle.getLine1() 
									+ "\n"
									+ "Raw: " + tle.getLine2()
									;

					logger.info(tleUpdate);
				}
			} catch (OrekitException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Return the assertion if the team of the ground station is working
	 * at the given date.
	 * @param date The given date
	 * @return True if the team is working at this date, false otherwise.
	 */
	public boolean isTeamWorking(AbsoluteDate date) {
		boolean teamIsWorking = false;

		/* If the the working date is reached... */
		if (date.compareTo(this.nextWorkingDate) >= 0) {
			teamIsWorking = true;
			this.nextWorkingDate = this.nextWorkingDate.shiftedBy(this.periodOfWork);
		}

		return teamIsWorking;
	}

}
