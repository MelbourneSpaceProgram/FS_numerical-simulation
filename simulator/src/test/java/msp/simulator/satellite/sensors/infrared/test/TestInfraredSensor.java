package msp.simulator.satellite.sensors.infrared.test;

import org.hipparchus.geometry.euclidean.threed.*;
import org.hipparchus.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

import msp.simulator.satellite.sensors.infrared.InfraredSensor;

public class TestInfraredSensor {

	@Test
	public void TestNadirVectorDetermination() {
		InfraredSensor[] sensors = new InfraredSensor[6];
		Vector3D result;
		Vector3D expectedResult = Vector3D.PLUS_I;
		sensors[0] = new InfraredSensor(Vector3D.PLUS_I);
		sensors[0].CalculateInfraredReading(expectedResult);
		sensors[5] = new InfraredSensor(Vector3D.PLUS_J);
		sensors[5].CalculateInfraredReading(expectedResult);
		sensors[1] = new InfraredSensor(Vector3D.PLUS_K);
		sensors[1].CalculateInfraredReading(expectedResult);
		sensors[2] = new InfraredSensor(Vector3D.MINUS_I);
		sensors[2].CalculateInfraredReading(expectedResult);
		sensors[4] = new InfraredSensor(Vector3D.MINUS_J);
		sensors[4].CalculateInfraredReading(expectedResult);
		sensors[3] = new InfraredSensor(Vector3D.MINUS_K);
		sensors[3].CalculateInfraredReading(expectedResult);

		result = NadirVectorDetermination(sensors);
		Assert.assertTrue(result.equals(expectedResult));
	}

	@Test
	public void TestAngleToInfrared() {
		InfraredSensor sensor1 = new InfraredSensor(Vector3D.PLUS_I);
		InfraredSensor sensor2 = new InfraredSensor(Vector3D.PLUS_I);
		InfraredSensor sensor3 = new InfraredSensor(Vector3D.PLUS_I);
		/* Angle of 0 */
		double result1 = sensor1.CalculateInfraredReading(Vector3D.PLUS_I);
		/* Angle of pi/2 */
		double result2 = sensor2.CalculateInfraredReading(Vector3D.PLUS_J);
		/* Angle of pi */
		double result3 = sensor3.CalculateInfraredReading(Vector3D.MINUS_I);
		Assert.assertTrue(result1 == 0.9872);
		/*
		 * Expecting 0.3652 but receiving 0.3658 due to rounding differences between
		 * MATLAB constants
		 */
		Assert.assertTrue(result2 > 0.3650 && result2 < 0.3660);
		Assert.assertTrue(result3 == 0);
	}

	@Test
	public void TestAngleFromNadirVector() {
		Vector3D nadirAlongxAxis = Vector3D.PLUS_I;
		double expectedAnswer1 = 0;
		double expectedAnswer2 = Math.PI;
		double expectedAnswer3 = Math.PI / 2;
		InfraredSensor sensor1 = new InfraredSensor(Vector3D.PLUS_I);
		InfraredSensor sensor2 = new InfraredSensor(Vector3D.MINUS_I);
		InfraredSensor sensor3 = new InfraredSensor(Vector3D.PLUS_K);

		sensor1.CalculateInfraredReading(nadirAlongxAxis);
		sensor2.CalculateInfraredReading(nadirAlongxAxis);
		sensor3.CalculateInfraredReading(nadirAlongxAxis);
		Assert.assertTrue(expectedAnswer1 == sensor1.getAngleReading());
		Assert.assertTrue(expectedAnswer2 == sensor2.getAngleReading());
		Assert.assertTrue(expectedAnswer3 == sensor3.getAngleReading());
	}
	
	/**
	 * Calculates the Nadir vector based upon readings obtained from the side
	 * sensors
	 * 
	 * @param sensors
	 *            A set of sensors for a satellite placed on each side of the object
	 * @return nadirVector Vector directed along Nadir for the satellite orientation
	 */
	public Vector3D NadirVectorDetermination(InfraredSensor[] sensors) {
		Vector3D xComponent, yComponent, zComponent;

		/*
		 * Computes the contribution of sensor readings to the Nadir vector along the
		 * x-axis, y-axis and finally x-axis
		 */
		xComponent = sensors[0].getSideNormal().scalarMultiply(FastMath.cos(sensors[0].getAngleReading()))
				.subtract(sensors[0].getSideNormal().scalarMultiply(FastMath.cos(sensors[2].getAngleReading())));
		xComponent = xComponent.scalarMultiply(
				FastMath.pow(sensors[0].getSideNormal().getNorm() + sensors[2].getSideNormal().getNorm(), -1));
		yComponent = sensors[1].getSideNormal().scalarMultiply(FastMath.cos(sensors[1].getAngleReading()))
				.subtract(sensors[1].getSideNormal().scalarMultiply(FastMath.cos(sensors[3].getAngleReading())));
		yComponent = yComponent.scalarMultiply(
				FastMath.pow(sensors[1].getSideNormal().getNorm() + sensors[3].getSideNormal().getNorm(), -1));
		zComponent = sensors[5].getSideNormal().scalarMultiply(FastMath.cos(sensors[5].getAngleReading()))
				.subtract(sensors[5].getSideNormal().scalarMultiply(FastMath.cos(sensors[4].getAngleReading())));
		zComponent = zComponent.scalarMultiply(
				FastMath.pow(sensors[5].getSideNormal().getNorm() + sensors[4].getSideNormal().getNorm(), -1));
		return xComponent.add(yComponent).add(zComponent);
	}
}
