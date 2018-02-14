/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.satellite.assembly.Assembly;
import msp.simulator.satellite.assembly.SatelliteStates;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class Satellite {

	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(Satellite.class);

	/** Instance of Assembly of the Satellite. */
	private Assembly assembly;

	/**
	 * Build the intance of the Satellite in the simulation.
	 * @param environment Instance of the Simulation
	 */
	public Satellite(Environment environment) {
		Satellite.logger.info(CustomLoggingTools.indentMsg(Satellite.logger,
				"Building the Satellite..."));

		/* Building the Assembly of the Satellite. */
		this.assembly = new Assembly(environment);
	//	this.guidance = new Guidance(environment, assembly);	
	}

	/**
	 * Return the assembly of the satellite.
	 * @return Assembly
	 */
	public Assembly getAssembly() {
		return this.assembly;
	}

	/**
	 * Return the States object of the satellite.
	 * @return SpacecraftState
	 */
	public SatelliteStates getStates() {
		return this.getAssembly().getStates();
	}

}
