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

import java.nio.ByteBuffer;

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
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class TestSatellite {

	/** Logger of the instance. */
	private static final Logger logger = LoggerFactory.getLogger(TestSatellite.class);

	@Test
	public void testMemcachedConnection() throws Exception {

		Dashboard.setDefaultConfiguration();

		Dashboard.setMemCachedConnection(true, "127.0.0.1:11211");
		Dashboard.setSimulationDuration(1);


		/* *** Creating and launching the simulation. *** */
		NumericalSimulator simu = new NumericalSimulator();
		simu.initialize();

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
		long accDuration = 100 ;
		Vector3D rotVector = new Vector3D(1, 0, 0);
		double torqueIntensity = 0.1 ;
		String torqueKey = MemCachedTorqueProvider.torqueCommandKey;
		/* ************************** */

		Dashboard.setDefaultConfiguration();
		Dashboard.setRealTimeProcessing(false);
		Dashboard.setSimulationDuration(accDuration);
		Dashboard.setIntegrationTimeStep(0.1);
		Dashboard.setGroundStationWorkPeriod(10);
		Dashboard.setInitialSpin(Vector3D.ZERO);

		Dashboard.setMemCachedConnection(true, "127.0.0.1:11211");
		Dashboard.setCommandTorqueProvider(TorqueProviderEnum.MEMCACHED);
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
		NumericalSimulator simu = new NumericalSimulator();
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
				1e-2);

		/* Checking Spin */
		Assert.assertArrayEquals(
				new Vector3D(expectedRotAcc).scalarMultiply(accDuration).toArray(), 
				SecondaryStates.extractState(
						finalState.getAdditionalState(SecondaryStates.key), 
						SecondaryStates.SPIN
						),				
				1e-2);

		/* Check that the MMT sensor data are well exported to the common memory. */
		Assert.assertArrayEquals(
				/* Note that getData always introduces a noise in the measure. */
				simu.getSatellite().getSensors().getMagnetometer().getData_magField().toArray(), 
				new Vector3D(
						ByteBuffer.wrap(
								simu.getIo().getMemcached().get(
										"Simulation_Magnetometer_X",
										simu.getIo().getRawTranscoder()
										)).getDouble(),
						ByteBuffer.wrap(
								simu.getIo().getMemcached().get(
										"Simulation_Magnetometer_Y",
										simu.getIo().getRawTranscoder()
										)).getDouble(),
						ByteBuffer.wrap(
								simu.getIo().getMemcached().get(
										"Simulation_Magnetometer_Z",
										simu.getIo().getRawTranscoder()
										)).getDouble()
						).toArray(),
				/* Converting to Tesla. */
				/* Note that we compare a noisy value with another, so we potentially add
				 * twice the noise in the worst case. */
				2 * simu.getSatellite().getSensors().getMagnetometer().getNoiseIntensity() * 1e-9 
				);

		/* End the simulation and the test. */
		simu.exit();
	}

	/**
	 * Test the existence of the instance and its basic behavior.
	 */
	@Test
	public void testMagnetometerExistence() {
		/* Set up the simulation. */
		NumericalSimulator simu = new NumericalSimulator();
		Dashboard.setDefaultConfiguration();
		Dashboard.setMagnetometerNoiseIntensity(1e2);

		try {
			simu.initialize();

			/* Extract the Magnetometer. */
			Magnetometer mmt = simu.getSatellite().getSensors().getMagnetometer();
			Assert.assertNotNull(mmt);

			/* Retrieve the inital measure of the magnetic field. */
			GeoMagneticElements initialPerfectField = mmt.retrievePerfectField();
			GeoMagneticElements initialNoisyField = mmt.retrieveNoisyField();

			Assert.assertArrayEquals(
					initialPerfectField.getFieldVector().toArray(), 
					initialNoisyField.getFieldVector().toArray(), 
					mmt.getNoiseIntensity()
					);

			simu.process();

			GeoMagneticElements finalPerfectField = mmt.retrievePerfectField();
			Assert.assertNotEquals(
					initialPerfectField, 
					finalPerfectField
					);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
