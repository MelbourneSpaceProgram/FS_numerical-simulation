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
package msp.simulator.dynamic.torques.disturbances;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.orekit.time.AbsoluteDate;

import msp.simulator.dynamic.torques.TorqueProvider;

/**
 * Describe a minimal torque disturbances provider.
 * 
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class SimpleTorqueDisturbances implements TorqueProvider {

	/**
	 * Simple constructor.
	 */
	public SimpleTorqueDisturbances() {
		
	}

	/** Create a simple random normal noise to perturb the overall torque. */
	@Override
	public Vector3D getTorque(AbsoluteDate date) {
		
		Vector3D disturbance = new Vector3D(
				2 * (FastMath.random() - 0.5) * 1e-3,
				2 * (FastMath.random() - 0.5) * 1e-3,
				2 * (FastMath.random() - 0.5) * 1e-3
				);
		
		return disturbance;
	}

}
