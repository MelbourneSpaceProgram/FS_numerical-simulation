/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.user;

import java.util.ArrayList;
import java.util.Arrays;

import org.hipparchus.complex.Quaternion;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.NumericalSimulator;
import msp.simulator.dynamic.propagation.integration.Integration;
import msp.simulator.dynamic.propagation.integration.RotAccProvider;
import msp.simulator.dynamic.torques.MemCachedTorqueProvider;
import msp.simulator.dynamic.torques.TorqueOverTimeScenarioProvider;
import msp.simulator.dynamic.torques.TorqueProviderEnum;
import msp.simulator.dynamic.torques.Torques;
import msp.simulator.environment.orbit.OrbitWrapper;
import msp.simulator.environment.orbit.OrbitWrapper.OrbitalParameters;
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
 * simulation creation and can be used
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

		Dashboard.setRealTimeProcessing(false);
		Dashboard.setIntegrationTimeStep(0.1);
		Dashboard.setEphemerisTimeStep(1.0);
		Dashboard.setSimulationDuration(10);
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

		Dashboard.setMagnetometerNoiseIntensity(Magnetometer.defaultNoiseIntensity);

		Dashboard.setTorqueScenario(new ArrayList<TorqueOverTimeScenarioProvider.Step>());
		Dashboard.setTorqueProvider(TorqueProviderEnum.SCENARIO);

		/* **** IO Settings **** */
		Dashboard.setMemCachedConnection(IO.connectMemCached, IO.memcachedSocketAddress);
		Dashboard.setTorqueCommandKey(MemCachedTorqueProvider.torqueCommandKey);

		try {
			Dashboard.checkConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the real-time processing flag of the simulator.
	 * @param status True to trigger the real-time processing.
	 */
	public static void setRealTimeProcessing(boolean status) {
		NumericalSimulator.realTimeUserFlag = status;
	}

	/**
	 * Set the integration time step of the different integrations
	 * used on the simulation (Attitude and Main PVT).
	 * <p>
	 * Note that the integration step should be a factor of the
	 * simulation duration.
	 * @param step in seconds and strictly positive
	 */
	public static void setIntegrationTimeStep(double step) {
		if (step > 0) {
			Integration.integrationTimeStep = step;

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
	 * Note that it should be a factor of the integration
	 * time step.
	 * @param duration in seconds
	 */
	public static void setSimulationDuration(long duration) {
		NumericalSimulator.simulationDuration = duration ; /* s. */
	}

	/**
	 * Set the orbital parameters required to define the orbit in
	 * the simulator.
	 * @param param The appropriate orbital parameters
	 * @see msp.simulator.environment.orbit.OrbitWrapper.OrbitalParameters
	 */
	public static void setOrbitalParameters(OrbitalParameters param) {
		OrbitWrapper.userOrbitalParameters = param;
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
	 * @param spin Vector in the space
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
	 * Set the torque provider to be use by the simulator.
	 * @param torqueProviderInUse Instance of the simulation
	 */
	public static void setTorqueProvider(TorqueProviderEnum torqueProviderInUse) {
		Torques.activeTorqueProvider = torqueProviderInUse;
	}

	/**
	 * Set the torque over time scenario provider.
	 * <p>
	 * If the scenario directly begins at the start date of the simulation,
	 * the initial acceleration is automatically updated.
	 * <p>
	 * NOTE: The user should prior set the initial spin and the inertia matrix
	 * of the satellite.
	 * @param scenario User defined steps of the torque law over time.
	 */
	public static void setTorqueScenario(ArrayList<TorqueOverTimeScenarioProvider.Step> scenario) {
		TorqueOverTimeScenarioProvider.TORQUE_SCENARIO = 
				new ArrayList<TorqueOverTimeScenarioProvider.Step>(scenario);

		/* If the torque scenario strictly begins at the entry date of the simulation
		 * we should correct the initial rotational acceleration of the satellite.
		 */
		if ( TorqueOverTimeScenarioProvider.TORQUE_SCENARIO.size() > 0
				&&
				TorqueOverTimeScenarioProvider.TORQUE_SCENARIO.get(0).getStart() == 0.)
		{
			Dashboard.setInitialRotAcceleration(

					new Vector3D(RotAccProvider.computeEulerEquations(
							TorqueOverTimeScenarioProvider.TORQUE_SCENARIO.get(0).getRotVector()
							.scalarMultiply(TorqueOverTimeScenarioProvider.getTorqueIntensity()), 
							SatelliteStates.initialSpin, 
							SatelliteBody.satInertiaMatrix)
							)
					);
		}
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

	/**
	 * Set the MemCached hash table key corresponding to the torque command.
	 * @param key Description of the value
	 */
	public static void setTorqueCommandKey(String key) {
		MemCachedTorqueProvider.torqueCommandKey = key;
	}

	/**
	 * Setting the connection to the VTS socket.
	 * @param active true to activate the connection.
	 */
	public static void setVtsConnection(boolean active) {
		IO.connectVts = active;
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
		boolean status;

		/* Check */
		/* The integration time step should be a factor of the simulation duration. */
		status = FastMath.floorMod(
				NumericalSimulator.simulationDuration * 1000,
				(long) (Integration.integrationTimeStep * 1000)
				) == 0;
		if (!status) {
			logger.error("The integration time step should be a factor "
					+ "of the simulation duration."
					+ "\n"
					+ "\t\tIntegration Step: {} ms. vs {} s. :Duration",
					(long) (Integration.integrationTimeStep * 1000),
					NumericalSimulator.simulationDuration);
		}
		mainStatus &= status;	

		/* Check */
		/* The ephemeris time step should be inferior than the simulation duration. */
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

		/* Check */
		/* When a MemCached torque provider is set, the MemCached connection 
		 * should be enable in the satellite IO. 
		 */
		status = (Torques.activeTorqueProvider != TorqueProviderEnum.MEMCACHED)
				||
				IO.connectMemCached ;
		if (!status) {
			logger.error("Activating the MemCached torque provider failed: "
					+ "The MemCached connection is not enable.");
		}
		mainStatus &= status;

		/* Check */
		/* In case the active torque provider is a scenario beginning at the initial
		 * date of the simulation, the first step should provide a torque that match 
		 * the initial rotational acceleration of the satellite.
		 */
		status = Torques.activeTorqueProvider != TorqueProviderEnum.SCENARIO
				|| 
				TorqueOverTimeScenarioProvider.TORQUE_SCENARIO.isEmpty()
				||
				((TorqueOverTimeScenarioProvider.TORQUE_SCENARIO.get(0).getStart() == 0.)
						&&
						Arrays.equals(

								RotAccProvider.computeEulerEquations(
										TorqueOverTimeScenarioProvider.TORQUE_SCENARIO.get(0).getRotVector()
										.scalarMultiply(TorqueOverTimeScenarioProvider.getTorqueIntensity()), 
										SatelliteStates.initialSpin, 
										SatelliteBody.satInertiaMatrix
										), 
								SatelliteStates.initialRotAcceleration.toArray()
								)
						)
				;
		if (!status) {
			logger.error("Incoherent Torque Scenario: the initial rotational acceleration "
					+ "does not match the initial scenario value."
					+ "\n"
					+ "Expected: " + Arrays.toString(

							RotAccProvider.computeEulerEquations(
									TorqueOverTimeScenarioProvider.TORQUE_SCENARIO.get(0).getRotVector()
									.scalarMultiply(TorqueOverTimeScenarioProvider.getTorqueIntensity()), 
									SatelliteStates.initialSpin, 
									SatelliteBody.satInertiaMatrix
									)) 
					+ "\n"
					+ "Actual  : " + Arrays.toString(
							SatelliteStates.initialRotAcceleration.toArray())
					);
		}
		mainStatus &= status;

		/* Overall check status. */
		if (!mainStatus) {
			logger.error("User Configuration Check Failed.");
			throw new Exception("User Configuration Check Failed.");
		}
	}
}
