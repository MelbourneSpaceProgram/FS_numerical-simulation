/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.propagation;

import org.hipparchus.ode.ODEIntegrator;
import org.hipparchus.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.orekit.errors.OrekitException;
import org.orekit.forces.ForceModel;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.dynamic.forces.Forces;
import msp.simulator.dynamic.guidance.Guidance;
import msp.simulator.dynamic.torques.Torques;
import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
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

	/* ******* Public Static Attributes ******* */

	/** Time Step in use for integration step calculation. (s) 
	 * <p>Default value is 0.1 s.
	 * */
	public static double integrationTimeStep = 0.1 ;

	/* **************************************** */

	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(Propagation.class);

	/** Instance of Integrator of the Propagator. */
	private ODEIntegrator integrator ;

	/** Instance of Propagator in the simulation. */
	private NumericalPropagator propagator;

	/** Instance of the satellite in the simulation. */
	private Satellite satellite;

	/** Instance of the Torques Manager. */
	private Torques torques;

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
		this.satellite = satellite;
		this.torques = torques;

		try {
			/* Creating the Instance of Propagator. */
			this.integrator = new ClassicalRungeKuttaIntegrator(Propagation.integrationTimeStep);
			this.propagator = new NumericalPropagator(this.integrator);

			/* Configuring the step handler of the propagation services. */
			StepHandler simulationStepHandler = new StepHandler(
					this.satellite,
					Propagation.integrationTimeStep
					);

			this.propagator.setMasterMode(
					Propagation.integrationTimeStep, 
					simulationStepHandler);

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
					satellite.getStates().getInitialState());

			/* Registering the different state providers. */
			
			/*  + Attitude					*/
			this.propagator.setAttitudeProvider(guidance.getAttitudeProvider());
			
			/*  + Torque Dynamic 			*/
			this.propagator.addAdditionalEquations(this.torques.getTorqueToSpinEquation());
			
			/*  + Rotational Acceleration.	*/
			this.propagator.addAdditionalStateProvider(this.torques.getRotAccProvider());
		
		
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
			this.satellite.getStates().setCurrentState(propagatedState);
					
		} catch (OrekitException e) {
			e.printStackTrace();
		}
	}




}
