
public class Main {
	
	/*
	 * Initialises the inertial frame (reference) axes vectors
	 */
	public static enum SideNormal {
		posXAxis(new int[] { 1, 0, 0 }), posYAxis(new int[] { 0, 1, 0 }), posZAxis(new int[] { 0, 0, 1 }), negXAxis(
				new int[] { -1, 0, 0 }), negYAxis(new int[] { 0, -1, 0 }), negZAxis(new int[] { 0, 0, -1 });
		private int[] numVal;

		SideNormal(int[] numVal) {
			this.numVal = numVal;
		}

		public int[] getVector() {
			return this.numVal;
		}
	}	

	/*
	 * Function to compute the angle of a side normal vector to nadir using a corresponding infrared reading
	 */
	public static double AngleToInfrared(double angle) {
		double result, x = Math.toDegrees(angle), p1 = 1.102e-18, p2 = -7.798e-16, p3 = 2.199e-13, p4 = -3.177e-11,
				p5 = 2.582e-09, p6 = -1.337e-07, p7 = 6.267e-06, p8 = -0.0003285, p9 = 0.003188, p10 = 0.9872;
		/*
		 * Uses a ninth-order polynomial of best fit to the model infrared curve
		 */
		result = p1 * Math.pow(x, 9) + p2 * Math.pow(x, 8) + p3 * Math.pow(x, 7) + p4 * Math.pow(x, 6)
				+ p5 * Math.pow(x, 5) + p6 * Math.pow(x, 4) + p7 * Math.pow(x, 3) + p8 * Math.pow(x, 2) + p9 * x + p10;
		if (result < 0) {
			result = 0;
		}
		return result;
	}

	/*
	 * Function to determine the angle of a nadir vector to each reference axes
	 */
	public static double AngleFromNadirVector(double[] nadir, double[] sideNormal) {
		double result;
		result = Math.acos(DotProduct(nadir, sideNormal) / VectorMagnitude(nadir));
		return result;
	}

	/*
	 * Function to calculate the dot product between two vectors of the same length
	 */
	public static double DotProduct(double[] vectorA, double[] vectorB) {
		double result = 0;
		for (int i = 0; i < vectorA.length; i++) {
			result += vectorA[i] * vectorB[i];
		}
		return result;
	}

	/*
	 * Function to calculate the Euclidean magnitude of a vector
	 */
	public static double VectorMagnitude(double[] vector) {
		double result = 0;
		for (int i = 0; i < vector.length; i++) {
			result += Math.pow(vector[i], 2);
		}
		return Math.sqrt(result);
	}

	/*
	 * Function to scale vectors
	 */
	public static double[] VectorScalarMultiplier(double[] vector, double scalar) {
		double[] result = new double[vector.length];
		for (int i = 0; i < vector.length; i++) {
			result[i] = vector[i] * scalar;
		}
		return result;
	}

	/*
	 * Function to complete two basic operations (addition and subtraction) of vectors
	 */
	public static double[] BasicVectorOperations(double[] vectorA, double[] vectorB, String operation) {
		double[] result = new double[vectorA.length];
		switch (operation) {
		case "add":
			for (int i = 0; i < vectorA.length; i++) {
				result[i] = vectorA[i] + vectorB[i];
			}
			break;
		case "minus":
			for (int i = 0; i < vectorA.length; i++) {
				result[i] = vectorA[i] - vectorB[i];
			}
			break;
		}
		return result;
	}

	/*
	 * Calculates the nadir vector based upon readings obtained from the side sensors
	 */
	public static double[] NadirVectorDetermination(SensorData[] sensors) {
		double[] result;
		double infraredThreshold = 0;

		/*
		 * Removes sensor data that is lower than a user-defined infrared threshold
		 */
		for (int i = 0; i < sensors.length; i++) {
			if (sensors[i].getInfraredReading() < infraredThreshold) {
				sensors[i].setSideNormal(new int[] { 0, 0, 0 });
				sensors[i].setAngleReading(Math.PI / 2);
			}
		}
		/*
		 * Computes the contribution of sensor readings to the nadir vector along the x-axis, y-axis and finally x-axis
		 */
		result = VectorScalarMultiplier(BasicVectorOperations(
				VectorScalarMultiplier(sensors[0].getSideNormal(), Math.cos(sensors[0].getAngleReading())),
				VectorScalarMultiplier(sensors[0].getSideNormal(), Math.cos(sensors[2].getAngleReading())), 
				"minus"), Math.pow((VectorMagnitude(sensors[0].getSideNormal()) 
						+ VectorMagnitude(sensors[2].getSideNormal())), -1));
		result = BasicVectorOperations(result, VectorScalarMultiplier(BasicVectorOperations(
				VectorScalarMultiplier(sensors[1].getSideNormal(), Math.cos(sensors[1].getAngleReading())),
				VectorScalarMultiplier(sensors[1].getSideNormal(), Math.cos(sensors[3].getAngleReading())),
				"minus"), Math.pow((VectorMagnitude(sensors[1].getSideNormal())
						+ VectorMagnitude(sensors[3].getSideNormal())), -1)), "add");
		result = BasicVectorOperations(result, VectorScalarMultiplier(BasicVectorOperations(
				VectorScalarMultiplier(sensors[5].getSideNormal(), Math.cos(sensors[5].getAngleReading())),
				VectorScalarMultiplier(sensors[5].getSideNormal(), Math.cos(sensors[4].getAngleReading())),
				"minus"), Math.pow((VectorMagnitude(sensors[5].getSideNormal())
						+ VectorMagnitude(sensors[4].getSideNormal())), -1)), "add");
		return result;
	}
}