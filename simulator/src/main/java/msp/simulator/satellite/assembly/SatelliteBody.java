/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite.assembly;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.forces.BoxAndSolarArraySpacecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.solarSystem.Sun;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class SatelliteBody extends BoxAndSolarArraySpacecraft {

	/** Logger of the class */
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Build the Satellite Body as a CubeSat (Cube with no Solar Arrays)
	 * @param cubeLength
	 * @param Sun 
	 */
	public SatelliteBody(double cubeLength, Sun sun) {
		super(
				cubeLength,	/* X Length of Body */
				cubeLength,	/* Y Length of Body */
				cubeLength,	/* Z Length of Body */
				sun.getPvCoordinateProvider(), /* Sun Coordinate Provider */
				0, Vector3D.PLUS_I, 0, 0, 0 /* Solar Array Parameters: Zero */
				);
		this.logger.info(CustomLoggingTools.indentMsg(this.logger, 
				" -> Building the CubeSat body: Success."));
	}
}
