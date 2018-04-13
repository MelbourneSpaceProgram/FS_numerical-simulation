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
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
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
