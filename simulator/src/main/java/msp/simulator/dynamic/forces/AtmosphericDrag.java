/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.forces;


import org.orekit.forces.drag.DragForce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class represents the force model related to
 * the atmospheric drag.
 * 
 * @see DragForce
 * 
 * @author Florian CHAUBEYRE
 */
public class AtmosphericDrag extends DragForce {

	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(AtmosphericDrag.class);
	
	/**
	 * Construct the Asmospheric Drag Force Model.
	 * @param environment Space Environment of the Simulation to extract the Atmosphere
	 * @param satellite Satellite instance to extract the dreag sensitive body.
	 */
	public AtmosphericDrag(Environment environment, Satellite satellite) {
		super(	environment.getAtmosphere(),
				satellite.getAssembly().getBody()
				);
		AtmosphericDrag.logger.info(CustomLoggingTools.indentMsg(logger, 
				" -> Building Atmospheric Drag: Succeed."));
	}
	
	@Override
	public String toString() {
		return new String("Atmospheric Drag");
	}

}
