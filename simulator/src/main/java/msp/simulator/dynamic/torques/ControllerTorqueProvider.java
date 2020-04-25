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

import msp.simulator.NumericalSimulator;
import msp.simulator.dynamic.torques.TorqueOverTimeScenarioProvider.Step;
import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
/**
 *
 * @author Jack McRobbie
 * This class represents the satellites own method 
 * for providing stabilization and control 
 */
public class ControllerTorqueProvider implements TorqueProvider{
	private Satellite sat;
	private Vector3D steptorque;
	public ControllerTorqueProvider(Satellite satellite, AbsoluteDate date, Environment environment) {
		this.sat = satellite;
		this.steptorque = Vector3D.ZERO; 
	}
	/** {@inheritDoc} */
	@Override
	public Vector3D getTorque(AbsoluteDate date) {
		this.steptorque = sat.getADCS().ComputeTorque();
		/* Finally returns the torque of the step (updated if needed). */
		return this.steptorque;
	}
}
