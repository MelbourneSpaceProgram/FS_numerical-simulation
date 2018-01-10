/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment;

import org.orekit.errors.OrekitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class Environment {
	
	/** Logger of the instance. */
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
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
	public Environment() throws OrekitException {
		this.logger.info(CustomLoggingTools.indentMsg(this.logger,
				"Building the Environent..."));
		
		/* Building the Solar System. */
		this.solarSystem = new msp.simulator.environment.solarSystem.SolarSystem();
		
		/* Building the Earth Atmosphere. */
		this.atmosphere = new msp.simulator.environment.atmosphere.Atmosphere(
				this.solarSystem.getEarth(),
				this.solarSystem.getSun());
		
		/* Building the orbit. */
		//this.logWriter.printMsg("Building the orbit...", this);
		// this.orbit = new msp.simulator.environment.orbit.Orbit(this.orbit);
	}

}
