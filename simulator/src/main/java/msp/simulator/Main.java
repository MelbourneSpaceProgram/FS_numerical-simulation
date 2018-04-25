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

package msp.simulator;

import org.hipparchus.complex.Quaternion;
import org.hipparchus.geometry.euclidean.threed.Vector3D;

import msp.simulator.NumericalSimulator;
import msp.simulator.dynamic.torques.TorqueProviderEnum;
import msp.simulator.satellite.assembly.SatelliteBody;
import msp.simulator.user.Dashboard;

/**
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 *
 */
public class Main {
	
	/**
	 * Main method: compute the main use-case of the simulator.
	 * @param args - unused
	 */
	public static void main(String[] args) {
		try {
			/* *** Configuration of the simulator. *** */
			Dashboard.setDefaultConfiguration();
			Dashboard.setRealTimeProcessing(true);
			Dashboard.setSimulationDuration(1000000);
			
			Dashboard.setIntegrationTimeStep(0.1);
			Dashboard.setEphemerisTimeStep(0.1);
			Dashboard.setStepDelay(0.1);		

			Dashboard.setInitialAttitudeQuaternion(new Quaternion(1, 0, 0, 0));
			Dashboard.setInitialSpin(new Vector3D(0.5, 0.5, 0.5));
			Dashboard.setInitialRotAcceleration(new Vector3D(0,0,0));
			Dashboard.setTorqueDisturbances(false);
			//Dashboard.setSatelliteInertiaMatrix(SatelliteBody.satInertiaMatrix);

			Dashboard.setCommandTorqueProvider(TorqueProviderEnum.MEMCACHED);
			Dashboard.setMemCachedConnection(true, "127.0.0.1:11211");

			Dashboard.setVtsConnection(false);
			
			/* *** Creating and launching the simulation. *** */
			NumericalSimulator simulator = new NumericalSimulator();
			simulator.initialize();
			simulator.process();
			simulator.exit();
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

}
