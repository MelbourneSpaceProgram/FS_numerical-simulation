/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite.actuators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class Actuators {
	
	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(Actuators.class);

	/**
	 * 
	 */
	public Actuators() {
		logger.info(CustomLoggingTools.indentMsg(logger,
				"Building the Actuators..."));
	}

}
