/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.test;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.junit.Test;

import msp.simulator.NumericalSimulator;
import msp.simulator.dynamic.torques.TorqueProviderEnum;
import msp.simulator.satellite.assembly.SatelliteBody;
import msp.simulator.user.Dashboard;

/**
 * User test for a real-time communication with the flight software.
 * This test is exclude from the automation test runner of the 
 * project.
 * @author Florian CHAUBEYRE
 */
public class TestRealTime {

	@Test
	public void testDetumblingRealTime() throws Exception {

		Dashboard.setDefaultConfiguration();
		Dashboard.setRealTimeProcessing(true);
		Dashboard.setSimulationDuration(1000000);
		
		Dashboard.setIntegrationTimeStep(0.1);
		Dashboard.setEphemerisTimeStep(1.0);
		Dashboard.setSatelliteInertiaMatrix(SatelliteBody.satInertiaMatrix);

		Dashboard.setInitialAttitudeQuaternion(1, 0, 0, 0);
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
