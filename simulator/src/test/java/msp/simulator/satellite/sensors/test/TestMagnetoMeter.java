/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite.sensors.test;

import org.junit.Assert;
import org.junit.Test;
import org.orekit.models.earth.GeoMagneticElements;

import msp.simulator.NumericalSimulator;
import msp.simulator.satellite.sensors.Magnetometer;
import msp.simulator.user.Dashboard;

/**
 * Unit Tests of the magnetometer component.
 * @author Florian CHAUBEYRE
 */
public class TestMagnetoMeter {

	/**
	 * Test the existence of the instance and its basic behavior.
	 */
	@Test
	public void testExistence() {
		/* Set up the simulation. */
		NumericalSimulator simu = new NumericalSimulator();
		Dashboard.setDefaultConfiguration();
		Dashboard.setMagnetometerNoiseIntensity(Magnetometer.defaultNoiseIntensity);
		
		try {
			simu.initialize();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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

	}
	
	
}
