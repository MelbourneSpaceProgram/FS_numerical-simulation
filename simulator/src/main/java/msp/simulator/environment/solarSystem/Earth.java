/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment.solarSystem;

import org.orekit.bodies.CelestialBody;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class represents the Earth in the simulation and provides access
 * and tools to the singleton instance created through OreKit.<p>
 * 
 * @see CelestialBodyFactory
 * 
 * @author Florian CHAUBEYRE
 */
public class Earth {

	/** Logger of the Earth instance. */
	private final Logger logger;
	
	/* We do not extend a Celestial Body as it would imply a
	 * possible instantiation of several Earth bodies.
	 * In our case, if we still can instanciate several earth
	 * objects, they will be at least all linked to the same 
	 * singleton created by OreKit.
	 * 
	 * Another justification is that this instance is model-
	 * dependent, e.g. related here to the GRS80 Earth model.
	 */
	
	/** Earth Celestial Body. */
	private CelestialBody earthCelestialBody = null;
	
	/** Body Shape of the Earth Celestial Body. */
	private OneAxisEllipsoid earthEllipsoidBodyShape = null;
	
	/** The Earth Radius. */
	private double earthRadius = Constants.EGM96_EARTH_EQUATORIAL_RADIUS;
	
	/** Earth Attraction Coefficient. ~ 3.986e14 m³/s² */
	private double attractCoeff_mu = Constants.EGM96_EARTH_MU;
	
	/**
	 * Constructor of the Earth instance.<p>
	 * 
	 * Keep in mind this is only a link to the singleton
	 * instance provided by  OreKit. So multiple instanciations
	 * will only point at the same object.
	*/
	public Earth() {
		this.logger = LoggerFactory.getLogger(this.getClass());	
		this.logger.info(CustomLoggingTools.indentMsg(this.logger,
				"-> Building the Earth..."));
		
		try {
			/** Building the shape of the Earth body. 	**/
			
			/* Linking the singleton. */
			this.earthCelestialBody = CelestialBodyFactory.getEarth();
			
			/* Attraction Coefficient mu					*/
			this.attractCoeff_mu = Constants.EGM96_EARTH_MU;
			
			/*	-> Radius								*/
			this.earthRadius = Constants.WGS84_EARTH_EQUATORIAL_RADIUS;
			
			/*	-> Ellipsoid								*/
			this.earthEllipsoidBodyShape = new OneAxisEllipsoid (
				this.earthRadius,
				Constants.WGS84_EARTH_FLATTENING,
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
		return this.earthEllipsoidBodyShape;
	}
	
	/**
	 * Return the radius of the Earth body
	 * following the same shape model. (WGS84 ...)
	 * 
	 * @return The Earth radius in m.
	 */
	public double getRadius() {
		return this.earthRadius;
	}
	
	/**
	 * Return the Attraction Coefficient of the Earth
	 * following the EGM96 model. 
	 * (~ 3.986e14 m³/s²)
	 * 
	 * @return Attraction Coefficient 
	 */
	public double getAttractCoeffMu() {
		return this.attractCoeff_mu;
	}
}