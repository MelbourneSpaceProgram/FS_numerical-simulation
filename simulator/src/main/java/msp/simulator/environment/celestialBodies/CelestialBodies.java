/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment.celestialBodies;

import msp.simulator.utils.architecture.ClusterFactory;
import msp.simulator.utils.logs.LogWriter;

/**
 *
 * @author Florian CHAUBEYRE
 */
public final class CelestialBodies extends ClusterFactory {
	
	/**
	 * 
	 */
	public CelestialBodies(LogWriter simulatorLogWriter) {
		super(simulatorLogWriter);
		this.logWriter.printMsg("Building the Celestial Bodies...", this);
		
		Earth.build(super.logWriter);
	}

}
