/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment.solarSystem;

import org.orekit.bodies.CelestialBody;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.errors.OrekitException;
import org.orekit.utils.PVCoordinatesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class represents the Sun in the simulation and provides access
 * and tools to the singleton instance created through OreKit.<p>
 * 
 * @see CelestialBodyFactory
 * 
 * @author Florian CHAUBEYRE
 */
public class Sun {
	
	/** Logger instance for the Sun. */
	private final Logger logger;

	/** Celestial Body of the Sun instance. */
	private CelestialBody sunCelestialBody;
	
	
	/**
	 * Constructor of the Sun instance.<p>
	 * Keep in mind this is only a link to the singleton
	 * instance provided by  OreKit. So multiple instanciations
	 * will only point at the same object.
	 */
	public Sun() {
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.logger.info(CustomLoggingTools.indentMsg(this.logger,
				"-> Building the Sun..."));
		try {
			this.sunCelestialBody = CelestialBodyFactory.getSun();
		} catch (OrekitException e) {
			e.printStackTrace();
		}
	}
	
	public PVCoordinatesProvider getPvCoordinateProvider() {
		return ((PVCoordinatesProvider) this.sunCelestialBody) ;
	}

}
