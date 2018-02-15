/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.forces;

import org.orekit.forces.gravity.ThirdBodyAttraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class implements the force model attraction 
 * of a third body of the Solar System.
 * 
 * @see msp.simulator.environment.solarSystem.Moon
 * @author Florian CHAUBEYRE
 */
public class MoonThirdBodyAttraction extends ThirdBodyAttraction {

	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(MoonThirdBodyAttraction.class);

	/**
	 * Construct the gravity attraction of a third body of the Solar System.
	 * It currently takes into account the effect of the Moon.
	 * 
	 * @param environment The Space Environment of Simulation.
	 * @see msp.simulator.environment.solarSystem.Moon
	 */
	public MoonThirdBodyAttraction(Environment environment) {
		super(environment.getSolarSystem().getMoon().getMoonCelestialBody());
	
		MoonThirdBodyAttraction.logger.info(CustomLoggingTools.indentMsg(
				MoonThirdBodyAttraction.logger,
				" -> Building the Third Body Attraction for the Moon: Succeed"));
	}
}
