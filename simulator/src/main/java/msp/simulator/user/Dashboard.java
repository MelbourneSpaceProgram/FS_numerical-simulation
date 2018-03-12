/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.user;

import java.util.ArrayList;

import org.hipparchus.complex.Quaternion;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.NumericalSimulator;
import msp.simulator.dynamic.propagation.Propagation;
import msp.simulator.dynamic.torques.AutomaticTorqueLaw;
import msp.simulator.environment.orbit.Orbit;
import msp.simulator.environment.orbit.Orbit.OrbitalParameters;
import msp.simulator.satellite.assembly.SatelliteBody;
import msp.simulator.satellite.assembly.SatelliteStates;
import msp.simulator.satellite.io.IO;
import msp.simulator.satellite.sensors.Magnetometer;
import msp.simulator.utils.logs.CustomLoggingTools;
import msp.simulator.utils.logs.ephemeris.EphemerisGenerator;

/**
 * This class handles the user-configuration 
 * of the numerical simulator.
 * <p>
 * The configuration has to be set before any
 * simulation initialization and can be used
 * anywhere by the user as the provided method 
 * are static.
 * <p>
 * The configuration setting relies on the fact 
 * that any static attribute of a class is created 
 * prior to the  usual instanciation of the object.
 * So we can instanciate an object of the simulation
 * with different settings as soon as they are defined 
 * as public static.
 * <p>
 * Note the fact that those parameters are public only
 * influences the initialization and are not used within
 * the main processing.
 *
 * @author Florian CHAUBEYRE
 */
public class Dashboard {	

