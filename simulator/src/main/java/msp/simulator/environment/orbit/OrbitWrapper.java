/* Copyright 20017-2018 Melbourne Space Program
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * understanding of the modules of the simulator, it is reproduced
 * here.
 * <p>
 * It also provides some other methods and tools to the simulator.
 * 
 * @see org.orekit.orbits.Orbit
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class OrbitWrapper {


	/* ******* Public Static Attributes ******* */

	/** Orbital parameters in use for the construction of the orbit in the simulation. */
	public static OrbitalParameters userOrbitalParameters = new OrbitWrapper.OrbitalParameters();

	/* **************************************** */


	/** Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(
			OrbitWrapper.class);

	/** Instance of the orbit in the simulation. */
	private CircularOrbit orbit;

	/**
	 * This embedded class is used to build the orbit of the simulation.
	 * It consists basically in the main orbital parameters and the date
	 * of definition.
	 * This class also enables to change the default orbit with some user
	 * defined specific orbit.
	 *
	 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
	 */
	public static class OrbitalParameters {
		protected double altitude = 575000;
		protected double ex = 0;
		protected double ey = 0;
		protected double i = FastMath.toRadians(98);
		protected double raan = FastMath.toRadians(269.939);
		protected double trueLatitude = FastMath.toRadians(0);
		protected String dateUtc = "2018-12-21T22:23:00.000";

		/** Default Constructor with default values. */
		public OrbitalParameters() {}

		/** Explicit constructor for specific user-defined orbit at the
		 * defined date.
		 * 
		 * @param altitude in meter
		 * @param ex Eccentricity on X
		 * @param ey Eccentricity on Y
		 * @param i Inclinaison
		 * @param raan Right ascension of the ascending node
		 * @param trueLatitude true latitude
		 * @param dateUtc String format of the date, e.g. "2018-12-21T22:23:00.000"
		 */
		public OrbitalParameters(
				double altitude, 
				double ex, double ey, 
				double i, double raan,
				double trueLatitude, String dateUtc) {
			this.altitude = altitude;
			this.ex = ex;
			this.ey = ey;
			this.i = i;
			this.raan = raan;
			this.trueLatitude = trueLatitude;
			this.dateUtc = dateUtc;
		}
	}

	/** Local Orbital Frame related to the instance of the orbit. */
	private LocalOrbitalFrame localOrbitalFrame;	

	/**
	 * Create the instance of OrbitWrapper in the simulation.
	 * @param solarSystem instance in the simulation
	 * @throws IllegalArgumentException if eccentricity is s. superior to 1
	 * @throws OrekitException if OreKit initialization failed
	 * @see CircularOrbit
	 */
	public OrbitWrapper(SolarSystem solarSystem) throws IllegalArgumentException, OrekitException {
		logger.info(CustomLoggingTools.indentMsg(logger, 
				"Building the OrbitWrapper..."));
		
		this.orbit = new CircularOrbit(
				solarSystem.getEarth().getRadius() 
				+ userOrbitalParameters.altitude,		/* Semi-Major Axis */
				userOrbitalParameters.ex,				/* Eccentricity on X */
				userOrbitalParameters.ey,				/* Eccentricity on Y */
				userOrbitalParameters.i,					/* Inclinaison */
				userOrbitalParameters.raan,				/* RAAN at the defined date */
				userOrbitalParameters.trueLatitude,		/* True lattitude at the date */
				PositionAngle.TRUE,						/* Type of angle for previous */	
				solarSystem.getIntertialFrame(),			/* Frame in use */
				new AbsoluteDate(
						userOrbitalParameters.dateUtc,	/* OrbitWrapper definition date */
						TimeScalesFactory.getUTC() 		/* Winter Solstice*/
						),
				solarSystem.getEarth().getAttractCoeffMu() 	/* Earth Attraction Coeff */
				);

		/* Creating the relating Local Orbital Frame. */
		this.createLOF(this);
	}

	/**
	 * This method creates an instance of the local orbital
	 * frame related to the instance of the orbit.
	 * The convention used is VNC:<p>
	 * - X aligned with velocity vector.<p>
	 * - Y aligned with orbit momentum.<p>
	 * - Z completes the frame.
	 * 
	 * @see org.orekit.frames.LOFType
	 * @param orbit Instance of the Simulation
	 */
	private void createLOF(OrbitWrapper orbit) {
		this.localOrbitalFrame = new LocalOrbitalFrame (
				FramesFactory.getEME2000(),
				LOFType.VNC,
				this.orbit,
				"LOF");
	}
	
	/**
	 * This method clones the given orbit in order to change the time
	 * stamped data.
	 * @param orbit Orbit to clone
	 * @param newDate New time stamped date
	 * @return A new orbit object 
	 */
	public static CircularOrbit clone(CircularOrbit orbit, AbsoluteDate newDate) {
		CircularOrbit timeUpdatedOrbit = new CircularOrbit (
				orbit.getA(),
				orbit.getCircularEx(),
				orbit.getCircularEy(),
				orbit.getI(),
				orbit.getRightAscensionOfAscendingNode(),
				orbit.getAlpha(PositionAngle.TRUE),
				orbit.getADot(),
				orbit.getCircularExDot(),
				orbit.getCircularEyDot(),
				orbit.getIDot(),
				orbit.getRightAscensionOfAscendingNodeDot(),
				orbit.getAlphaDot(PositionAngle.TRUE),
				PositionAngle.TRUE,
				orbit.getFrame(),
				newDate,
				orbit.getMu()
				);
		
		return timeUpdatedOrbit;
	}
	
	
	/** Get the OrbitWrapper of the simulation.
	 * @return orbit instance
	 */
	public CircularOrbit getOrbit() {
		return this.orbit;
	}
	
	/**
	 * Get the Local Orbital Frame related to the orbit.
	 * The convention used is VNC:<p>
	 * - Z completes the frame.<p>
	 * - X aligned with velocity vector.<p>
	 * - Y aligned with orbit momentum.
	 * @return LocalOrbitalFrame of the defined orbit
	 */
	public LocalOrbitalFrame getLof() {
		return this.localOrbitalFrame;
	}

}