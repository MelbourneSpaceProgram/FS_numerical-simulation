/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite;

import org.orekit.attitudes.Attitude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.orbit.Orbit;
import msp.simulator.environment.solarSystem.Sun;
import msp.simulator.satellite.assembly.Assembly;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class Satellite {

	/** Logger of the class */
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/** Instance of Assembly of the Satellite. */
	private Assembly assembly;
	
	/**
	 * Build the intance of the Satellite in the simulation.
	 * @param sun Sun instance in the simulation
	 */
	public Satellite(Orbit orbit, Attitude attitude, Sun sun) {
		this.logger.info(CustomLoggingTools.indentMsg(this.logger,
				"Building the Satellite..."));
		
		/* Building the Assembly of the Satellite. */
		this.assembly = new Assembly(orbit, attitude, sun);
	}
	
	/**
	 * Return the assembly of the satellite.
	 * @return Assembly
	 */
	public Assembly getAssembly() {
		return this.assembly;
	}

}
