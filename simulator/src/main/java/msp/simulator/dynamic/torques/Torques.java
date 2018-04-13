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

package msp.simulator.dynamic.torques;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class is responsible to manage the torque classes
 * of the simulator and to provide the overall interaction
 * on the satellite - in the satellite frame - to the
 * dynamic engine.
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class Torques {

	/* ******* Public Static Attributes ******* */

	/** Set the torque provider in use by the simulator. */
	public static TorqueProviderEnum commandTorqueProvider = TorqueProviderEnum.SCENARIO;

	/* **************************************** */

	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(Torques.class);

	/** Instance of Torque Provider. */
	private ArrayList<TorqueProvider> torqueProviders;

	/**
	 * Build the Main Torque Provider of the dynamic module.
	 * @param environment The Environment of Simulation
	 * @param satellite The Satellite in the simulation.
	 */
	public Torques (Environment environment, Satellite satellite) {
		logger.info(CustomLoggingTools.indentMsg(logger,
				"Building the Torque Engine..."));

		/* Build the torque providers in use in the simulation. 	*/
		this.torqueProviders = new ArrayList<TorqueProvider>();
		
		/*  - Register the command provider.						*/
		switch (Torques.commandTorqueProvider) {
		case MEMCACHED:
			this.torqueProviders.add(
					TorqueProviderEnum.MEMCACHED.getIndex(),
					new MemCachedTorqueProvider(satellite)
					);
			break;
		case SCENARIO:
			this.torqueProviders.add(
					TorqueProviderEnum.SCENARIO.getIndex(),
					new TorqueOverTimeScenarioProvider(
							satellite.getAssembly().getStates().getInitialState().getDate())
					);
			break;
		default:
			break;
		}

		/*  - Register the disturbances.							*/
		/* ........ */

	}

	/**
	 * @return The list of registered torque provider in use 
	 * in the simulation.
	 */
	public ArrayList<TorqueProvider> getTorqueProviders() {
		return this.torqueProviders;
	}

}
