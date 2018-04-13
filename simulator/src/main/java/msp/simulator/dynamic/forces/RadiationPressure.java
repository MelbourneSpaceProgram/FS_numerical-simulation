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
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
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
