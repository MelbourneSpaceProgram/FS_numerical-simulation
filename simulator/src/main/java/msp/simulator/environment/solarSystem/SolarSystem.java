/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment.solarSystem;

import msp.simulator.utils.architecture.ClusterManager;
import msp.simulator.utils.logs.LogWriter;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class SolarSystem extends ClusterManager {

	/** Earth Instance of the Solar System in the simulation. */
	private Earth earth;
	
	/**
	 * 
	 */
	public SolarSystem(LogWriter simulatorLogWriter) {
		super(simulatorLogWriter);
		this.logWriter.printMsg("Building the Solar System...", this);
		
		this.earth = new Earth(simulatorLogWriter);
	}
	
	/**
	 * Return the Earth instance of the simulation.
	 * @return Earth instance of the simulator
	 * @see Earth
	 */
	public Earth getEarth() {
		return this.earth;
	}

}
