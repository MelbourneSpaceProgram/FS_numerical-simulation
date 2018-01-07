/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment.celestialBodies;

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
 * The "redefinition" is done for clarity purpose as every objects we 
 * use in the simulator are then explicitly stated. 
 * Thus this is a static class that does not handle any instantiation.
 * 
 * @see CelestialBodyFactory
 * 
 * @author Florian CHAUBEYRE
 */
public final class Earth {

	/** Earth Celestial Body. */
	private static CelestialBody earthCelestialBody = null;
	
	/** Body Shape of the Earth Celestial Body. */
	private static OneAxisEllipsoid earthBodyShape = null;
	
	/** Logger of the simulation. */
	private static LogWriter simulatorLogger = null;
	
	/**
	 * Constructor of the Earth Celestial Body.
	 * Be aware that this object is a singleton and therefore the constructor
	 * is private to avoid any instantiation.
	 */
	private Earth() {}
	
	public static void build(LogWriter simulatorLogger) {
		Earth.simulatorLogger.printMsg("Building the Earth Body...", new Earth());
		
		try {
			/* Linking the singleton. */
			Earth.earthCelestialBody = CelestialBodyFactory.getEarth();
			
			/* Building the shape of the Earth body. */
			Earth.earthBodyShape = new OneAxisEllipsoid (
				Constants.GRS80_EARTH_EQUATORIAL_RADIUS,
				Constants.GRS80_EARTH_FLATTENING,
				Earth.earthCelestialBody.getInertiallyOrientedFrame()
				);
	
		} catch (OrekitException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Return the singleton instance of the Earth.
	 * @return
	 */
	public static CelestialBody getCelestialBody() {
		return Earth.earthCelestialBody;
	}
	
	/**
	 * Return the Earth Body Shape as an OneAxisEllipsoid
	 * according to the GRS80 convention.
	 * @return OneAxisEllipsoid
	 */
	public static OneAxisEllipsoid getBodyShape() {
		return Earth.earthBodyShape;
	}

}
