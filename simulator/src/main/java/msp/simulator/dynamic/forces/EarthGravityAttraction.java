/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.forces;

import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class EarthGravityAttraction extends HolmesFeatherstoneAttractionModel {

	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(
			EarthGravityAttraction.class);
	
	/**
	 * Construct the Earth Gravity Attraction Force Model.
	 * This model is currently the Holmes Featherstone model.
	 * 
	 * @see HolmesFeatherstoneAttractionModel
	 * @param environment The Space Environment in the simulation
	 */
	public EarthGravityAttraction(Environment environment) {
		super (
				environment.getSolarSystem().getEarth().getRotatingFrame(),
				environment.getGravitationalPotential().
				getNormalizedSphericalHarmonicCoeffProvider()
				);
	
		EarthGravityAttraction.logger.info(CustomLoggingTools.indentMsg(logger, 
				" -> Building Earth Gravity Attraction: Succeed."));
	}
}
