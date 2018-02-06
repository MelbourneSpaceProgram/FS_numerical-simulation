/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite.assembly;

import org.orekit.attitudes.Attitude;
import org.orekit.orbits.Orbit;
import org.orekit.propagation.SpacecraftState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class SatelliteState extends SpacecraftState {

	/** Generated Serial Version UID. */
	private static final long serialVersionUID = 4915009976932582048L;

	/** Logger of the class */
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Build the Satellite State in Space to be propagated.
	 * This constructor should provide the initial state through
	 * the Orbit and the Attitude.
	 * 
	 * @param orbit Orbit followed by the satellite - Contains
	 * the initial position and speed
	 * @param attitude Initial attitude of the satellite
	 * @param mass Mass of the satellite
	 * @throws IllegalArgumentException
	 */
	public SatelliteState(Orbit orbit, Attitude attitude, double mass)
			throws IllegalArgumentException {
		super(orbit, attitude, mass);
		
		this.logger.info(CustomLoggingTools.indentMsg(this.logger,
				" -> Creating Initial State of the satellite: Success."));
	}

}
