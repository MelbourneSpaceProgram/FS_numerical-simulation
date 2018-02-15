/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.test;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
import org.orekit.attitudes.Attitude;

import msp.simulator.NumericalSimulator;
import msp.simulator.user.Dashboard;


/**
 * JUnit Tests of the attitude propagation engine.
 * 
 * @author Florian CHAUBEYRE
 */
public class TestAttitudePropagation {

	/**
	 * Process a simple rotation at constant spin to
	 * check the attitude quaternion propagation.
	 * 
	 * <p><b>Justification:</b><p>
	 * 
	 * + At the initial state: <p>
	 * Q0 = (1, 0, 0, 0) i.e. the satellite frame is aligned
	 * with the inertial frame.
	 * <p>
	 * + At any time t: <p>
	 * Q	 = ( cos(a/2), sin(a/2).n )
	 *  	= ( cos(w.t/2), sin(w.t/2).n )
	 *  with w = Pi/dur
	 *  <p>
	 * + At the time t = dur, i.e. the end state:<p>
	 *  Q = ( cos(Pi/2), sin(Pi/2).n )
	 *    = ( 0, nx, ny, nz)
	 * 
	 */
	@Test 
	public void testSimpleRotation() {
		
		NumericalSimulator simu = new NumericalSimulator();
		Dashboard.setDefaultConfiguration();
		
		double rotationTime = 100;
		Vector3D n = new Vector3D(1,2,3).normalize();
		
		Dashboard.setIntegrationTimeStep(1.0);
		Dashboard.setSimulationDuration(rotationTime);
		Dashboard.setInitialAttitudeQuaternion(1, 0, 0, 0);
		Dashboard.setInitialSpin(new Vector3D(FastMath.PI / rotationTime, n));
		Dashboard.setInitialRotAcceleration(new Vector3D(0,0,0));
		
		simu.launch();
		
		/* Actual end state of the satellite. */
		Attitude endAttitude = simu.getSatellite().getStates().getCurrentState().getAttitude();
		double[] actualAttitudeArray = new double[] {
				endAttitude.getRotation().getQ0(),
				endAttitude.getRotation().getQ1(),
				endAttitude.getRotation().getQ2(),
				endAttitude.getRotation().getQ3(),
		};
		
		/* Expected state of the satellite after the processing. */
		double[] expectedAttitudeArray = new double[] {0, n.getX(), n.getY(), n.getZ()} ;
		
		/* Approximation error during the propagation. */
		double delta = 1e-6 ;
		
		/* Testing the attitude of the satellite after the processing. */
		Assert.assertArrayEquals(
				expectedAttitudeArray, 
				actualAttitudeArray,
				delta);
		
	}
	
}
