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

package msp.simulator.satellite.ADCS.Actuators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class Actuators {
	
	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(Actuators.class);
	private MagnetoTorquers magnetorquer;
	/**
	 * 
	 */
	public Actuators() {
		this.magnetorquer = new MagnetoTorquers();
		Actuators.logger.info(CustomLoggingTools.indentMsg(Actuators.logger,
				"Building the Actuators..."));
	}

	/**
	 * @return
	 */
	public MagnetoTorquers getMagnetorquers() {
		return this.magnetorquer;
	}
}
