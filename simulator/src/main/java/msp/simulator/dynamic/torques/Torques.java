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
	
	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(Torques.class);
	
	/** Instance of Torque Provider. */
	private TorqueProvider torqueProvider;
	
	/** Instance of the rotational acceleration Provider. */
	private RotAccelerationProvider rotAccProvider;
	
	/** Instance of the additional equations */
	private TorqueToSpinEquation torque2spinEquation;
	
	/**
	 * Build the Main Torque Provider of the dynamic module.
	 * @param environment The Environment of Simulation
	 * @param satellite The Satellite in the simulation.
	 */
	public Torques (Environment environment, Satellite satellite) {
		logger.info(CustomLoggingTools.indentMsg(logger,
				"Building the Torque Engine..."));
		
		//this.torqueProvider = new AutomaticTorqueLaw(
		//		satellite.getAssembly().getStates().getInitialState().getDate());
		
		this.torqueProvider = new MemCachedTorqueProvider(satellite);
		
		this.rotAccProvider = new RotAccelerationProvider(
				this.torqueProvider,
				satellite.getAssembly().getBody());
		
		this.torque2spinEquation = new TorqueToSpinEquation(
				rotAccProvider);
	}

	/**
	 * @return the torque2spinEquation
	 */
	public TorqueToSpinEquation getTorqueToSpinEquation() {
		return torque2spinEquation;
	}

	/**
	 * @return the torqueProvider
	 */
	public TorqueProvider getTorqueProvider() {
		return torqueProvider;
	}

	/**
	 * @return the rotAccProvider
	 */
	public RotAccelerationProvider getRotAccProvider() {
		return rotAccProvider;
	}

}
