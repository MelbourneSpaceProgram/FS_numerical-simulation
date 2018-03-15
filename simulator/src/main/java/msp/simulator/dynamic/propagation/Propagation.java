/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.propagation;

import java.util.Arrays;

import org.orekit.errors.OrekitException;
import org.orekit.forces.ForceModel;
import org.orekit.orbits.OrbitType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.dynamic.forces.Forces;
import msp.simulator.dynamic.guidance.Guidance;
import msp.simulator.dynamic.propagation.integration.Integration;
import msp.simulator.dynamic.torques.Torques;
import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
import msp.simulator.satellite.assembly.SatelliteStates;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class is the Top Class implementing all of the
 * propagation services for the simulation.
 * <p>
 * The propagation is the core of the dynamic as it allows
 * the simulation to calculate the next step of the satellite
 * defined states through the environment, the applied forces
 * and other user-defined equations, e.g. the torques processing. 
 * 
 * @see NumericalPropagator
 *
 * @author Florian CHAUBEYRE
 */
public class Propagation {

	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(Propagation.class);
	
	/** Instance of the intagration manager. */
	private Integration integrationManager;

	/** Instance of Propagator in the simulation. */
	private NumericalPropagator propagator;

	/** Instance of the satellite in the simulation. */
	private SatelliteStates satelliteStates;
	
	/** Type of the orbit in the simulation. */
	private OrbitType orbitType;

	/**
	 * Create and Configure the Instance of Propagator
	 * of the Simulation.
	 * @param environment Instance of the Simulation
	 * @param satellite Instance of the Simulation
	 * @param forces Instance of the Simulation
	 * @param torques Instance of the Simulation
	 * @param guidance Instance of the Simulation
	 */
	public Propagation(Environment environment, Satellite satellite, 
			Forces forces,
			Torques torques,
			Guidance guidance
			) {
		Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
				"Registering to the Propagation Services..."));

		/* Linking the main simulation objects. */
		this.satelliteStates = satellite.getStates();
		this.orbitType = environment.getOrbit().getType();
		
		/* Building the integration manager. */
		this.integrationManager = new Integration(satellite, torques.getTorqueProvider());

		try {
			/* Creating the Instance of Propagator. 
			 * Be aware that this new instance will have its default parameters.
			 * The user needs to configure it afterwards.
			 */
			this.propagator = new NumericalPropagator(
					this.integrationManager.getIntegrator());

			/* Configuring the step handler of the propagation services. */
			StepHandler simulationStepHandler = new StepHandler(
					satelliteStates,
					this.integrationManager.getStepSize()
					);

			this.propagator.setMasterMode(
					this.integrationManager.getStepSize(), 
					simulationStepHandler);
			
			/* Set the orbit type. */
			this.propagator.setOrbitType(this.orbitType);

			/* Registering the implemented force models. */
			Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
					"-> Registering the implemented Linear Force Models..."));
			for (ForceModel forceModel : forces.getListOfForces() ) {
				this.propagator.addForceModel(forceModel);
				Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
						"   + " + forceModel.toString()));
			}

			/* Configuring the initial state of the satellite. */
			Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
					"-> Configuring the initial state of the satellite..."));
			this.propagator.setInitialState(
					satelliteStates.getInitialState());

			/* Registering the different state providers. */
			
			/*  + Attitude					*/
			this.propagator.setAttitudeProvider(
					guidance.getAttitudeProvider());
			
			/*  + Rotational Acceleration.	*/
			this.propagator.addAdditionalStateProvider(
					this.integrationManager.getRotAccProvider());
		
			/*  + Rotational Speed 			*/
			this.propagator.addAdditionalEquations(
					this.integrationManager.getSpinEquation());
			
			/*  + Rotation Angle				*/
			this.propagator.addAdditionalEquations(
					this.integrationManager.getThetaEquation());
		
		} catch (OrekitException e) {
			e.printStackTrace();
		}

		Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
				"Propagator Configured."));
	}

	/**
	 * Ensure the propagation to the target date and update the satellite
	 * state.
	 * <p>
	 * Note that the time resolution of the propagation is not given by the
	 * target date but by the integration time step.
	 * 
	 * @param targetDate The date where the propagation processing ends.
	 */
	public void propagate(AbsoluteDate targetDate) {
		try {
			SpacecraftState propagatedState = this.propagator.propagate(targetDate);
			this.satelliteStates.setCurrentState(propagatedState);
			
			System.out.println("Theta: " + 
					Arrays.toString(propagatedState.getAdditionalState("Theta")));
			
					
		} catch (OrekitException e) {
			e.printStackTrace();
		}
	}




}
