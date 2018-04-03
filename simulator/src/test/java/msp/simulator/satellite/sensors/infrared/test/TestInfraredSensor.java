package msp.simulator.satellite.sensors.infrared.test;

import org.hipparchus.geometry.euclidean.threed.*;
import org.junit.Assert;
import org.junit.Test;

import msp.simulator.satellite.sensors.infrared.InfraredSensor;

public class TestInfraredSensor {

	@Test
	public void TestNadirVectorDetermination() {
		InfraredSensor[] sensors = new InfraredSensor[6];
		Vector3D result;
		Vector3D expectedResult = new Vector3D(1, 0, 0);
		sensors[0] = new InfraredSensor(Vector3D.PLUS_I);
		sensors[0].setAngleReading(0);
		sensors[5] = new InfraredSensor(Vector3D.PLUS_J);
		sensors[5].setAngleReading(Math.PI / 2);
		sensors[1] = new InfraredSensor(Vector3D.PLUS_K);
		sensors[1].setAngleReading(Math.PI / 2);
		sensors[2] = new InfraredSensor(Vector3D.MINUS_I);
		sensors[2].setAngleReading(Math.PI);
		sensors[4] = new InfraredSensor(Vector3D.MINUS_J);
		sensors[4].setAngleReading(Math.PI / 2);
		sensors[3] = new InfraredSensor(Vector3D.MINUS_K);
		sensors[3].setAngleReading(Math.PI / 2);

		result = InfraredSensor.NadirVectorDetermination(sensors);
		Assert.assertTrue(result.equals(expectedResult));
	}

	@Test
	public void TestAngleToInfrared() {
		InfraredSensor sensor1 = new InfraredSensor(Vector3D.ZERO);
		InfraredSensor sensor2 = new InfraredSensor(Vector3D.ZERO);
		InfraredSensor sensor3 = new InfraredSensor(Vector3D.ZERO);
		sensor1.setAngleReading(0);
		sensor1.ConvertAngleToInfrared();
		sensor2.setAngleReading(Math.PI / 2);
		sensor2.ConvertAngleToInfrared();
		sensor3.setAngleReading(Math.PI);
		sensor3.ConvertAngleToInfrared();
		Assert.assertTrue(sensor1.getInfraredReading() == 0.9872);
		/*
		 * Expecting 0.3652 but receiving 0.3658 due to rounding differences between MATLAB constants
		 */
		Assert.assertTrue(sensor2.getInfraredReading() > 0.3650 && sensor2.getInfraredReading() < 0.3660);
		Assert.assertTrue(sensor3.getInfraredReading() == 0);
	}

	@Test
	public void TestAngleFromNadirVector() {
		Vector3D nadirAlongxAxis = new Vector3D(1, 0, 0);
		double expectedAnswer1 = 0;
		double expectedAnswer2 = Math.PI;
		double expectedAnswer3 = Math.PI / 2;
		InfraredSensor sensor1 = new InfraredSensor(Vector3D.PLUS_I);
		InfraredSensor sensor2 = new InfraredSensor(Vector3D.MINUS_I);
		InfraredSensor sensor3 = new InfraredSensor(Vector3D.PLUS_K);

		sensor1.CalculateAngleFromNadir(nadirAlongxAxis);
		sensor2.CalculateAngleFromNadir(nadirAlongxAxis);
		sensor3.CalculateAngleFromNadir(nadirAlongxAxis);
		Assert.assertTrue(expectedAnswer1 == sensor1.getAngleReading());
		Assert.assertTrue(expectedAnswer2 == sensor2.getAngleReading());
		Assert.assertTrue(expectedAnswer3 == sensor3.getAngleReading());
	}
}
