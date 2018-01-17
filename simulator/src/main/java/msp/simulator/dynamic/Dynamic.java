/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.dynamic.forces.Forces;
import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class is the top class to handle all of the
 * dynamic processing in the simulation.
 * It includes all of the interactions, linear or 
 * rotational, between the environment and the satellite.
 *
 * @author Florian CHAUBEYRE
 */
public class Dynamic {
	
	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(Dynamic.class);

	/** Instance of Linear Forces Model of the dynamic. */
	private Forces forces;

	/**
	 * 
	 */
	public Dynamic(Environment environment, Satellite satellite) {
		Dynamic.logger.info(CustomLoggingTools.indentMsg(logger, 
				"Building the Model of Dynamic..."));

		this.forces = new Forces(environment, satellite);
	}

	/**
	 * @return the linear Forces instance of the dynamic module.
	 */
	public Forces getForces() {
		return forces;
	}

}
