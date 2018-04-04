/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.groundStation;

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
import msp.simulator.utils.logs.CustomLoggingTools;

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
		double meanMotionFirstDerivative = 0;
		double meanMotionSecondDerivative = 0;
		
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
				meanMotionFirstDerivative, 
				meanMotionSecondDerivative,
				currentKeplerianOrbit.getE(), 
				MathUtils.normalizeAngle(currentOrbit.getI(), FastMath.PI),
				MathUtils.normalizeAngle(currentKeplerianOrbit.getPerigeeArgument(), FastMath.PI),
				MathUtils.normalizeAngle(currentKeplerianOrbit.getRightAscensionOfAscendingNode(), FastMath.PI),
				MathUtils.normalizeAngle(currentKeplerianOrbit.getMeanAnomaly(), FastMath.PI),
				currentRevolutionNumber,
				balisticCoeff
				);

		if (this.satellite.getIO().isConnectedToMemCached()) {
			
		}

		try {
			System.out.println(tle.getLine1());
			System.out.println(tle.getLine2());
			
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
