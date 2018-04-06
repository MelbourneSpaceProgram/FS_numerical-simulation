package msp.simulator.satellite.sensors.infrared.test;

import org.hipparchus.geometry.euclidean.threed.*;
import org.hipparchus.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

import msp.simulator.satellite.sensors.InfraredSensor;

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
		Assert.assertEquals(result1, 0.987208931556143, 1e-6);
		Assert.assertEquals(result2,  0.365192225102539, 1e-6);
		Assert.assertEquals(result3, -8.375071744498586e-4, 1e-6);
	}

	@Test
	public void TestAngleFromNadirVector() {
		Vector3D nadirAlongXAxis = Vector3D.PLUS_I;
		double expectedAnswer1 = 0;
		double expectedAnswer2 = FastMath.PI;
		double expectedAnswer3 = FastMath.PI / 2;
		double expectedAnswer4 = FastMath.PI / 4;
		double expectedAnswer5 = 3 * FastMath.PI / 4;
		InfraredSensor sensor1 = new InfraredSensor(Vector3D.PLUS_I);
		InfraredSensor sensor2 = new InfraredSensor(Vector3D.MINUS_I);
		InfraredSensor sensor3 = new InfraredSensor(Vector3D.PLUS_K);
		InfraredSensor sensor4 = new InfraredSensor(new Vector3D(FastMath.sqrt(2), 0, FastMath.sqrt(2)));
		InfraredSensor sensor5 = new InfraredSensor(new Vector3D(-FastMath.sqrt(2), -FastMath.sqrt(2), 0));

		sensor1.CalculateInfraredReading(nadirAlongXAxis);
		sensor2.CalculateInfraredReading(nadirAlongXAxis);
		sensor3.CalculateInfraredReading(nadirAlongXAxis);
		sensor4.CalculateInfraredReading(nadirAlongXAxis);
		sensor5.CalculateInfraredReading(nadirAlongXAxis);
		Assert.assertTrue(expectedAnswer1 == sensor1.getAngle());
		Assert.assertTrue(expectedAnswer2 == sensor2.getAngle());
		Assert.assertTrue(expectedAnswer3 == sensor3.getAngle());
		Assert.assertTrue(expectedAnswer4 == sensor4.getAngle());
		Assert.assertTrue(expectedAnswer5 == sensor5.getAngle());
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
		 * x-axis, y-axis and finally z-axis
		 */
		xComponent = sensors[0].getSideNormal().scalarMultiply(FastMath.cos(sensors[0].getAngle()))
				.subtract(sensors[0].getSideNormal().scalarMultiply(FastMath.cos(sensors[2].getAngle())));
		xComponent = xComponent.scalarMultiply(
				FastMath.pow(sensors[0].getSideNormal().getNorm() + sensors[2].getSideNormal().getNorm(), -1));
		yComponent = sensors[1].getSideNormal().scalarMultiply(FastMath.cos(sensors[1].getAngle()))
				.subtract(sensors[1].getSideNormal().scalarMultiply(FastMath.cos(sensors[3].getAngle())));
		yComponent = yComponent.scalarMultiply(
				FastMath.pow(sensors[1].getSideNormal().getNorm() + sensors[3].getSideNormal().getNorm(), -1));
		zComponent = sensors[5].getSideNormal().scalarMultiply(FastMath.cos(sensors[5].getAngle()))
				.subtract(sensors[5].getSideNormal().scalarMultiply(FastMath.cos(sensors[4].getAngle())));
		zComponent = zComponent.scalarMultiply(
				FastMath.pow(sensors[5].getSideNormal().getNorm() + sensors[4].getSideNormal().getNorm(), -1));
		return xComponent.add(yComponent).add(zComponent);
	}
}
