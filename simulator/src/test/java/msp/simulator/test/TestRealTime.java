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

package msp.simulator.test;

import org.hipparchus.complex.Quaternion;
import org.hipparchus.geometry.euclidean.threed.Vector3D;

import msp.simulator.NumericalSimulator;
import msp.simulator.dynamic.torques.TorqueProviderEnum;
import msp.simulator.satellite.assembly.SatelliteBody;
import msp.simulator.user.Dashboard;

/**
 * User test for a real-time communication with the flight software.
 * 
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class TestRealTime {

	/**
	 * This test is supposed to create an infinite simulation
	 * with an open communication with the flight software
	 * running on the launchpad.
	 * Thus it should be excluded from the test automation.
	 * 
	 * TODO: Move the infinite-simulation test out of the test procedures.
	 * 
	 * @throws Exception When the simulation fails.
	 */
	public void testDetumblingRealTime() throws Exception {

		Dashboard.setDefaultConfiguration();
		Dashboard.setRealTimeProcessing(true);
		Dashboard.setSimulationDuration(1000000);
		
		Dashboard.setIntegrationTimeStep(0.1);
		Dashboard.setEphemerisTimeStep(1.0);
		Dashboard.setSatelliteInertiaMatrix(SatelliteBody.satInertiaMatrix);

		Dashboard.setInitialAttitudeQuaternion(new Quaternion(1, 0, 0, 0));
		Dashboard.setInitialSpin(new Vector3D(1, 1, 1));
		Dashboard.setInitialRotAcceleration(new Vector3D(0,0,0));

		Dashboard.setTorqueProvider(TorqueProviderEnum.MEMCACHED);
		Dashboard.setMemCachedConnection(true, "127.0.0.1:11211");

		//Dashboard.setVtsConnection(true);

		/* *** Creating and launching the simulation. *** */
		NumericalSimulator simu = new NumericalSimulator();
		simu.initialize();
		simu.process();
		simu.exit();
	}

}
