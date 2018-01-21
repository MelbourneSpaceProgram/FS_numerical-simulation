/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.guidance;

import org.orekit.attitudes.AttitudeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.dynamic.torques.TorqueProvider;
import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class Guidance {
	
	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(Guidance.class);
	
	/** Automatic Guidance instance. */
	@SuppressWarnings("unused")
	private AutomaticGuidanceEngine automaticGuidance;
	
	/** Dynamic Guidance Engine. */
	private DynamicGuidance dynamicGuidance;
	
	/* Attitude Provider of the Gudiance Engine. */
	private AttitudeProvider attitudeProvider;
	
	public Guidance(Environment environment, Satellite satellite,
			TorqueProvider torqueProvider) {
		logger.info(CustomLoggingTools.indentMsg(logger, 
				"Building the Guidance Core..."));
		
		/* Build the different guidance engine. */
		this.automaticGuidance = new AutomaticGuidanceEngine(environment, satellite);
		this.dynamicGuidance = new DynamicGuidance(satellite, torqueProvider);
		
		/* Define the attitude provider for the simulation. */
		//this.attitudeProvider = this.automaticGuidance.getEarthPointing();
		this.attitudeProvider = this.dynamicGuidance;
	
	}

	/**
	 * @return the attitudeEngine
	 */
	public AttitudeProvider getAttitudeProvider() {
		return this.attitudeProvider;
	}
	
}
