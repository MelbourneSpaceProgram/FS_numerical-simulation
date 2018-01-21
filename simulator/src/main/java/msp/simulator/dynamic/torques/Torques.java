/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.torques;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.propagation.integration.AdditionalEquations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class is responsible to handle the processing
 * of any torque and provide the overall interaction
 * on the satellite - in the satellite frame - to the
 * dynamic engine.
 *
 * @author Florian CHAUBEYRE
 */
public class Torques implements TorqueProvider {
	
	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(Torques.class);
	
	private TorqueToSpinEquation dynamicTorqueEquation;
	
	public Torques (Environment environment, Satellite satellite) {
		logger.info(CustomLoggingTools.indentMsg(logger,
				"Building the Torque Engine..."));
		
		this.dynamicTorqueEquation = new TorqueToSpinEquation(satellite, this);
	}
	
	public AdditionalEquations getTorqueToSpinEquation() {
		return this.dynamicTorqueEquation;
	}

	/**
	 * Provide the overall torque interaction on the
	 * satellite in the satellite frame.
	 * 
	 * @see msp.simulator.dynamic.torques.TorqueProvider#getTorque()
	 */
	@Override
	public Vector3D getTorque() {
		/* TODO */
		return new Vector3D(0.01, 0, 0.001) ;
	}

}
