/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.forces;

import org.orekit.forces.radiation.SolarRadiationPressure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class is the Force Model related to the Solar
 * Radiation Pressure.<p>
 * To take it into account in the overall propagation,
 * the user should add it to the defined propagator 
 * with the method addForceModel().
 * 
 * @author Florian CHAUBEYRE
 */
public class RadiationPressure extends SolarRadiationPressure {
	
	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(RadiationPressure.class);
	

	/**
	 * Construct the Solar Radiation Pressure Force Model.
	 * @param environment Space Environment of the simulation to extract the Sun celestial body
	 * @param satellite Satellite instance to extract the radiation sensitive body
	 */
	public RadiationPressure(Environment environment, Satellite satellite) {
		super(
				environment.getSolarSystem().getSun().getPvCoordinateProvider(),
				environment.getSolarSystem().getEarth().getRadius(),
				satellite.getAssembly().getBody()
				);
		
		RadiationPressure.logger.info(CustomLoggingTools.indentMsg(logger, 
				" -> Building Solar Radiation Pressure: Succeed."));

	}
}
