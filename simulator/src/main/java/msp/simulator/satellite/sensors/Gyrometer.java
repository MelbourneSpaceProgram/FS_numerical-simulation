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
package msp.simulator.satellite.sensors;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.satellite.assembly.Assembly;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * Modelize the gyrometer sensor of the satellite.
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class Gyrometer {

	/** Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(
			Gyrometer.class);

	/** Instance of the simulation. */
	private Assembly assembly;

	/**
	 * Simple constructor of the gyrometer.
	 * @param environment Instance of the simulation
	 * @param assembly Instance of the simulation
	 */
	public Gyrometer(Environment environment, Assembly assembly) {
		logger.info(CustomLoggingTools.indentMsg(logger,
				" -> Building the Gyrometer..."));

		this.assembly = assembly;
	}

	/**
	 * Retrieve the data from the sensor.
	 * @return Rotational Acceleration
	 * 
	 * TODO: Add noise to the sensor data.
	 */
	public Vector3D getData_rotAcc() {
		/* Get the acceleration from the satellite state. */
		Vector3D data = this.assembly.getStates()
				.getCurrentState().getAttitude().getRotationAcceleration();

		/* The data are already in the body frame!! */
		
		/* Transform the data into the satellite body frame. */
		//Vector3D data_body = this.assembly.getStates().getCurrentState().toTransform()
		//		.transformVector(data_inertial);		
		
		return data;
	}


}
