/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.test;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.junit.Assert;
import org.junit.Test;
import org.orekit.models.earth.GeoMagneticElements;
import org.orekit.propagation.SpacecraftState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.NumericalSimulator;
import msp.simulator.dynamic.propagation.integration.RotAccProvider;
import msp.simulator.dynamic.propagation.integration.SecondaryStates;
import msp.simulator.dynamic.torques.MemCachedTorqueProvider;
import msp.simulator.dynamic.torques.TorqueProviderEnum;
import msp.simulator.satellite.assembly.SatelliteBody;
import msp.simulator.satellite.assembly.SatelliteStates;
import msp.simulator.satellite.io.MemcachedRawTranscoder;
import msp.simulator.satellite.sensors.Magnetometer;
import msp.simulator.user.Dashboard;
import net.spy.memcached.MemcachedClient;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class TestSatellite {

	/** Logger of the instance. */
	private static final Logger logger = LoggerFactory.getLogger(TestSatellite.class);

	@Test
	public void testMemcachedConnection() {
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

		String message = "I am alive!";
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
	public void testMemcachedTorqueDrivenSimulation() throws Exception {

		/* **** Data of the test **** */
		double accDuration = 10 ;
		Vector3D rotVector = new Vector3D(1, 0, 0);
		double torqueIntensity = 0.1 ;
		String torqueKey = MemCachedTorqueProvider.torqueCommandKey;
		/* ************************** */

		NumericalSimulator simu = new NumericalSimulator();
		Dashboard.setDefaultConfiguration();
		Dashboard.setSimulationDuration(accDuration);
		Dashboard.setIntegrationTimeStep(1.0);

		Dashboard.setMemCachedConnection(true, "127.0.0.1:11211");
		Dashboard.setTorqueProvider(TorqueProviderEnum.MEMCACHED);
		Dashboard.setTorqueCommandKey(torqueKey);

		Dashboard.setInitialRotAcceleration(
				new Vector3D(
						RotAccProvider.computeEulerEquations(
								rotVector.scalarMultiply(torqueIntensity), 
								SatelliteStates.initialSpin, 
								SatelliteBody.satInertiaMatrix)
						)
				);
		Dashboard.setInitialRotAcceleration(new Vector3D(0.0, 0.0, 0.0));


		Dashboard.checkConfiguration();

		/* Launching the simulation. */
		simu.initialize();

		/* Set the torque value in the hash table as an array of double. */
		MemcachedClient memcached = simu.getIo().getMemcached();
		
		memcached.set(torqueKey + "X", 0, MemcachedRawTranscoder.toRawByteArray(
				rotVector.scalarMultiply(torqueIntensity).getX()));
		memcached.set(torqueKey + "Y", 0, MemcachedRawTranscoder.toRawByteArray(
				rotVector.scalarMultiply(torqueIntensity).getY()));
		memcached.set(torqueKey + "Z", 0, MemcachedRawTranscoder.toRawByteArray(
				rotVector.scalarMultiply(torqueIntensity).getZ()));

		simu.process();

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

	/**
	 * Test the existence of the instance and its basic behavior.
	 */
	@Test
	public void testMagnetometerExistence() {
		/* Set up the simulation. */
		NumericalSimulator simu = new NumericalSimulator();
		Dashboard.setDefaultConfiguration();
		Dashboard.setMagnetometerNoiseIntensity(Magnetometer.defaultNoiseIntensity);

		try {
			simu.initialize();

			/* Extract the Magnetometer. */
			Magnetometer mmt = simu.getSatellite().getSensors().getMagnetometer();
			Assert.assertNotNull(mmt);

			/* Retrieve the inital measure of the magnetic field. */
			GeoMagneticElements initialPerfectMeasure = mmt.retrievePerfectMeasurement();
			GeoMagneticElements initialNoisyMeasure = mmt.retrieveNoisyMeasurement();

			Assert.assertArrayEquals(
					initialPerfectMeasure.getFieldVector().toArray(), 
					initialNoisyMeasure.getFieldVector().toArray(), 
					mmt.getNoiseIntensity()
					);

			simu.process();

			GeoMagneticElements finalPerfectMeasure = mmt.retrievePerfectMeasurement();
			Assert.assertNotEquals(
					initialPerfectMeasure, 
					finalPerfectMeasure
					);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
