/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite.assembly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
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
	public static double cs1_Length = 0.010; /* m */
	
	/** Static field describing the Satellite Body Mass. */
	public static double cs1_Mass = 10; /* kg */
	
	/** Static field describing the Satellite inertia. */
	/* TODO Inertia */
	public static double cs1_inertia = 1.0; /* m.s^2 */
	
	/* **************************************************************	*/
	
	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(Assembly.class);
	
	/** Instance of the satellite body for the assembly. */
	private SatelliteBody satelliteBody;
	
	/** Instance of the satellite initial state in space. */
	private SatelliteStates satelliteStates;
	
	/**
	 * Build the satellite as a body and a state vector.
	 * 
	 * @param environment Use to extract the Sun body to create a
	 * radiation sensitive satellite body.
	 */
	public Assembly(Environment environment) {
		Assembly.logger.info(CustomLoggingTools.indentMsg(Assembly.logger,
				"Assembly in process..."));
		
		this.satelliteBody = new SatelliteBody(environment);
		this.satelliteStates = new SatelliteStates(environment);
	}
	
	/**
	 * Return the satellite body as a CubeSat box sensitive
	 * to radiation and drag.
	 * @return BoxAndSolarArraySpaceCraft (DragSensitive, RadiationSensitive)
	 */
	public SatelliteBody getBody() {
		return this.satelliteBody;
	}
	
	/**
	 * Return the satellite state in space.
	 * @return SpacecraftState
	 */
	public SatelliteStates getStates() {
		return this.satelliteStates;
	}

}
