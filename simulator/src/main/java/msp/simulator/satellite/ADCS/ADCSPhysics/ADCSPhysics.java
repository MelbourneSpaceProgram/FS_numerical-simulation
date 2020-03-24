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
package msp.simulator.satellite.ADCS.ADCSPhysics;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
import msp.simulator.satellite.ADACS.sensors.Sensors;
import msp.simulator.satellite.ADCS.Actuators.MagnetoTorquers;
import msp.simulator.satellite.assembly.SatelliteBody;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Jack McRobbie
 */
public class ADCSPhysics {

	/** Logger of the class. */
	private static final Logger logger = 
			LoggerFactory.getLogger(ADCSPhysics.class);
	
	private Satellite satellite; 
	private Environment environment; 
	private Sensors sensor;
	public ADCSPhysics(Satellite satellite, Environment environemt) {
		this.satellite = satellite; 
		this.environment = environment; 
		ADCSPhysics.logger.info(CustomLoggingTools.indentMsg(ADCSPhysics.logger, 
				" -> Building the ADCS Physics engine: Success."));
	}
	public Vector3D ComputeMagnetorquerTorque(Vector3D magneticDipole) {
		Vector3D magfield = this.satellite.getADCS().getSensors().getMagnetometer()
				.retrievePerfectField().getFieldVector().scalarMultiply(0.000000001);
		Vector3D result = Vector3D.crossProduct(magneticDipole,magfield );
		return result;
	}
}
