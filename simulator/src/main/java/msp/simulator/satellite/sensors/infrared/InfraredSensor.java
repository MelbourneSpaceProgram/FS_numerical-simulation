package msp.simulator.satellite.sensors.infrared;

import org.hipparchus.geometry.euclidean.threed.*;
import org.hipparchus.util.FastMath;

public class InfraredSensor {

	private static final double p1 = 0.0073, p2 = -0.0906, p3 = 0.4458, p4 = -1.124, p5 = 1.595, p6 = -1.441,
			p7 = 1.179, p8 = -1.078, p9 = 0.1827, p10 = 0.9872;
	private double infraredReading, angleReading;
	private Vector3D sideNormal;

	/*
	 * Creates an infrared sensor for a satellite side with an infrared reading and
	 * associated angle to nadir
	 */
	public InfraredSensor(Vector3D sideNormalVector) {
		infraredReading = 0;
		angleReading = 0;
		sideNormal = sideNormalVector;
	}

	/*
	 * Function to compute the infrared reading for a face of the satellite given
	 * the angle to Nadir
	 */
	private static double AngleToInfrared(double angle) {
		double result, x = angle;
		/*
		 * Uses a ninth-order polynomial of best fit to the model infrared curve
		 */
		result = p1 * FastMath.pow(x, 9) + p2 * FastMath.pow(x, 8) + p3 * FastMath.pow(x, 7) + p4 * FastMath.pow(x, 6)
				+ p5 * FastMath.pow(x, 5) + p6 * FastMath.pow(x, 4) + p7 * FastMath.pow(x, 3) + p8 * FastMath.pow(x, 2)
				+ p9 * x + p10;

		/*
		 * Values calculated at an angle of pi (180 degrees) can be negative but are
		 * very small in magnitude
		 */
		if (result < 0) {
			result = 0;
		}
		return result;
	}

	/*
	 * Function to determine the angle of a Nadir vector to each reference axes
	 */
	private static double AngleFromNadirVector(Vector3D nadir, Vector3D sideNormal) {
		return FastMath.acos(Vector3D.dotProduct(nadir, sideNormal) / nadir.getNorm());
	}

	/*
	 * Calculates the Nadir vector based upon readings obtained from the side
	 * sensors
	 */
	public static final Vector3D NadirVectorDetermination(InfraredSensor[] sensors) {
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

	/*
	 * Series of get and set functions for InfraredSensor
	 */
	public void CalculateAngleFromNadir(Vector3D nadir) {
		angleReading = AngleFromNadirVector(nadir, sideNormal);
	}

	public void ConvertAngleToInfrared() {
		infraredReading = AngleToInfrared(angleReading);
	}

	public double getInfraredReading() {
		return infraredReading;
	}

	public void setInfraredReading(double value) {
		infraredReading = value;
	}

	public double getAngleReading() {
		return angleReading;
	}

	public void setAngleReading(double value) {
		angleReading = value;
	}

	public Vector3D getSideNormal() {
		return sideNormal;
	}

	public void setSideNormal(Vector3D value) {
		sideNormal = value;
	}
}
