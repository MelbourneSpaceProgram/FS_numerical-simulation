/* Copyright 2017-2018 Melbourne Space Program */

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
 * @author Florian CHAUBEYRE
 */
public class GroundStation {

	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(GroundStation.class);

	/** Instance of the satellite in the simulation: the ground station has access
	 * to the satellite status at any time. */
	private Satellite satellite;

	/**
	 * Simple constructor of the ground station.
	 */
	public GroundStation(Environment environment, Satellite satellite) {
		logger.info(CustomLoggingTools.indentMsg(logger, 
				"Building the Ground Station..."));

		this.satellite = satellite;
	}

	/**
	 * Execute the mission of the ground station.
	 */
	public void executeMission(AbsoluteDate date) {

		/* ***** Sending the updated TLE data to the satellite. *****/

		/* Extracting the current orbit of the satellite. */
		Orbit currentOrbit = this.satellite.getStates().getCurrentState().getOrbit();
		final KeplerianOrbit currentKeplerianOrbit = (KeplerianOrbit) 
				OrbitType.KEPLERIAN.convertType(currentOrbit);

		/* Current Approximations. */
		int satelliteNumber = 1;
		char classification = 'U';
		int launchNumber = 1;
		int tleNumber = 1;
		String launchPiece = "A";
		int currentRevolutionNumber = 0;
		double balisticCoeff = 0;
		double meanMotionFirstDer = 0;
		double meanMotionSecondDer = 0;

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

				byte[] eccentricity = MemcachedRawTranscoder.toRawByteArray(
						Double.valueOf(tle.getLine2().substring(26, 33)));

				byte[] argPerigee = MemcachedRawTranscoder.toRawByteArray(
						Double.valueOf(tle.getLine2().substring(34, 42)));

				byte[] meanAnomaly = MemcachedRawTranscoder.toRawByteArray(
						Double.valueOf(tle.getLine2().substring(43, 51)));

				byte[] meanMotion = MemcachedRawTranscoder.toRawByteArray(
						Double.valueOf(tle.getLine2().substring(52, 63)));

				/* Exporting the values to MemCached. */
				memcached.set("TLE_Mean_Motion", 0, meanMotion);
				memcached.set("TLE_Mean_Motion_First_Deriv", 0, meanMotionFirstDerivative);
				memcached.set("TLE_Mean_Motion_Second_Deriv", 0, meanMotionSecondDerivative);
				memcached.set("TLE_Mean_Anomaly", 0, meanAnomaly);
				memcached.set("TLE_Inclinaison", 0, inclination);
				memcached.set("TLE_Raan", 0, raan);
				memcached.set("TLE_Bstar", 0, bStar);
				memcached.set("TLE_Epoch", 0, epoch);
				memcached.set("TLE_Eccentricity", 0, eccentricity);
				memcached.set("TLE_Argument_Perigee", 0, argPerigee);

				
				System.out.println("-------------------------");
				
				System.out.println("TLE_Mean_Motion: " + 
						ByteBuffer.wrap((byte[]) memcached.get("TLE_Mean_Motion")).getDouble());
				System.out.println("TLE_Mean_Motion_First_Deriv: " + 
						ByteBuffer.wrap((byte[]) memcached.get("TLE_Mean_Motion_First_Deriv")).getDouble());
				System.out.println("TLE_Mean_Motion_Second_Deriv: " + 
						ByteBuffer.wrap((byte[]) memcached.get("TLE_Mean_Motion_Second_Deriv")).getDouble());
				System.out.println("TLE_Mean_Anomaly: " + 
						ByteBuffer.wrap((byte[]) memcached.get("TLE_Mean_Anomaly")).getDouble());
				System.out.println("TLE_Inclinaison: " + 
						ByteBuffer.wrap((byte[]) memcached.get("TLE_Inclinaison")).getDouble());
				System.out.println("TLE_Raan: " + 
						ByteBuffer.wrap((byte[]) memcached.get("TLE_Raan")).getDouble());
				System.out.println("TLE_Bstar: " + 
						ByteBuffer.wrap((byte[]) memcached.get("TLE_Bstar")).getDouble());
				System.out.println("TLE_Epoch: " + 
						ByteBuffer.wrap((byte[]) memcached.get("TLE_Epoch")).getDouble());
				System.out.println("TLE_Eccentricity: " + 
						ByteBuffer.wrap((byte[]) memcached.get("TLE_Eccentricity")).getDouble());
				System.out.println("TLE_Argument_Perigee: " + 
						ByteBuffer.wrap((byte[]) memcached.get("TLE_Argument_Perigee")).getDouble());
			
			}

			System.out.println(tle.getLine1());
			System.out.println(tle.getLine2());

			System.out.println("-------------------------");
			
		} catch (OrekitException e) {
			e.printStackTrace();
		}



	}

	/**
	 * Return the assertion of an update in the mission from the ground
	 * station.
	 * @return True if an update has been made from the ground station.
	 */
	public boolean teamHasWorked(AbsoluteDate date) {

		//AbsoluteDate initialDate = this.satellite.getStates()
		//		.getInitialState().getDate();

		//double timeFromLastUpdate = 

		return true;
	}

}
