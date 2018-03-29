import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class JUnitTests {

	@Test
	void TestNadirVectorDetermination() {
		SensorData[] sensors = new SensorData[6];
		for (int i = 0; i < sensors.length; i++) {
			sensors[i] = new SensorData(new int[] { 0, 0, 0 });
		}
		double[] result, expectedResult = new double[] { 1, 0, 0 };
		sensors[0].setSideNormal(Main.SideNormal.posXAxis.getVector());
		sensors[0].setAngleReading(0);
		sensors[5].setSideNormal(Main.SideNormal.posYAxis.getVector());
		sensors[5].setAngleReading(Math.PI / 2);
		sensors[1].setSideNormal(Main.SideNormal.posZAxis.getVector());
		sensors[1].setAngleReading(Math.PI / 2);
		sensors[2].setSideNormal(Main.SideNormal.negXAxis.getVector());
		sensors[2].setAngleReading(Math.PI);
		sensors[4].setSideNormal(Main.SideNormal.negYAxis.getVector());
		sensors[4].setAngleReading(Math.PI / 2);
		sensors[3].setSideNormal(Main.SideNormal.negZAxis.getVector());
		sensors[3].setAngleReading(Math.PI / 2);
		for (int i = 0; i < sensors.length; i++) {
			sensors[i].setInfraredReading(Main.AngleToInfrared(sensors[i].getAngleReading()));
		}

		result = Main.NadirVectorDetermination(sensors);
		assertTrue(Arrays.equals(result, expectedResult));
	}

	@Test
	void TestAngleToInfrared() {
		double angle1 = 0, angle2 = Math.PI / 2, angle3 = Math.PI, result1, result2, result3;

		result1 = Main.AngleToInfrared(angle1);
		result2 = Main.AngleToInfrared(angle2);
		result3 = Main.AngleToInfrared(angle3);
		assertTrue(result1 > 0.9);
		assertTrue(result2 < 0.45);
		assertTrue(result3 < 0.1 && result3 >= 0);
	}

	@Test
	void TestAngleFromNadirVector() {
		double[] nadirAlongxAxis = { 1, 0, 0 };
		double result1, result2, result3, expectedAnswer1 = 0, expectedAnswer2 = Math.PI, expectedAnswer3 = Math.PI / 2;
		SensorData sensor1 = new SensorData(Main.SideNormal.posXAxis.getVector());
		SensorData sensor2 = new SensorData(Main.SideNormal.negXAxis.getVector());
		SensorData sensor3 = new SensorData(Main.SideNormal.posZAxis.getVector());

		result1 = Main.AngleFromNadirVector(nadirAlongxAxis, sensor1.getSideNormal());
		result2 = Main.AngleFromNadirVector(nadirAlongxAxis, sensor2.getSideNormal());
		result3 = Main.AngleFromNadirVector(nadirAlongxAxis, sensor3.getSideNormal());
		assertEquals(result1, expectedAnswer1);
		assertEquals(result2, expectedAnswer2);
		assertEquals(result3, expectedAnswer3);
	}

	@Test
	void TestDotProduct() {
		double[] vectorA = { 1, 2, 3 }, vectorB = { 3, 2, 1 };
		double expectedResult = 10, result;

		result = Main.DotProduct(vectorA, vectorB);
		assertEquals(result, expectedResult);
	}

	@Test
	void TestVectorMagnitude() {
		double[] vector = { 3, 4, 0 };
		double expectedResult = 5, result;

		result = Main.VectorMagnitude(vector);
		assertEquals(result, expectedResult);
	}
}
