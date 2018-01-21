/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite.assembly;

import java.util.Map;

import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.geometry.euclidean.threed.Rotation;
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
 *
 * @author Florian CHAUBEYRE
 */
public class SatelliteStates {

	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(SatelliteStates.class);

	/* 
	 * ***************************************** * 
	 * 	Any action on a SpacecraftState creates	*
	 * 	a new instance, so we should update a	*
	 * 	current SpacecraftState attribute.		*
	 * *****************************************	* 
	 */

	/** Initial state of the satellite. */
	private SpacecraftState initialState;

	/** Current state of the satellite. */
	private SpacecraftState currentState;

	private Attitude defaultAttitude; 

	/**
	 * 
	 * @param environment
	 * @throws IllegalArgumentException
	 */
	public SatelliteStates(Environment environment) {

		SatelliteStates.logger.info(CustomLoggingTools.indentMsg(SatelliteStates.logger,
				" -> Initializing the satellite states..."));

		this.defaultAttitude = new Attitude(
				environment.getOrbit().getDate(),
				environment.getOrbit().getFrame(),
				new AngularCoordinates(
						new Rotation(1,0,0,0,true), 
						new Vector3D(0.031415,0.031415,0.031415)
						)
				);

		this.initialState = new SpacecraftState(
				environment.getOrbit(),
				/* Default Attitude. The operating one should 
				 * be brought by the Dynamic module. */
				this.defaultAttitude,
				Assembly.cs1_Mass);

		/* Add user-related additional states. */
		this.initialState = this.initialState
				/* Rotation Speed
				 *  - Satellite frame
				 *  - (rad/s) 
				 */
				.addAdditionalState("Spin", new double[]{
						0.031415, 	/* W_x */
						0.031415,	/* W_y */
						0.0}			/* W_z */
						)
				/*	Torque Actions
				 *  - Satellite frame
				 *  - (N.m) 
				 *
				 *	.addAdditionalState("Torque", new double[]{
				 *		0.0, 	 M_x 
				 *		0.0,		 M_y 
				 *		0.0}		 M_z 
				 *		)
				 */
				;

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
	 * @param state	The SpacecraftState to update
	 * @param newState The new value for the state
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
	 * @param newInitialState
	 */
	public void setInitialState(SpacecraftState newInitialState) {
		this.initialState = this.updateState(newInitialState);
	}

	/**
	 * Update the value of the current state of the satellite.
	 * A check is done on the compatibility of the additional states.
	 * @param newCurrentState
	 */
	public void setCurrentState(SpacecraftState newCurrentState) {
		this.currentState = this.updateState(newCurrentState);
	}


	/**
	 * @return the defaultAttitude
	 */
	public Attitude getDefaultAttitude() {
		return defaultAttitude;
	}	




}
