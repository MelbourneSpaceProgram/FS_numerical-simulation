/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.torques;

import org.orekit.propagation.integration.AdditionalEquations;
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
	
	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(Torques.class);
	
	/** Instance of Torque Provider of the Engine. */
	private TorqueProvider torqueProvider;
	
	/** Instance of the additional equation */
	private TorqueToSpinEquation dynamicTorqueEquation;
	
	/**
	 * Build the Main Torque Provider of the dynamic module.
	 * @param environment The Environment of Simulation
	 * @param satellite The Satellite in the simulation.
	 */
	public Torques (Environment environment, Satellite satellite) {
		logger.info(CustomLoggingTools.indentMsg(logger,
				"Building the Torque Engine..."));
		
		this.torqueProvider = new AutomaticManoeuvre(
				satellite.getAssembly().getStates().getInitialState().getDate());
		
		this.dynamicTorqueEquation = new TorqueToSpinEquation(this.torqueProvider);
	}
	
	/**
	 * Return the Additional Equation computing the acceleration rotation
	 * rate from the torque interaction on the satellite.
	 * @return The Additional Equation to link to the propagator
	 */
	public AdditionalEquations getTorqueToSpinEquation() {
		return this.dynamicTorqueEquation;
	}

	/**
	 * @return the torqueProvider
	 */
	public TorqueProvider getTorqueProvider() {
		return torqueProvider;
	}

}
