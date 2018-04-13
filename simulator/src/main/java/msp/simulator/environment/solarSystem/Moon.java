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

package msp.simulator.environment.solarSystem;

import org.orekit.bodies.CelestialBody;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.errors.OrekitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class represents the Earth in the simulation and provides access
 * and tools to the singleton instance created through OreKit.<p>
 * 
 * @see CelestialBodyFactory
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class Moon {
	
	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(Moon.class);

	/** Moon Celestial Body. */
	private CelestialBody moonCelestialBody = null;
	
	public Moon() {
		Moon.logger.info(CustomLoggingTools.indentMsg(Moon.logger,
				"-> Building the Moon..."));
	
		try {
			this.moonCelestialBody = CelestialBodyFactory.getMoon();
			
		} catch (OrekitException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Moon Celestial Body
	 */
	public CelestialBody getMoonCelestialBody() {
		return moonCelestialBody;
	}

}
