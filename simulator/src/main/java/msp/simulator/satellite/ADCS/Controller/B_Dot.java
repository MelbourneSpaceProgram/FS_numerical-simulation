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
package msp.simulator.satellite.ADCS.Controller;

import org.hipparchus.geometry.euclidean.threed.Vector3D;

import msp.simulator.satellite.Satellite;
import msp.simulator.satellite.ADCS.Actuators.Actuators;
import msp.simulator.satellite.ADCS.Estimators.BdotEstimator.BdotEstimator;

/**
 *
 * @author Jack McRobbie>
 */
public class B_Dot {
	private static final Vector3D bdotGains = new Vector3D(-54000,-54000,-54000);
	private Actuators actuators; 
	private BdotEstimator est; 
	
	public B_Dot(Satellite sat) {
		this.est = new BdotEstimator(sat);
		this.actuators = new Actuators();
	}
	public Vector3D computeDipole() {
		Vector3D dutyCycle = new Vector3D(1.0,est.computeBdot(),1.0,this.bdotGains);
		Vector3D result = this.actuators.getMagnetorquers().computeDipole(dutyCycle);
		return result;
	}
}
