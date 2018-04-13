/* Copyright 20017-2018 Melbourne Space Program
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package msp.simulator.dynamic.guidance;

import org.orekit.attitudes.AttitudeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class Guidance {
	
	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(Guidance.class);
	
	/** Automatic Guidance instance. */
	@SuppressWarnings("unused")
	private AutomaticGuidance automaticGuidance;
	
	/** Dynamic Guidance Engine. */
	private DynamicGuidance dynamicGuidance;
	
	/* Attitude Provider of the Gudiance Engine. */
	private AttitudeProvider attitudeProvider;
	
	public Guidance(Environment environment, Satellite satellite) {
		logger.info(CustomLoggingTools.indentMsg(logger, 
				"Building the Guidance Core..."));
		
		/* Build the different guidance engine. */
		//this.automaticGuidance = new AutomaticGuidanceEngine(environment, satellite);
		this.dynamicGuidance = new DynamicGuidance(satellite);
		
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
