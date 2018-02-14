/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.NumericalSimulator;
import msp.simulator.dynamic.propagation.Propagation;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * This class handles the user-configuration 
 * of the numerical simulator.
 * <p>
 * The configuration has to be set before any
 * simulation initialization and can be used
 * anywhere by the user as the provided method 
 * are static.
 * <p>
 * The configuration setting relies on the fact 
 * that any static attribute of a class is created 
 * prior to the  usual instanciation of the object.
 * So we can instanciate an object of the simulation
 * with different settings as soon as they are defined 
 * as public static.
 * <p>
 * Note the fact that those parameters are public only
 * influences the initialization and are not used within
 * the main processing.
 *
 * @author Florian CHAUBEYRE
 */
public class Dashboard {	
	
	/** Logger of the instance. */
	private static final Logger logger = LoggerFactory.getLogger(
			Dashboard.class);

	/** Set the Configuration of the Simulation to the default Settings. */
	public static void setDefaultConfiguration() {
		logger.info(CustomLoggingTools.indentMsg(logger, 
				"Setting Default Configuration..."));
		
		Dashboard.setTimeIntegrationStep(1.0);
		Dashboard.setSimulationDuration(100.0);
	}

	/**
	 * Set the time duration of the simulation to process.
	 * @param duration in seconds
	 */
	public static void setSimulationDuration(double duration) {
		NumericalSimulator.simulationDuration = duration ; /* s. */
	}
	
	/**
	 * Set the integration time step of the different integrators
	 * used on the simulation (Attitude and Main PVT)
	 * @param step in seconds
	 */
	public static void setTimeIntegrationStep(double step) {
		Propagation.integrationTimeStep = step;
	}

}
