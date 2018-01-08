/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment;

import org.orekit.errors.OrekitException;

import msp.simulator.utils.architecture.ClusterManager;
import msp.simulator.utils.logs.LogWriter;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class Environment extends ClusterManager {
	
	/** Instance of the Solar System in the simulation. */
	private msp.simulator.environment.solarSystem.SolarSystem solarSystem;
	
	/** Instance of the Earth Atmosphere in the simulation. */
	@SuppressWarnings("unused")
	private msp.simulator.environment.atmosphere.Atmosphere atmosphere;
	
	/** Instance of the Orbit in the simulation. */
	@SuppressWarnings("unused")
	private msp.simulator.environment.orbit.Orbit orbit;
	
	/**
	 * Constructor of the Space Environment of the Simulation.
	 * @param simulatorLogMsg The logger of the simulator.
	 * @throws OrekitException
	 */
	public Environment(LogWriter simulatorLogMsg) throws OrekitException {
		super(simulatorLogMsg);
		this.logWriter.printMsg("Building the simulation Environment...", this);
		
		/* Building the Solar System. */
		this.solarSystem = new msp.simulator.environment.solarSystem.SolarSystem(
				this.logWriter);
		
		/* Building the Earth Atmosphere. */
		this.logWriter.printMsg("Building the Earth Atmosphere...", this);
		this.atmosphere = new msp.simulator.environment.atmosphere.Atmosphere(
				this.solarSystem.getEarth());
		
		/* Building the orbit. */
		//this.logWriter.printMsg("Building the orbit...", this);
		// this.orbit = new msp.simulator.environment.orbit.Orbit(this.orbit);
	}

}
