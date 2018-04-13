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
import org.orekit.utils.PVCoordinatesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class represents the Sun in the simulation and provides access
 * and tools to the singleton instance created through OreKit.<p>
 * 
 * @see CelestialBodyFactory
 * 
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class Sun {
	
	/** Logger instance for the Sun. */
	private final Logger logger;

	/** Celestial Body of the Sun instance. */
	private CelestialBody sunCelestialBody;
	
	
	/**
	 * Constructor of the Sun instance.<p>
	 * Keep in mind this is only a link to the singleton
	 * instance provided by  OreKit. So multiple instanciations
	 * will only point at the same object.
	 */
	public Sun() {
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.logger.info(CustomLoggingTools.indentMsg(this.logger,
				"-> Building the Sun..."));
		try {
			this.sunCelestialBody = CelestialBodyFactory.getSun();
		} catch (OrekitException e) {
			e.printStackTrace();
		}
	}
	
	public PVCoordinatesProvider getPvCoordinateProvider() {
		return (this.sunCelestialBody) ;
	}

}
