/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.test;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.junit.Test;

import msp.simulator.NumericalSimulator;
import msp.simulator.dynamic.torques.TorqueProviderEnum;
import msp.simulator.user.Dashboard;

/**
 * 
 * @author Florian CHAUBEYRE
 */
public class TestRealTime {

	@Test 
	public void testDetumblingRealTime() throws Exception {

		NumericalSimulator simu = new NumericalSimulator();
		Dashboard.setDefaultConfiguration();

		Dashboard.setIntegrationTimeStep(0.1);
		Dashboard.setEphemerisTimeStep(1.0);
		Dashboard.setSimulationDuration(100);
		
		Dashboard.setInitialAttitudeQuaternion(1, 0, 0, 0);
		Dashboard.setInitialSpin(new Vector3D(1, 1, 1));
		Dashboard.setInitialRotAcceleration(new Vector3D(0,0,0));
		
		Dashboard.setTorqueProvider(TorqueProviderEnum.MEMCACHED);
		Dashboard.setMemCachedConnection(true, "127.0.0.1:11211");
		
		//Dashboard.setVtsConnection(true);

		simu.initialize();
		simu.process();
		simu.exit();
	}

}
