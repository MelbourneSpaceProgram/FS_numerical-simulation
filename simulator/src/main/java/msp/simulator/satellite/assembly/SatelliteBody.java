/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite.assembly;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.forces.BoxAndSolarArraySpacecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class SatelliteBody extends BoxAndSolarArraySpacecraft {

	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(SatelliteBody.class);
	
	/**
	 * Build the Satellite Body as a CubeSat (Cube with no Solar Arrays)
	 * sensitive to drag and radiation.
	 * @param cubeLength
	 * @param Sun 
	 */
	public SatelliteBody(Environment environment) {
		super(
				Assembly.cs1_Length,	/* X Length of Body */
				Assembly.cs1_Length,	/* Y Length of Body */
				Assembly.cs1_Length,	/* Z Length of Body */
				environment.getSolarSystem().getSun()
				.getPvCoordinateProvider(), /* Sun Coordinate Provider */
				0, Vector3D.PLUS_I, 0, 0, 0 /* Solar Array Parameters: Zero */
				);
		SatelliteBody.logger.info(CustomLoggingTools.indentMsg(SatelliteBody.logger, 
				" -> Building the CubeSat body: Success."));
	}
}
