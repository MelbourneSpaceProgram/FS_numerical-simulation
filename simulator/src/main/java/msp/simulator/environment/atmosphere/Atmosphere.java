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

package msp.simulator.environment.atmosphere;

import org.orekit.errors.OrekitException;
import org.orekit.forces.drag.atmosphere.HarrisPriester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.solarSystem.Earth;
import msp.simulator.environment.solarSystem.Sun;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class represents the atmosphere of the earth used
 * in the numerical simulator.<p>
 * 
 * The current instance relies on the Harris-Priester model.
 * This one is static and do not need any daily additionnal data.<p>
 * 
 * This class is directly related to OREKIT but for a better
 * understanding of the modules of the simulator, it is extended
 * here.<p>
 * It also provides some other methods and tools to the simulator.
 * 
 * @see org.orekit.forces.drag.atmosphere.HarrisPriester
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public final class Atmosphere extends HarrisPriester {

	/** Generated Serial Version UID. */
	private static final long serialVersionUID = -1497026274240688235L;
	
	/** Logger of the instance of the class. */
	private final static Logger logger = LoggerFactory.getLogger(Atmosphere.class);
	
	/**
	 * Constructor of the instance of Earth atmosphere.
	 * @param earth The Earth Instance of the simulation.
	 * @param sun The Sun Instance of the simulation.
	 * 
	 * @throws OrekitException If OreKit initialization fails
	 */
	public Atmosphere(Earth earth, Sun sun) throws OrekitException {
		
		/* Harris-Priester Model 
		 * - Sun Coordinate Provider
		 * - Earth One Axis Ellipsoid
		 * - Cosine Exponent : 2 (low inclinaison) to 6 (Polar OrbitWrapper)
		 */
		super(	sun.getPvCoordinateProvider(),
				earth.getEllipsoid(),
				5		/* Arbitrary set. */
				);
		
		logger.info(CustomLoggingTools.indentMsg(logger, 
				"Building the Earth Atmosphere: success."));
	}
	
}
