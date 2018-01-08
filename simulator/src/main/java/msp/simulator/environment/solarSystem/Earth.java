/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment.solarSystem;

import org.orekit.bodies.CelestialBody;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.utils.Constants;

import msp.simulator.utils.logs.LogWriter;

/**
 * This class represents the Earth in the simulation and provides access
 * and tools to the singleton instance created through OreKit.<p>
 * 
 * @see CelestialBodyFactory
 * 
 * @author Florian CHAUBEYRE
 */
public class Earth {

	/** Earth Celestial Body. */
	private CelestialBody earthCelestialBody = null;
	
	/** Body Shape of the Earth Celestial Body. */
	private OneAxisEllipsoid earthBodyShape = null;
	
	/** Logger of the simulation. */
	private LogWriter simulatorLogger = null;
	
	
	public Earth(LogWriter simulatorLogger) {
		this.simulatorLogger = simulatorLogger;		
		this.simulatorLogger.printMsg("Building the Earth...", this);
		try {
			/* Linking the singleton. */
			this.earthCelestialBody = CelestialBodyFactory.getEarth();
			
			/* Building the shape of the Earth body. */
			this.earthBodyShape = new OneAxisEllipsoid (
				Constants.GRS80_EARTH_EQUATORIAL_RADIUS,
				Constants.GRS80_EARTH_FLATTENING,
				this.earthCelestialBody.getInertiallyOrientedFrame()
				);
	
		} catch (OrekitException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Return the singleton instance of the Earth.
	 * @return
	 */
	public CelestialBody getCelestialBody() {
		return this.earthCelestialBody;
	}
	
	/**
	 * Return the Earth Body Shape as an OneAxisEllipsoid
	 * according to the GRS80 convention.
	 * @return OneAxisEllipsoid
	 */
	public OneAxisEllipsoid getBodyShape() {
		return this.earthBodyShape;
	}

}
