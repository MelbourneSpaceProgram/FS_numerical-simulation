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

package msp.simulator.dynamic.torques;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.time.AbsoluteDate;

/**
 * Interface to normalize the provision of torques
 * to the dynamic engine.
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public interface TorqueProvider {
	
	/**
	 * Provide a torque interaction on the satellite
	 * in the satellite frame.
	 * @param date Used to retrieve the appropriate torque in time.
	 * @return Vector3D in Satellite Frame.
	 */
	public Vector3D getTorque(AbsoluteDate date);
	

}
