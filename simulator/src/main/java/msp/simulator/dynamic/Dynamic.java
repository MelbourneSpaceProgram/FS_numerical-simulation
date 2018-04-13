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

package msp.simulator.dynamic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.dynamic.forces.Forces;
import msp.simulator.dynamic.guidance.Guidance;
import msp.simulator.dynamic.propagation.Propagation;
import msp.simulator.dynamic.torques.Torques;
import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class is the top class to handle all of the
 * dynamic processing in the simulation.
 * It includes all of the interactions, linear or 
 * rotational, between the environment and the satellite.
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class Dynamic {
	
	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(Dynamic.class);

	/** Instance of Linear Forces Model of the dynamic. */
	private Forces forces;
	
	/** Instance of the Guidance Core of the Satellite in the simulation. */
	private Guidance guidance;
	
	/** Instance of the Torques Manager of the Dynamic Module/. */
	private Torques torques;

	/** Instance of Propagation of the dynamic. */
	private Propagation propagation;

	/**
	 * Create the instance of the dynamic engine of the simulation.
	 * @param environment Simulation Instance
	 * @param satellite Simulation Instance
	 */
	public Dynamic(Environment environment, Satellite satellite) {
		Dynamic.logger.info(CustomLoggingTools.indentMsg(logger, 
				"Building the Dynamic Engine..."));

		this.forces = new Forces(environment, satellite);
		this.torques = new Torques(environment, satellite);
		this.guidance = new Guidance(environment, satellite);
		this.propagation = new Propagation(
				environment,
				satellite, 
				this.forces,
				this.torques,
				this.guidance
				);
	}

	/**
	 * @return the linear Forces instance of the dynamic module.
	 */
	public Forces getForces() {
		return forces;
	}

	/**
	 * @return The Propagtion Services of the Dynamic Module
	 */
	public Propagation getPropagation() {
		return propagation;
	}

	/**
	 * @return the guidance
	 */
	public Guidance getGuidance() {
		return guidance;
	}

	/**
	 * @return the torques
	 */
	public Torques getTorques() {
		return torques;
	}

}
