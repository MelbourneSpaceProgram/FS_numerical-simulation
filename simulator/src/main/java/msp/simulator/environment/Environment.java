/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment;

import org.orekit.errors.OrekitException;

import msp.simulator.utils.architecture.ClusterFactory;
import msp.simulator.utils.logs.LogWriter;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class Environment extends ClusterFactory {
	
	/** Instance of the Celestial Bodies in the simulation. */
	@SuppressWarnings("unused")
	private msp.simulator.environment.celestialBodies.CelestialBodies celestialBodies;
	
	/** Instance of the Earth Atmosphere in the simulation. */
	@SuppressWarnings("unused")
	private msp.simulator.environment.atmosphere.Atmosphere atmosphere;
	
	/** Instance of the Orbit in the simulation. */
	@SuppressWarnings("unused")
	private msp.simulator.environment.orbit.Orbit orbit;
	
	
	public Environment(LogWriter simulatorLogMsg) throws OrekitException {
		super(simulatorLogMsg);
		
		/* Building the Celestial Bodies. */
		this.celestialBodies = new msp.simulator.environment.
				celestialBodies.CelestialBodies(super.logWriter);
		
		/* Building the Earth Atmosphere. */
		this.logWriter.printMsg("Building the Earth Atmosphere...", this);
		this.atmosphere = new msp.simulator.environment.atmosphere.Atmosphere();
		
		/* Building the orbit. */
		//this.logWriter.printMsg("Building the orbit...", this);
		// this.orbit = new msp.simulator.environment.orbit.Orbit(this.orbit);
	}

}
