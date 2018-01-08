/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment.atmosphere;

import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.errors.OrekitException;
import org.orekit.forces.drag.atmosphere.HarrisPriester;
import org.orekit.utils.PVCoordinatesProvider;

import msp.simulator.environment.solarSystem.Earth;

/**
 * This class represents the atmosphere of the earth used
 * in the numerical simulator.<p>
 * 
 * The current instance relies on the Harris-Priester model.
 * This one is static and do not need any daily additionnal data.<p>
 * 
 * This class is directly related to OREKIT but for a better
 * understanding of the modules of the simulator, it is extended
 * here.<p>
 * It also provides some other methods and tools to the simulator.
 * 
 * @see org.orekit.forces.drag.atmosphere.HarrisPriester
 * @author Florian CHAUBEYRE
 */
public final class Atmosphere extends HarrisPriester {

	/** Generated Serial Version UID. */
	private static final long serialVersionUID = -1497026274240688235L;
	
	/**
	 * Constructor of the instance of atmosphere.
	 * @param earth The Earth Instance of the simulation.
	 * 
	 * @throws OrekitException 
	 */
	public Atmosphere(Earth earth) throws OrekitException {
		
		/* Harris-Priester Model 
		 * - Sun Coordinate Provider
		 * - Earth One Axis Ellipsoid
		 * - Cosine Exponent : 2 (low inclinaison) to 6 (Polar Orbit)
		 */
		super(	(PVCoordinatesProvider) 	CelestialBodyFactory.getSun(),
				earth.getBodyShape(),
				/* Cosine Exponent */ 5
				);
	}
	
}
