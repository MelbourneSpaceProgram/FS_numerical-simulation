/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.test;

import java.util.ArrayList;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
import org.orekit.attitudes.Attitude;
import org.orekit.propagation.SpacecraftState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.NumericalSimulator;
import msp.simulator.dynamic.propagation.integration.RotAccProvider;
import msp.simulator.dynamic.propagation.integration.SecondaryStates;
import msp.simulator.dynamic.torques.TorqueOverTimeScenarioProvider;
import msp.simulator.dynamic.torques.TorqueOverTimeScenarioProvider.Step;
import msp.simulator.dynamic.torques.TorqueProviderEnum;
import msp.simulator.user.Dashboard;
import msp.simulator.utils.logs.CustomLoggingTools;


/**
 * JUnit Tests of the attitude propagation engine.
 * 
 * @author Florian CHAUBEYRE
 */
public class TestAttitudePropagation {

	/** Instance of the Logger of the class. */
	private static final Logger logger = 
			LoggerFactory.getLogger(TestAttitudePropagation.class);

	/**
	 * Process a simple rotation of Pi at constant spin to
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
	 * @throws Exception when initialization of simulation fails
	 * 
	 */
	@Test 
	public void testSimpleRotation() throws Exception {

		/* *** CONFIGURATION *** */
		double rotationTime = 100;
		Vector3D n = new Vector3D(1,2,3).normalize();
		/* ********************* */

		NumericalSimulator simu = new NumericalSimulator();
		Dashboard.setDefaultConfiguration();

		Dashboard.setTorqueProvider(TorqueProviderEnum.SCENARIO);

		Dashboard.setIntegrationTimeStep(0.1);
		Dashboard.setEphemerisTimeStep(1.0);
		Dashboard.setSimulationDuration(rotationTime);
		Dashboard.setInitialAttitudeQuaternion(1, 0, 0, 0);
		Dashboard.setInitialSpin(new Vector3D(FastMath.PI / rotationTime, n));
		Dashboard.setInitialRotAcceleration(new Vector3D(0,0,0));

		simu.initialize();

		logger.info(CustomLoggingTools.toString(
				"Initial State of the satellite", 
				simu.getSatellite().getStates().getInitialState()));

		simu.process();

		logger.info(CustomLoggingTools.toString(
				"Final State of the satellite", 
				simu.getSatellite().getStates().getCurrentState()));

		simu.exit();

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
		double delta = 1e-9;

		/* Testing the attitude of the satellite after the processing. */
		Assert.assertArrayEquals(
				expectedAttitudeArray, 
				actualAttitudeArray,
				delta);
	}

	@Test
	public void testSimpleAcceleration() throws Exception {

		/* **** Data of the test **** */
		double accDuration = 100;
		Vector3D rotVector = new Vector3D(0.1, 0.2, 0.3);
		/* ************************** */

		NumericalSimulator simu = new NumericalSimulator();
		Dashboard.setDefaultConfiguration();
		Dashboard.setSimulationDuration(accDuration);
		Dashboard.setIntegrationTimeStep(0.1);

		Dashboard.setTorqueProvider(TorqueProviderEnum.SCENARIO);

		/* Writing the torque scenario. */
		ArrayList<TorqueOverTimeScenarioProvider.Step> torqueScenario = 
				new ArrayList<TorqueOverTimeScenarioProvider.Step>();
		torqueScenario.add(new Step(0., accDuration + 1, rotVector));

		//torqueScenario.add(new Step(5., 3., new Vector3D(-1,0,0)));
		//torqueScenario.add(new Step(55., 10., new Vector3D(1,2,3)));
		//torqueScenario.add(new Step(70., 10., new Vector3D(-1,-2,-3)));

		Dashboard.setTorqueScenario(torqueScenario);

		Dashboard.checkConfiguration();

		/* Launching the simulation. */
		simu.initialize();

		logger.info(CustomLoggingTools.toString(
				"Initial State of the satellite", 
				simu.getSatellite().getStates().getInitialState()));

		simu.process();

		logger.info(CustomLoggingTools.toString(
				"Final State of the satellite", 
				simu.getSatellite().getStates().getCurrentState()));

		simu.exit();

		/* Extracting final state. */
		SpacecraftState finalState = simu.getSatellite().getStates().getCurrentState();

		/* Computing the expected acceleration. */
		double[] expectedRotAcc = RotAccProvider.computeEulerEquations(
				rotVector.scalarMultiply(
						TorqueOverTimeScenarioProvider.getTorqueIntensity()),
				finalState.getAttitude().getSpin(), 
				simu.getSatellite().getAssembly().getBody().getInertiaMatrix()
				);

		/* Checking Rotational Acceleration. */
		Assert.assertArrayEquals(
				expectedRotAcc,
				finalState.getAdditionalState("RotAcc"), 
				1e-9);

		/* Checking Spin */
		Assert.assertArrayEquals(
				new Vector3D(expectedRotAcc).scalarMultiply(accDuration).toArray(), 
				SecondaryStates.extractState(
						finalState.getAdditionalState(SecondaryStates.key), 
						SecondaryStates.SPIN
						),
				1e-9);

	}

}
