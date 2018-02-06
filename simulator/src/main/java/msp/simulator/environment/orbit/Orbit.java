/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment.orbit;

import org.hipparchus.util.FastMath;
import org.orekit.errors.OrekitException;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.LOFType;
import org.orekit.frames.LocalOrbitalFrame;
import org.orekit.orbits.CircularOrbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.solarSystem.SolarSystem;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class represents an instance of an Earth orbit for
 * the simulation.
 * 
 * This class is directly related to OREKIT but for a better
 * understanding of the modules of the simulator, it is extended
 * here.<p>
 * It also provides some other methods and tools to the simulator.
 * 
 * @see org.orekit.orbits.Orbit
 *
 * @author Florian CHAUBEYRE
 */
public class Orbit extends CircularOrbit {

	/** Generated Serial Version UID. */
	private static final long serialVersionUID = 8798538622702226489L;
	
	/** Logger of the class. */
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/** Local Orbital Frame related to the instance of the orbit. */
	private LocalOrbitalFrame localOrbitalFrame;
	
	
	/** Creates a new instance of the orbit.<p>
	 * 
	 * This orbit is a Circular Orbit and is defined through the following
	 * parameter through superclass construction:
	 * 
	 * @param a  semi-major axis (m)
	 * @param ex e cos(ω), first component of circular eccentricity vector
	 * @param ey e sin(ω), second component of circular eccentricity vector
	 * @param i inclination (rad)
	 * @param raan right ascension of ascending node (Ω, rad)
	 * @param alpha  an + ω, mean, eccentric or true latitude argument (rad)
	 * @param type type of latitude argument
	 * @param frame the frame in which are defined the parameters
     * @param date date of the orbital parameters
     * @param mu central attraction coefficient (m³/s²)
	 * @throws OrekitException 
	 * @throws IllegalArgumentException 
     */
	public Orbit(SolarSystem solarSystem) throws IllegalArgumentException, OrekitException {
		super(	solarSystem.getEarth().getRadius() + 575000,	/* Semi-Major Axis */
				(double) 0.,								/* Eccentricity on X */
				(double) 0.,						 		/* Eccentricity on Y */
				(double) FastMath.toRadians(98),			/* Inclinaison */
				(double) FastMath.toRadians(269.939),	/* RAAN at the defined date */
				(double) FastMath.toRadians(0),		/* True lattitude at the date */
				PositionAngle.TRUE,					/* Type of angle for previous */	
				solarSystem.getIntertialFrame(),		/* Frame in use */
				new AbsoluteDate(
						"2018-12-21T22:23:00.000",	/* Orbit definition date */
						TimeScalesFactory.getUTC() 	/* Winter Solstice*/
						),
				solarSystem.getEarth().getAttractCoeffMu() 	/* Earth Attraction Coeff */
			);

		/* Creating the relating Local Orbital Frame. */
		this.createLOF(this);
		
		this.logger.info(CustomLoggingTools.indentMsg(this.logger, 
				"Building the Orbit : Success."));
	}
	
	/**
	 * This method creates an instance of the local orbital
	 * frame related to the instance of the orbit.
	 * The convention used is VNC:<p>
	 * - X aligned with velocity vector.<p>
	 * - Y aligned with orbit momentum.<p>
	 * - Z completes the frame.<p>
	 * 
	 * @see org.orekit.frames.LOFType.VNC
	 * @param orbit 
	 */
	private void createLOF(Orbit orbit) {
		this.localOrbitalFrame = new LocalOrbitalFrame (
				FramesFactory.getEME2000(),
				LOFType.VNC,
				orbit,
				"LOF");
	}
	
	/**
	 * Get the Local Orbital Frame related to the orbit.
	 * The convention used is VNC:<p>
	 * - Z completes the frame.<p>
	 * - X aligned with velocity vector.<p>
	 * - Y aligned with orbit momentum.<p>
	 * @return
	 */
	public LocalOrbitalFrame getLof() {
		return this.localOrbitalFrame;
	}
	
}