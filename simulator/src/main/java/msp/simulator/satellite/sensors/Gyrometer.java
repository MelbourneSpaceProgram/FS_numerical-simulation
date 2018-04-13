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
import org.hipparchus.util.FastMath;
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
	
	/* ******* Public Static Attributes ******* */

	/** This intensity is used to generate a random number to be
	 * added to each components of the sensor data.
	 */
	public static double defaultGyroNoiseIntensity = 1e-3;

	/* **************************************** */

	/** Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(
			Gyrometer.class);

	/** Instance of the simulation. */
	private Assembly assembly;
	
	/** Normal noise disturbing the gyrometer measures. */
	private double gyroNoiseIntensity;

	/**
	 * Simple constructor of the gyrometer.
	 * @param environment Instance of the simulation
	 * @param assembly Instance of the simulation
	 */
	public Gyrometer(Environment environment, Assembly assembly) {
		logger.info(CustomLoggingTools.indentMsg(logger,
				" -> Building the Gyrometer..."));

		this.assembly = assembly;
		this.gyroNoiseIntensity = defaultGyroNoiseIntensity;
	}

	/**
	 * Retrieve the data from the sensor.
	 * @return Rotational Acceleration
	 */
	public Vector3D getData_rotAcc() {
		/* Get the acceleration from the satellite state. */
		/* Note that these data are already in the satellite
		 * body frame!
		 */
		Vector3D data = this.assembly.getStates()
				.getCurrentState().getAttitude().getRotationAcceleration();
		
		/* Add the noise contribution. */
		Vector3D noise = new Vector3D(
				2 * (FastMath.random() - 0.5) * this.gyroNoiseIntensity,
				2 * (FastMath.random() - 0.5) * this.gyroNoiseIntensity,
				2 * (FastMath.random() - 0.5) * this.gyroNoiseIntensity
				);
		
		data = data.add(noise);
		
		return data;
	}


}
