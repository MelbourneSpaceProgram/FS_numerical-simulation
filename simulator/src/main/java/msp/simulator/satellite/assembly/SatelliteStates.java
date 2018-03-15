/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite.assembly;

import java.util.Map;

import org.hipparchus.complex.Quaternion;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.geometry.euclidean.threed.Rotation;
import org.hipparchus.geometry.euclidean.threed.RotationConvention;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.utils.AngularCoordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class is responsible to store, maintain and
 * provide tools to access the satellite state over 
 * the time in the simulation.
 * <p>
 * Note that an OreKit Spacecraft state is immutable
 * so any action on the satellite creates a new instance.
 * Then this class focus on keeping up-to-date the right
 * current state of the satellite in time.
 * 
 * @see SpacecraftState 
 * 
 * @author Florian CHAUBEYRE
 */
public class SatelliteStates {

	/* ******* Public Static Attributes ******* */

	public static Quaternion initialAttitudeQuaternion = 
			new Quaternion(1,0,0,0);

	/** Initial Spin of the satellite */
	public static Vector3D initialSpin = new Vector3D(
			0.0,
			0.0,
			0.0
			);
	/** Initial Rotation Acceleration of the satellite */
	public static Vector3D initialRotAcceleration = new Vector3D(
			0.0,
			0.0,
			0.0
			);

	/* **************************************** */

	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(SatelliteStates.class);

	/** Initial Attitude Object of the satellite. */
	private Attitude initialAttitude;

	/** Initial state of the satellite. */
	private SpacecraftState initialState;

	/** Current state of the satellite. */
	private SpacecraftState currentState;

	/**
	 * Create the instance of Satellite states.
	 * @param environment  Instance of the Simulation
	 * @param body of the satellite
	 * @throws IllegalArgumentException if orbit and attitude dates or frames are not equal
	 */
	public SatelliteStates(Environment environment, SatelliteBody body) {

		SatelliteStates.logger.info(CustomLoggingTools.indentMsg(SatelliteStates.logger,
				" -> Initializing the satellite states..."));

		/* Creates here the initial first attitude object of the satellite. */
		Attitude initialAttitude = new Attitude(
				environment.getOrbit().getDate(),
				environment.getOrbit().getFrame(),
				new AngularCoordinates(
						new Rotation(
								SatelliteStates.initialAttitudeQuaternion.getQ0(),
								SatelliteStates.initialAttitudeQuaternion.getQ1(),
								SatelliteStates.initialAttitudeQuaternion.getQ2(),
								SatelliteStates.initialAttitudeQuaternion.getQ3(),
								true), 
						SatelliteStates.initialSpin,
						SatelliteStates.initialRotAcceleration
						)
				);

		/* Store the user-defined or default initial attitude. */
		this.initialAttitude = initialAttitude;

		/* Finally creates the initial State of the satellite. */
		this.initialState = new SpacecraftState(
				environment.getOrbit(),
				initialAttitude,
				body.getSatMass()
				);

		/* Add user-related additional states. */
		/*   -> Create the angle vector first. */
		Rotation rotAttitude = new Rotation (
				SatelliteStates.initialAttitudeQuaternion.getQ0(),
				SatelliteStates.initialAttitudeQuaternion.getQ1(),
				SatelliteStates.initialAttitudeQuaternion.getQ2(),
				SatelliteStates.initialAttitudeQuaternion.getQ3(),
				true);
		
		Vector3D theta = rotAttitude.getAxis(RotationConvention.VECTOR_OPERATOR)
				.scalarMultiply(rotAttitude.getAngle());
		
		this.initialState = this.initialState
				/* Rotational Acceleration
				 *  - Satellite Frame
				 *  - (rad/s^2)
				 */
				.addAdditionalState(
						"RotAcc",
						SatelliteStates.initialRotAcceleration.toArray()
						)
				/* Rotation Angle Vector ( or integrated spin )
				 *  - From the inertial frame (quaternion)
				 *  - (rad)
				 */
				.addAdditionalState(
						"Theta",
						theta.toArray()
						)
				/* Rotational Speed
				 *  - Satellite frame
				 *  - (rad/s) 
				 */
				.addAdditionalState(
						"Spin",
						SatelliteStates.initialSpin.toArray()
						)
				;

		/* Update the current state as the initial state. */
		this.currentState = this.initialState;
	}


	/**
	 * @return the currentState
	 */
	public SpacecraftState getCurrentState() {
		return currentState;
	}

	/**
	 * @return the initialState
	 */
	public SpacecraftState getInitialState() {
		return initialState;
	}

	/**
	 * Update the value of the state by the newState one.
	 * This method is generic regarding all of the additional
	 * states of the satellite.
	 * @param newState The new value for the state
	 * @return The updated old-state.
	 */
	private SpacecraftState updateState(SpacecraftState newState) {
		try {
			/* Updating the Main States. */
			SpacecraftState state = new SpacecraftState(
					newState.getOrbit(),
					newState.getAttitude(),
					newState.getMass()
					);

			/* Updating the additional states. */
			Map<String, double[]> additionalStates = newState.getAdditionalStates();

			for (final Map.Entry<String, double[]> entry : additionalStates.entrySet()) {
				state = state.addAdditionalState(
						entry.getKey(),
						entry.getValue()
						);
			}
			/* Check the additional states compatibility between the desired
			 * state and the state it is about to return.
			 */
			state.ensureCompatibleAdditionalStates(newState);

			return state;

		} catch (MathIllegalStateException | OrekitException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Update the value of the initial state of the satellite.
	 * A check is done on the compatibility of the additional states.
	 * @param newInitialState The user defined initial state
	 */
	public void setInitialState(SpacecraftState newInitialState) {
		this.initialState = this.updateState(newInitialState);
	}

	/**
	 * Update the value of the current state of the satellite.
	 * A check is done on the compatibility of the additional states.
	 * @param newCurrentState the new -updated- satellite state
	 */
	public void setCurrentState(SpacecraftState newCurrentState) {
		this.currentState = this.updateState(newCurrentState);
	}


	/**
	 * Get the initial Attitude of the satellite.
	 * @return Attitude at the initial state
	 * @see Attitude
	 */
	public Attitude getInitialAttitude() {
		return this.initialAttitude;
	}	


}
