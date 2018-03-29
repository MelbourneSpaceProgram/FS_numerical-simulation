public class SensorData {

	private double infraredReading, angleReading;
	private int[] sideNormal;

	public SensorData(int[] sideNormalVector) {
		infraredReading = 0;
		angleReading = 0;
		sideNormal = sideNormalVector;
	}

	public void CalculateAngleFromNadir(double[] nadir) {
		angleReading = Main.AngleFromNadirVector(nadir, getSideNormal());
	}

	public void ConvertAngleToInfrared() {
		infraredReading = Main.AngleToInfrared(angleReading);
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

	public double[] getSideNormal() {
		double[] result = new double[sideNormal.length];
		for (int i = 0; i < sideNormal.length; i++) {
			result[i] = (double)sideNormal[i];
		}
		return result;
	}
	
	public void setSideNormal(int[] value) {
		sideNormal = value;
	}
}
