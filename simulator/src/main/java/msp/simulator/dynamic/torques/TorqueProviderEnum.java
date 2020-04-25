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

/**
 * Enumerate the different torque providers available in the simulator.
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public enum TorqueProviderEnum {
	/* Command torque provider. */
	MEMCACHED(0),
	SCENARIO(0),
	CONTROLLER(0),
	/* Disturbances. */
	GRAVITY(1),
	ATMOSPHERIC(2),
	MAGNETIC(3),
	SOLAR_PRESSURE(4)
	;
	
	/** Define the nature of the provided torque. */
	private int id;
	
	/**
	 * Constructor of a torque provider enumerate.
	 * @param id Defines the nature of the provided torque.
	 */
	private TorqueProviderEnum(int id) {
		this.id = id;
	}
	
	/**
	 * Get the ID describing the nature of the provided torque.
	 * @return The ID of the Enumerate.
	 */
	public int getIndex() {
		return this.id;
	}
}
