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

import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class EarthGravityAttraction extends HolmesFeatherstoneAttractionModel {

	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(
			EarthGravityAttraction.class);
	
	/**
	 * Construct the Earth Gravity Attraction Force Model.
	 * This model is currently the Holmes Featherstone model.
	 * 
	 * @see HolmesFeatherstoneAttractionModel
	 * @param environment The Space Environment in the simulation
	 */
	public EarthGravityAttraction(Environment environment) {
		super (
				environment.getSolarSystem().getEarth().getRotatingFrame(),
				environment.getGravitationalPotential().
				getNormalizedSphericalHarmonicCoeffProvider()
				);
	
		EarthGravityAttraction.logger.info(CustomLoggingTools.indentMsg(logger, 
				" -> Building Earth Gravity Attraction: Succeed."));
	}
}
