/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment.solarSystem;

import org.orekit.bodies.CelestialBody;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.errors.OrekitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class represents the Earth in the simulation and provides access
 * and tools to the singleton instance created through OreKit.<p>
 * 
 * @see CelestialBodyFactory
 * @author Florian CHAUBEYRE
 */
public class Moon {
	
	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(Moon.class);

	/** Moon Celestial Body. */
	private CelestialBody moonCelestialBody = null;
	
	public Moon() {
		Moon.logger.info(CustomLoggingTools.indentMsg(Moon.logger,
				"-> Building the Moon..."));
	
		try {
			this.moonCelestialBody = CelestialBodyFactory.getMoon();
			
		} catch (OrekitException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Moon Celestial Body
	 */
	public CelestialBody getMoonCelestialBody() {
		return moonCelestialBody;
	}

}
