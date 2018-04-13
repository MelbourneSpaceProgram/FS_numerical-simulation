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

import org.orekit.forces.gravity.ThirdBodyAttraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class implements the force model attraction 
 * of a third body of the Solar System.
 * 
 * @see msp.simulator.environment.solarSystem.Moon
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class MoonThirdBodyAttraction extends ThirdBodyAttraction {

	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(MoonThirdBodyAttraction.class);

	/**
	 * Construct the gravity attraction of a third body of the Solar System.
	 * It currently takes into account the effect of the Moon.
	 * 
	 * @param environment The Space Environment of Simulation.
	 * @see msp.simulator.environment.solarSystem.Moon
	 */
	public MoonThirdBodyAttraction(Environment environment) {
		super(environment.getSolarSystem().getMoon().getMoonCelestialBody());
	
		MoonThirdBodyAttraction.logger.info(CustomLoggingTools.indentMsg(
				MoonThirdBodyAttraction.logger,
				" -> Building the Third Body Attraction for the Moon: Succeed"));
	}
}
