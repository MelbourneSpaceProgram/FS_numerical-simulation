/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.io.test;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.junit.Assert;
import org.junit.Test;
import org.orekit.propagation.SpacecraftState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.NumericalSimulator;
import msp.simulator.dynamic.propagation.integration.RotAccProvider;
import msp.simulator.dynamic.propagation.integration.SecondaryStates;
import msp.simulator.dynamic.torques.TorqueProviderEnum;
import msp.simulator.satellite.assembly.SatelliteBody;
import msp.simulator.satellite.assembly.SatelliteStates;
import msp.simulator.user.Dashboard;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class TestMemCached {

	/** Logger of the instance. */
	private static final Logger logger = LoggerFactory.getLogger(TestMemCached.class);

	@Test
	public void testSimpleConnection() {
		/* Set up the simulation. */
		NumericalSimulator simu = new NumericalSimulator();
		Dashboard.setDefaultConfiguration();

		Dashboard.setMemCachedConnection(true, "127.0.0.1:11211");
		Dashboard.setSimulationDuration(1.0);

		try {
			/*
			if (System.getProperty("os.name").toLowerCase().contains("mac")) {
				logger.info("### Launching the MemCached server.");
				IO.mac_os_create_memcached_servor();
			} else {
				System.out.println(
						"##### Be sure the MemCached server is up and running #####");
			}
			 */

			simu.initialize();

		} catch (Exception e) {
			e.printStackTrace();
		}

		String message = "- MemCached: \"I am alive!\" ";
		double data = 1234.5678;

		/* Write the data into the io. */
		simu.getIo().getMemcached().set("status", 0, message);
		simu.getIo().getMemcached().set("sensor", 0, data);

		/* Checking that the key are well stored. */
		Assert.assertEquals(
				message, 
				simu.getIo().getMemcached().get("status"));

		logger.info((String) simu.getIo().getMemcached().get("status"));

		Assert.assertArrayEquals(
				new double[] {data}, 
				new double[] {(double) simu.getIo().getMemcached().get("sensor")}, 
				0.);

		simu.getIo().getMemcached().delete("status");
		Assert.assertEquals(
				null, 
				simu.getIo().getMemcached().get("status"));

		/* Ending the simulation. */
		simu.exit();
	}

	@Test
	public void testTorqueDrivenSimulation() throws Exception {

		/* **** Data of the test **** */
		double accDuration = 10 ;
		Vector3D rotVector = new Vector3D(1, 0, 0);
		double torqueIntensity = 0.1 ;
		String torqueKey = "torque";
		/* ************************** */

		NumericalSimulator simu = new NumericalSimulator();
		Dashboard.setDefaultConfiguration();
		Dashboard.setSimulationDuration(accDuration);
		Dashboard.setIntegrationTimeStep(0.1);

		Dashboard.setMemCachedConnection(true, "127.0.0.1:11211");
		Dashboard.setTorqueProvider(TorqueProviderEnum.MEMCACHED);
		Dashboard.setTorqueCommandKey(torqueKey);

		Dashboard.setInitialRotAcceleration(
				new Vector3D(RotAccProvider.computeEulerEquations(
						rotVector.scalarMultiply(torqueIntensity), 
						SatelliteStates.initialSpin, 
						SatelliteBody.satInertiaMatrix)
						)
				);

		Dashboard.checkConfiguration();

		/* Launching the simulation. */
		simu.initialize();

		logger.info(CustomLoggingTools.toString(
				"Initial State of the satellite", 
				simu.getSatellite().getStates().getInitialState()));

		/* Set the torque value in the hash table as an array of double. */
		simu.getIo().getMemcached().set(
				torqueKey, 
				0, 
				rotVector.scalarMultiply(torqueIntensity).toArray()
				);

		simu.process();

		logger.info(CustomLoggingTools.toString(
				"Final State of the satellite", 
				simu.getSatellite().getStates().getCurrentState()));

		simu.exit();

		/* Extracting final state. */
		SpacecraftState finalState = simu.getSatellite().getStates().getCurrentState();

		/* Computing the expected acceleration. */
		double[] expectedRotAcc = RotAccProvider.computeEulerEquations(
				rotVector.scalarMultiply(torqueIntensity),
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
						),				1e-9);
	}



}
