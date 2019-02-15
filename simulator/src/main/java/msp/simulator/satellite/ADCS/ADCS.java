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
package msp.simulator.satellite.ADCS;

import java.util.function.Consumer;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
import msp.simulator.satellite.ADACS.sensors.Sensors;
import msp.simulator.satellite.ADCS.ADCSPhysics.ADCSPhysics;
import msp.simulator.satellite.ADCS.Actuators.Actuators;
import msp.simulator.satellite.ADCS.Controller.Controller;
import msp.simulator.satellite.ADCS.Estimators.Estimators;
import msp.simulator.utils.logs.CustomLoggingTools;
/**
 *
 * @author Jack McRobbie 
 */
public class ADCS {
	private static final Logger logger = LoggerFactory.getLogger(ADCS.class);
	private Sensors sensors; 
	private Estimators estimators;
	private Controller controllers; 
	private Actuators actuators;
	private ADCSPhysics physics; 
	
	
	public ADCS(Satellite sat,Environment environment) {
		this.sensors = new Sensors(environment, sat.getAssembly());
		this.estimators = new Estimators(sat);
		this.controllers = new Controller(sat);	
		this.physics = new ADCSPhysics(sat, environment);
		ADCS.logger.info(CustomLoggingTools.indentMsg(ADCS.logger,
				"Building the ADCS Module: Success..."));
	}
	public Vector3D ComputeTorque() {
		Vector3D magneticDipole = this.controllers.getDipole();
		logger.info(this.physics.ComputeMagnetorquerTorque(magneticDipole).toString());
		return this.physics.ComputeMagnetorquerTorque(magneticDipole);
//		return new Vector3D(0.01,0.01,0.01);
	}
	/**
	 * @return
	 */
	public Sensors getSensors() {
		return sensors;
	}
}
