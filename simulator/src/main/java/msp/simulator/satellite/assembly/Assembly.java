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

package msp.simulator.satellite.assembly;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * The assembly describes the entity of the satellite and gathers
 * both its physical and state representation.
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class Assembly {

	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(Assembly.class);

	/** Instance of the satellite body for the assembly. */
	private SatelliteBody satelliteBody;

	/** Instance of the satellite initial state in space. */
	private SatelliteStates satelliteStates;

	/**
	 * Build the satellite as a body and a state vector.
	 * 
	 * @param environment Use to extract the Sun body to create a
	 * radiation sensitive satellite body.
	 */
	public Assembly(Environment environment) {
		Assembly.logger.info(CustomLoggingTools.indentMsg(Assembly.logger,
				"Assembly in process..."));

		this.satelliteBody = new SatelliteBody(environment);
		this.satelliteStates = new SatelliteStates(environment, satelliteBody);
	}

	/**
	 * Return the satellite body as a CubeSat box sensitive
	 * to radiation and drag.
	 * @return BoxAndSolarArraySpaceCraft (DragSensitive, RadiationSensitive)
	 */
	public SatelliteBody getBody() {
		return this.satelliteBody;
	}

	/**
	 * Return the satellite states of the satellite
	 * directly from the Assembly Object.
	 * 
	 * @return SpacecraftState
	 * @see Assembly
	 */
	public SatelliteStates getStates() {
		return this.satelliteStates;
	}

	/**
	 * @return The Satellite frame: a non-inertial rotating
	 * frame fixed with the axis body.
	 */
	public Frame getSatelliteFrame() {
		return new Frame (
				FramesFactory.getEME2000(),
				this.getStates().getCurrentState().toTransform(),
				"SatelliteFrame"
				);
	}

	/**
	 * Calculate the current angular momentum of the satellite.
	 * @return Angular momentum vector
	 */
	public Vector3D getAngularMomentum() {
		Vector3D rotationRate = this.satelliteStates
				.getCurrentState().getAttitude().getSpin();
		
		double[][] inertiaMatrix = this.satelliteBody.getInertiaMatrix();

		Vector3D row0 = new Vector3D(inertiaMatrix[0]);
		Vector3D row1 = new Vector3D(inertiaMatrix[1]);
		Vector3D row2 = new Vector3D(inertiaMatrix[2]);

		Vector3D angularMomentum = new Vector3D(
				row0.dotProduct(rotationRate),
				row1.dotProduct(rotationRate),
				row2.dotProduct(rotationRate)
				);

		return angularMomentum;
	}


}
