/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.torques;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class is responsible to manage the torque classes
 * of the simulator and to provide the overall interaction
 * on the satellite - in the satellite frame - to the
 * dynamic engine.
 *
 * @author Florian CHAUBEYRE
 */
public class Torques {

	/* ******* Public Static Attributes ******* */

	/** Set the torque provider in use by the simulator. */
	public static TorqueProviderEnum activeTorqueProvider = TorqueProviderEnum.SCENARIO;

	/* **************************************** */

	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(Torques.class);

	/** Instance of Torque Provider. */
	private TorqueProvider torqueProvider;

	/**
	 * Build the Main Torque Provider of the dynamic module.
	 * @param environment The Environment of Simulation
	 * @param satellite The Satellite in the simulation.
	 */
	public Torques (Environment environment, Satellite satellite) {
		logger.info(CustomLoggingTools.indentMsg(logger,
				"Building the Torque Engine..."));

		/* Build the torque provider in use in the simulation. */
		switch (Torques.activeTorqueProvider) {
		case MEMCACHED:
			this.torqueProvider = new MemCachedTorqueProvider(satellite);
			break;
		case SCENARIO:
			this.torqueProvider = new TorqueOverTimeScenarioProvider(
					satellite.getAssembly().getStates().getInitialState().getDate());
			break;
		}

	}
	
	/**
	 * @return the torqueProvider
	 */
	public TorqueProvider getTorqueProvider() {
		return torqueProvider;
	}

}
