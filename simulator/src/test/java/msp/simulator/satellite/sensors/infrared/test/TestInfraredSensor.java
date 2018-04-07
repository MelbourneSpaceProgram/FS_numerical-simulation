package msp.simulator.satellite.sensors.infrared.test;

import org.hipparchus.geometry.euclidean.threed.*;
import org.hipparchus.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

import msp.simulator.satellite.sensors.InfraredSensor;

public class TestInfraredSensor {

	@Test
	public void testNadirVectorDetermination() {
		InfraredSensor[] sensors = new InfraredSensor[6];
		Vector3D result;
		Vector3D expectedResult = Vector3D.PLUS_I;
		sensors[0] = new InfraredSensor(Vector3D.PLUS_I);
		sensors[0].calculateInfraredReading(expectedResult);
		sensors[5] = new InfraredSensor(Vector3D.PLUS_J);
		sensors[5].calculateInfraredReading(expectedResult);
		sensors[1] = new InfraredSensor(Vector3D.PLUS_K);
		sensors[1].calculateInfraredReading(expectedResult);
		sensors[2] = new InfraredSensor(Vector3D.MINUS_I);
		sensors[2].calculateInfraredReading(expectedResult);
		sensors[4] = new InfraredSensor(Vector3D.MINUS_J);
		sensors[4].calculateInfraredReading(expectedResult);
		sensors[3] = new InfraredSensor(Vector3D.MINUS_K);
		sensors[3].calculateInfraredReading(expectedResult);

		result = nadirVectorDetermination(sensors);
		Assert.assertTrue(result.equals(expectedResult));
	}

	@Test
	public void testAngleToInfrared() {
		InfraredSensor sensor1 = new InfraredSensor(Vector3D.PLUS_I);
		InfraredSensor sensor2 = new InfraredSensor(Vector3D.PLUS_I);
		InfraredSensor sensor3 = new InfraredSensor(Vector3D.PLUS_I);
		/* Angle of 0 */
		double result1 = sensor1.calculateInfraredReading(Vector3D.PLUS_I);
		/* Angle of pi/2 */
		double result2 = sensor2.calculateInfraredReading(Vector3D.PLUS_J);
		/* Angle of pi */
		double result3 = sensor3.calculateInfraredReading(Vector3D.MINUS_I);
		Assert.assertEquals(result1, 0.987208931556143, 1e-6);
		Assert.assertEquals(result2, 0.365192225102539, 1e-6);
		Assert.assertEquals(result3, -8.375071744498586e-4, 1e-6);
	}

	@Test
	public void testAngleFromNadirVector() {
		Vector3D nadirAlongXAxis = Vector3D.PLUS_I;
		Vector3D nadir45BetweenXAndZAxis = new Vector3D(1, 0, 1);
		double expectedAnswer1 = 0;
		double expectedAnswer2 = FastMath.PI;
		double expectedAnswer3 = FastMath.PI / 2;
		double expectedAnswer4 = FastMath.PI / 4;
		double expectedAnswer5 = 3 * FastMath.PI / 4;
		double expectedAnswer6 = FastMath.PI / 4;
		InfraredSensor sensor1 = new InfraredSensor(Vector3D.PLUS_I);
		InfraredSensor sensor2 = new InfraredSensor(Vector3D.MINUS_I);
		InfraredSensor sensor3 = new InfraredSensor(Vector3D.PLUS_K);

		sensor1.calculateInfraredReading(nadirAlongXAxis);
		sensor2.calculateInfraredReading(nadirAlongXAxis);
		sensor3.calculateInfraredReading(nadirAlongXAxis);
		Assert.assertEquals(expectedAnswer1, sensor1.getAngle(), 1e-6);
		Assert.assertEquals(expectedAnswer2, sensor2.getAngle(), 1e-6);
		Assert.assertEquals(expectedAnswer3, sensor3.getAngle(), 1e-6);

		sensor1.calculateInfraredReading(nadir45BetweenXAndZAxis);
		sensor2.calculateInfraredReading(nadir45BetweenXAndZAxis);
		sensor3.calculateInfraredReading(nadir45BetweenXAndZAxis);

		Assert.assertEquals(expectedAnswer4, sensor1.getAngle(), 1e-6);
		Assert.assertEquals(expectedAnswer5, sensor2.getAngle(), 1e-6);
		Assert.assertEquals(expectedAnswer6, sensor3.getAngle(), 1e-6);
	}

	/**
	 * Calculates the Nadir vector based upon readings obtained from the side
	 * sensors
	 * 
	 * @param sensors A set of sensors for a satellite placed on each side of
	 * 		the object
	 * @return nadirVector Vector directed along Nadir for the satellite 
	 * 		orientation
	 */
	public Vector3D nadirVectorDetermination(InfraredSensor[] sensors) {
		Vector3D xComponent, yComponent, zComponent;

		/*
		 * Computes the contribution of sensor readings to the Nadir vector 
		 * along the x-axis, y-axis and finally z-axis
		 */
		xComponent = sensors[0].getSideNormal().scalarMultiply(FastMath.cos(
				sensors[0].getAngle())).subtract(sensors[0].getSideNormal().
						scalarMultiply(FastMath.cos(sensors[2].getAngle())));
		xComponent = xComponent.scalarMultiply(FastMath.pow(
				sensors[0].getSideNormal().getNorm() + sensors[2].
						getSideNormal().getNorm(), -1));
		yComponent = sensors[1].getSideNormal().scalarMultiply(FastMath.cos(
				sensors[1].getAngle())).subtract(sensors[1].getSideNormal().
						scalarMultiply(FastMath.cos(sensors[3].getAngle())));
		yComponent = yComponent.scalarMultiply(FastMath.pow(
				sensors[1].getSideNormal().getNorm() + sensors[3].
						getSideNormal().getNorm(), -1));
		zComponent = sensors[5].getSideNormal().scalarMultiply(FastMath.cos(
				sensors[5].getAngle())).subtract(sensors[5].getSideNormal().
						scalarMultiply(FastMath.cos(sensors[4].getAngle())));
		zComponent = zComponent.scalarMultiply(FastMath.pow(
				sensors[5].getSideNormal().getNorm() + sensors[4].
						getSideNormal().getNorm(), -1));
		return xComponent.add(yComponent).add(zComponent);
	}
}
