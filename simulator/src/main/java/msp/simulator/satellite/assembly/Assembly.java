/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite.assembly;

import org.orekit.attitudes.Attitude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.orbit.Orbit;
import msp.simulator.environment.solarSystem.Sun;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class Assembly {

	/* **************************************************************	*/
	/* 				STATIC FIELD FOR CUBESAT PARAMETERS.				*/
	/* **************************************************************	*/
	
	/** Static field describing the length of the sides of the cube body. */
	private static double cubesatLength = 0.010 ; /* m */
	
	/** Static field describing the Satellite Body Mass. */
	private static double cubesatMass = 10 ; /* kg */
	
	/* **************************************************************	*/
	
	/** Logger of the class */
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/** Instance of the satellite body for the assembly. */
	private SatelliteBody satelliteBody;
	
	/** Instance of the satellite initial state in space. */
	private SatelliteState satelliteState;
	
	
	/**
	 * 
	 */
	public Assembly(Orbit orbit, Attitude attitude, Sun sun) {
		this.logger.info(CustomLoggingTools.indentMsg(this.logger,
				"Assembly in process..."));
		
		this.satelliteBody = new SatelliteBody(Assembly.cubesatLength, sun);
		this.satelliteState = new SatelliteState(orbit, attitude, Assembly.cubesatMass);
	}
	
	/**
	 * Return the satellite body as a CubeSat box sensitive
	 * to radiation and drag.
	 * @return BoxAndSolarArraySpaceCraft (DragSensitive, RadiationSensitive)
	 */
	public SatelliteBody getSatelliteBody() {
		return this.satelliteBody;
	}
	
	/**
	 * Return the satellite state in space.
	 * @return SpacecraftState
	 */
	public SatelliteState getSatelliteState() {
		return this.satelliteState;
	}

}
