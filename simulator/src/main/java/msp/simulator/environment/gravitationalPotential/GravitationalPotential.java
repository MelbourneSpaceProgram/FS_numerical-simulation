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

package msp.simulator.environment.gravitationalPotential;

import org.orekit.errors.OrekitException;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.ICGEMFormatReader;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class represents the potential gravity of the Earth used
 * in the numerical simulator.<p>
 * 
 * This class is directly related to OREKIT but for a better
 * understanding of the modules of the simulator, the functionalities
 * are linked here.
 * It also provides some other methods and tools useful to some other
 * modules of the simulator, e.g. the forces and the dynamic.
 * 
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class GravitationalPotential {

	/** Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(
			GravitationalPotential.class);

	private NormalizedSphericalHarmonicsProvider spericalHarmonicProvider;

	public GravitationalPotential() {
		logger.info(CustomLoggingTools.indentMsg(logger,
				"Loading the Earth Potential Gravity Field..."));
		
		/* Loading the Earth Gravity Field in OreKit. */
		GravityFieldFactory.addPotentialCoefficientsReader(
				new ICGEMFormatReader(GravityFieldFactory.ICGEM_FILENAME, true));
		try {
			this.spericalHarmonicProvider = 
					GravityFieldFactory.getConstantNormalizedProvider(6, 6);
		} catch (OrekitException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Return the provider of the gravity model harmonic fields.
	 * The coefficients are initially read in a resource file.
	 * 
	 * @return Provider of the gravity field coefficient.
	 * @see GravityFieldFactory
	 */
	public NormalizedSphericalHarmonicsProvider 
	getNormalizedSphericalHarmonicCoeffProvider() {
		return this.spericalHarmonicProvider;
	}
}
