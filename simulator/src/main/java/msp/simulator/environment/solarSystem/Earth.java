/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment.solarSystem;

import org.orekit.bodies.CelestialBody;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.frames.FactoryManagedFrame;
import org.orekit.frames.FramesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinatesProvider;
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
	private CelestialBody celestialBody = null;
	
	/** Earth-centered and rotating frame. */
	private FactoryManagedFrame rotatingFrame;
	
	/** Body Shape of the Earth Celestial Body. */
	private OneAxisEllipsoid ellipsoid = null;
	
	/** The Earth Radius. */
	private double radius;
	
	/** Earth Attraction Coefficient. ~ 3.986e14 m³/s² */
	private double attractCoeff_mu;
	
	
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
			/* Building the shape of the Earth body. 	*/
			
			/*  -> Celestial Body: linking the singleton.*/
			this.celestialBody = CelestialBodyFactory.getEarth();
			
			/*  -> Attraction Coefficient mu				*/
			this.attractCoeff_mu = Constants.WGS84_EARTH_MU;
			
			/*	-> Radius								*/
			this.radius = Constants.WGS84_EARTH_EQUATORIAL_RADIUS;
			
			/*  -> Earth Rotating Frame. */
			this.rotatingFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
			
			/*	-> Ellipsoid								*/
			this.ellipsoid = new OneAxisEllipsoid (
				this.radius,
				Constants.WGS84_EARTH_FLATTENING,
				this.rotatingFrame
					);
		} catch (OrekitException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Return the singleton instance of the Earth.
	 * @return Earth celestial body
	 */
	public CelestialBody getCelestialBody() {
		return this.celestialBody;
	}
	
	/**
	 * Return the Earth body as a OneAxisEllipsoid
	 * according to the defined convention. (e.g. WGS84...)
	 * @return OneAxisEllipsoid
	 */
	public OneAxisEllipsoid getEllipsoid() {
		return this.ellipsoid;
	}
	
	/**
	 * Return the radius of the Earth body
	 * following the same shape model. (e.g. WGS84...)
	 * 
	 * @return The Earth radius in m.
	 */
	public double getRadius() {
		return this.radius;
	}
	
	/**
	 * Return the Attraction Coefficient of the Earth
	 * following the defined model. (e.g. WGS84...) 
	 * (~ 3.986004418e14 m³/s²)
	 * 
	 * @return Attraction Coefficient 
	 */
	public double getAttractCoeffMu() {
		return this.attractCoeff_mu;
	}
	
	/**
	 * Return the Earth-centered rotating frame.<p>
	 * FramesFactory.getITRF(IERSConventions.IERS_2010, true)
	 * @return ITRF Frame from IERS 2010 Convention
	 */
	public FactoryManagedFrame getRotatingFrame() {
		return this.rotatingFrame;
	}
	
	public PVCoordinatesProvider getPvCoordinateProvider() {
		return (this.celestialBody) ;
	}
}
