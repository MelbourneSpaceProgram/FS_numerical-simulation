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
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 * @author Braeden BORG
 */
public class Sensors {

	/** Logger of the class. */
	private static final Logger logger = 
			LoggerFactory.getLogger(Sensors.class);

	/** Instance of environment of the simulation. */
	private Environment environment;

	/** Instance of the satellite assembly of the simulation. */
	private Assembly assembly;

	/* ***** Instances of the different sensors. ***** */

	/** Instance of magnetometer in the simulation */
	private Magnetometer magnetometer;

	/** Instance of infrared Sensors in the simulation */
	private InfraredSensor 
	posXIRSensor,
	negXIRSensor,
	posYIRSensor, 
	negYIRSensor,
	posZIRSensor,
	negZIRSensor;
	
	/** Instance of gyrometer in the simulation. */
	private Gyrometer gyrometer;

	/**
	 * Constructor of the satellite sensors.
	 * 
	 * @param environment Instance of the simulation
	 * @param assembly Instance of the simulation
	 */
	public Sensors(Environment environment, Assembly assembly) {
		logger.info(CustomLoggingTools.indentMsg(logger, 
				"Building the satellite Sensors..."));

		/* Linking the sensors class to the rest of the simulation. */
		this.environment = environment;
		this.assembly = assembly;

		/* TODO: Normalize the construction and the use of the sensors.
		 * Typically by extending a minimal abstract class Sensor.
		 */
		
		/* Building the sensors. */
		this.magnetometer = new Magnetometer(this.environment, this.assembly);
		this.gyrometer = new Gyrometer(this.environment, this.assembly);
		
		this.posXIRSensor = new InfraredSensor(Vector3D.PLUS_I);
		this.negXIRSensor = new InfraredSensor(Vector3D.MINUS_I);
		this.posYIRSensor = new InfraredSensor(Vector3D.PLUS_J);
		this.negYIRSensor = new InfraredSensor(Vector3D.MINUS_J);
		this.posZIRSensor = new InfraredSensor(Vector3D.PLUS_K);
		this.negZIRSensor = new InfraredSensor(Vector3D.MINUS_K);
	}

	/**
	 * @return the magnetometer
	 */
	public Magnetometer getMagnetometer() {
		return magnetometer;
	}
	
	/**
	 * @return the gyrometer
	 */
	public Gyrometer getGyrometer() {
		return gyrometer;
	}

	/**
	 * @return Infrared sensor for a side of the satellite
	 */
	public InfraredSensor getPosXIRSensor() {
		return posXIRSensor;
	}

	public InfraredSensor getNegXIRSensor() {
		return negXIRSensor;
	}

	public InfraredSensor getPosYIRSensor() {
		return posYIRSensor;
	}

	public InfraredSensor getNegYIRSensor() {
		return negYIRSensor;
	}

	public InfraredSensor getPosZIRSensor() {
		return posZIRSensor;
	}

	public InfraredSensor getNegZIRSensor() {
		return negZIRSensor;
	}
}
