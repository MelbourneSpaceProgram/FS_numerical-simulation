/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite.sensors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.satellite.assembly.Assembly;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class Sensors {
	
	/** Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(
			Sensors.class);
	
	/** Instance of environment of the simulation. */
	private Environment environment;
	
	/** Instance of the satellite assembly of the simulation. */
	private Assembly assembly;
	
	/* ***** Instances of the different sensors. ***** */
	
	/** Instance of Magnetometer in the simulation */
	private Magnetometer magnetometer;

	/**
	 * 
	 */
	public Sensors(Environment environment, Assembly assembly) {
		logger.info(CustomLoggingTools.indentMsg(logger,
				"Building the satellite Sensors..."));
		
		/* Linking the sensors class to the rest of the simulation. */
		this.environment = environment;
		this.assembly = assembly;
		
		/* Building the sensors. */
		this.magnetometer = new Magnetometer(
				this.environment,
				this.assembly
				);
	}

	
	/**
	 * @return the magnetometer
	 */
	public Magnetometer getMagnetometer() {
		return magnetometer;
	}

}
