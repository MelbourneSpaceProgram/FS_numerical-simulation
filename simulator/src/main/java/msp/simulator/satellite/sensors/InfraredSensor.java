package msp.simulator.satellite.sensors;

import org.hipparchus.geometry.euclidean.threed.*;
import org.hipparchus.util.FastMath;

/**
 * This class represents the infrared sensor of the satellite
 *
 * @author Braeden BORG
 */
public class InfraredSensor {

	/**
	 * Constants determined for a ninth-order polynomial that best represents the
	 * model curve for infrared readings versus angle from Nadir
	 */
	private static final double p1 = 0.0073, p2 = -0.0906, p3 = 0.4458, p4 = -1.124, p5 = 1.595, p6 = -1.441,
			p7 = 1.179, p8 = -1.078, p9 = 0.1827, p10 = 0.9872;
	private double infraredReading, angleReading;
	private Vector3D sideNormal;

	/**
	 * Creates an infrared sensor for a satellite side with an infrared reading and
	 * associated angle to nadir
	 * 
	 * @param sideNormalVector
	 *            Vector normal to a satellite side
	 */
	public InfraredSensor(Vector3D sideNormalVector) {
		infraredReading = 0;
		angleReading = 0;
		sideNormal = sideNormalVector;
	}

	/**
	 * Function to compute the infrared reading for a face of the satellite given
	 * the angle to Nadir
	 * 
	 * @param angle
	 *            Angle to Nadir vector along an axis plane
	 * @return result The corresponding infrared reading for the angle to Nadir
	 */
	private double AngleToInfrared(double angle) {
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

	/**
	 * Function to determine the angle of a Nadir vector to each reference axes
	 * 
	 * @param nadir
	 *            Known Nadir vector
	 * @param sideNormal
	 *            Vector normal to a satellite side
	 * @return Angle to Nadir along an axis plane
	 */
	private double AngleFromNadirVector(Vector3D nadir, Vector3D sideNormal) {
		return FastMath.acos(Vector3D.dotProduct(nadir, sideNormal) / nadir.getNorm());
	}
	
	/**
	 * Function to initialise the infrared sensor and all of its values for a given Nadir vector
	 * 
	 * @param nadir	Known Nadir vector
	 * @return infraredReading Infrared reading of the sensor based upon its relationship with the angle to Nadir
	 */
	public double CalculateInfraredReading(Vector3D nadir) {
		angleReading = AngleFromNadirVector(nadir, sideNormal);
		infraredReading = AngleToInfrared(angleReading);
		return infraredReading;
	}

	/*
	 * Series of get and set functions for InfraredSensor
	 */
	public double getAngleReading() {
		return angleReading;
	}

	public Vector3D getSideNormal() {
		return sideNormal;
	}
}
