/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment.atmosphere;

import org.orekit.errors.OrekitException;
import org.orekit.forces.drag.atmosphere.HarrisPriester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.solarSystem.Earth;
import msp.simulator.environment.solarSystem.Sun;
import msp.simulator.utils.logs.CustomLoggingTools;

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
	
	/** Logger of the instance of the class. */
	private final static Logger logger = LoggerFactory.getLogger(Atmosphere.class);
	
	/**
	 * Constructor of the instance of Earth atmosphere.
	 * @param earth The Earth Instance of the simulation.
	 * @param sun The Sun Instance of the simulation.
	 * 
	 * @throws OrekitException If OreKit initialization fails
	 */
	public Atmosphere(Earth earth, Sun sun) throws OrekitException {
		
		/* Harris-Priester Model 
		 * - Sun Coordinate Provider
		 * - Earth One Axis Ellipsoid
		 * - Cosine Exponent : 2 (low inclinaison) to 6 (Polar OrbitWrapper)
		 */
		super(	sun.getPvCoordinateProvider(),
				earth.getEllipsoid(),
				5		/* Arbitrary set. */
				);
		
		logger.info(CustomLoggingTools.indentMsg(logger, 
				"Building the Earth Atmosphere: success."));
	}
	
}