	/** Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(
			Dashboard.class);

	/** Set the Configuration of the Simulation to the default Settings. */
	public static void setDefaultConfiguration() {
		logger.info(CustomLoggingTools.indentMsg(logger, 
				"Setting Default Configuration..."));

		Dashboard.setIntegrationTimeStep(0.1);
		Dashboard.setEphemerisTimeStep(1.0);
		Dashboard.setSimulationDuration(10.0);
		Dashboard.setOrbitalParameters(new OrbitalParameters(
				575000, 	
				0,
				0,
				FastMath.toRadians(98),
				FastMath.toRadians(269.939),
				FastMath.toRadians(0),
				"2018-12-21T22:23:00.000"
				));
		Dashboard.setEphemerisFilesPath(EphemerisGenerator.DEFAULT_PATH);
		Dashboard.setSatBoxSizeWithNoSolarPanel(new double[] {0.01, 0.01, 0.01});
		Dashboard.setInitialAttitudeQuaternion(1, 0, 0, 0);
		Dashboard.setInitialSpin(new Vector3D(
				0.0,
				0.0,
				0.0	
				));
		Dashboard.setInitialRotAcceleration(new Vector3D(
				0.0,
				0.0,
				0.0	
				));

		@SuppressWarnings("unused")
		double[][] trueSatInertiaMatrix =  /* kg.m^2 */ {
				{1191.648 * 1.3e-6,           0       ,           0        },
				{         0       ,  1169.506 * 1.3e-6,           0        },
				{         0       ,           0       ,  1203.969 * 1.3e-6 },
		};

		double[][] simpleBalancedInertiaMatrix = {
				{ 1,   0,   0 },
				{ 0,   1,   0 },
				{ 0,   0,   1 }
		};
		Dashboard.setSatelliteInertiaMatrix(simpleBalancedInertiaMatrix);
		Dashboard.setTorqueScenario(new ArrayList<AutomaticTorqueLaw.Step>());

		Dashboard.setMagnetometerNoiseIntensity(Magnetometer.defaultNoiseIntensity);
		
		/* **** IO Settings **** */
		Dashboard.setMemCachedConnection(IO.connectMemCached, IO.memcachedSocketAddress);
		
		try {
			Dashboard.checkConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the integration time step of the different integrations
	 * used on the simulation (Attitude and Main PVT)
	 * @param step in seconds and strictly positive
	 */
	public static void setIntegrationTimeStep(double step) {
		if (step > 0) {
			Propagation.integrationTimeStep = step;
		} else {
			logger.error("Wrong time step - need to be strictly positive."
					+ " (value = " + step);
		}
	}

	/**
	 * Set the ephemeris time step.
	 * @param step in seconds and strictly positive.
	 */
	public static void setEphemerisTimeStep(double step) {
		if (step > 0) {
			EphemerisGenerator.ephemerisTimeStep = step;
		}
		else {
			logger.error("Wrong time step - need to be strictly positive."
					+ " (value = " + step);
		}
	}

	/**
	 * Set the time duration of the simulation to process.
	 * <p>
	 * The double max value <i>(Double.MAX_VALUE)</i> states
	 * for an "infinite loop" duration.
	 * @param duration in seconds
	 */
	public static void setSimulationDuration(double duration) {
		NumericalSimulator.simulationDuration = duration ; /* s. */
	}

	/**
	 * Set the orbital parameters required to define the orbit in
	 * the simulator.
	 * @param param The appropriate orbital parameters
	 * @see msp.simulator.environment.orbit.Orbit.OrbitalParameters
	 */
	public static void setOrbitalParameters(OrbitalParameters param) {
		Orbit.userOrbitalParameters = param;
	}

	/**
	 * Set the size of the satellite box without solar panel.
	 * @param xyzSize a three-dimension array (x, y, z) in meter.
	 */
	public static void setSatBoxSizeWithNoSolarPanel(double[] xyzSize) {
		SatelliteBody.satBoxSizeWithNoSolarPanel = xyzSize;
	}

	/**
	 * Set the initial attitude quaternion. (Representing the rotation
	 * from the inertial frame to the satellite frame).
	 * This method normalize the quaternion.
	 * @param q0 Scalar part
	 * @param q1 First Vector Part
	 * @param q2 Second Vector Part
	 * @param q3 Third Vector Part
	 */
	public static void setInitialAttitudeQuaternion(
			double q0, double q1, double q2, double q3) {
		SatelliteStates.initialAttitudeQuaternion = new Quaternion(q0, q1, q2, q3).normalize();
	}

	/**
	 * Set the initial spin of the satellite.
	 * @param spin Vector in the space.
	 */
	public static void setInitialSpin(Vector3D spin) {
		SatelliteStates.initialSpin = spin;
	}

	/**
	 * Set the initial rotational acceleration of the satellite.
	 * @param accRot Vector in the space.
	 */
	public static void setInitialRotAcceleration(Vector3D accRot) {
		SatelliteStates.initialRotAcceleration = accRot;
	}

	/**
	 * Set the user-defined satellite mass.
	 * @param mass in kilogram
	 */
	public static void setSatelliteMass(double mass) {
		SatelliteBody.satelliteMass = mass;
	}

	/**
	 * Set the user-specified inertia matrix of the satellite.
	 * @param iMatrix Inertia Matrix to be set
	 */
	public static void setSatelliteInertiaMatrix(double[][] iMatrix) {
		SatelliteBody.satInertiaMatrix = iMatrix;
	}

	/**
	 * Set the file path of the output ephemeris.
	 * @param newPath Absolute path to the ephemeris.
	 */
	public static void setEphemerisFilesPath(String newPath) {
		EphemerisGenerator.DEFAULT_PATH = newPath;
	}

	/**
	 * Set the automatic torque provider to the user-defined one.
	 * @param scenario steps of the torque law over time.
	 */
	public static void setTorqueScenario(ArrayList<AutomaticTorqueLaw.Step> scenario) {
		AutomaticTorqueLaw.TORQUE_SCENARIO = new ArrayList<AutomaticTorqueLaw.Step>(
				scenario);
	}

	/**
	 * Set the normally distributed noise intensity of the magnetometer.
	 * @param noiseIntensity order of intensity
	 */
	public static void setMagnetometerNoiseIntensity(double noiseIntensity) {
		Magnetometer.defaultNoiseIntensity = noiseIntensity;
	}

	/* ********************************************************* */
	/* *****************		 IO SETTINGS		 ****************** */
	/* ********************************************************* */
	
	/**
	 * Setting the IO MemCached connection.
	 * @param active True to setting up the connection.
	 * Note that the MemCached server should be already running.
	 * @param host Address of the socket.
	 * The format should be "add.add.add.add:port"
	 */
	public static void setMemCachedConnection(boolean active, String host) {
		IO.connectMemCached = active;
		IO.memcachedSocketAddress = host;
	}
	
	
	/* ********************************************************* */
	/* *****************		CHECK METHODS	 ****************** */
	/* ********************************************************* */
	
	/**
	 * Check the user-defined configuration.
	 * @throws Exception if an error is detected.
	 */
	public static void checkConfiguration() throws Exception {
		boolean mainStatus = true;
		boolean status = true;

		/* Check n°1 */
		status = EphemerisGenerator.ephemerisTimeStep <= NumericalSimulator.simulationDuration;
		if (!status) {
			logger.error("The ephemeris time step should be inferior or equal than the "
					+ "simulation duration."
					+ "\n"
					+ "\t\tEphemeris: {} s. > {} s. :Duration",
					EphemerisGenerator.ephemerisTimeStep,
					NumericalSimulator.simulationDuration);
		}
		mainStatus &= status;
		status = true;


		/* Overall check status. */
		if (!mainStatus) {
			logger.error("User Configuration Check Failed.");
			throw new Exception("User Configuration Check Failed.");
		}
	}
}
