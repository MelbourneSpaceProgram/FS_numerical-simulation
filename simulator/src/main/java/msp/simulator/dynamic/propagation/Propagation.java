/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.propagation;

import org.hipparchus.ode.ODEIntegrator;
import org.hipparchus.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.orekit.errors.OrekitException;
import org.orekit.forces.ForceModel;
import org.orekit.propagation.numerical.NumericalPropagator;
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

	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(Propagation.class);

	/** Instance of Integrator of the Propagator. */
	private ODEIntegrator integrator ;

	/** Time Step in use for integration step calculation. (s) */
	public static double integrationTimeStep = 0.1 ; /* Default value */ 

	/** Instance of Propagator in the simulation. */
	private NumericalPropagator propagator;

	/**
	 * 
	 * @param environment
	 * @param satellite
	 * @param forces
	 */
	public Propagation(Environment environment, Satellite satellite, 
			Forces forces,
			Torques torques,
			Guidance guidance
			) {
		Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
				"Registering to the Propagation Services..."));
		try {
			/* Creating the Instance of Propagator. */
			Propagation.integrationTimeStep = 1.;
			this.integrator = new ClassicalRungeKuttaIntegrator(Propagation.integrationTimeStep);
			this.propagator = new NumericalPropagator(this.integrator);

			/* Registering the implemented force models. */
			Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
					"-> Registering the implemented Force Models..."));
			for (ForceModel forceModel : forces.getListOfForces() ) {
				this.propagator.addForceModel(forceModel);
				Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
						"   + " + forceModel.toString()));
			}

			/* Registering the initial state of the satellite. */
			Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
					"-> Registering the initial Satellite State..."));
			/*    DEFAULT INITIAL STATE. */
			this.propagator.setInitialState(
					satellite.getAssembly().getStates().getInitialState());
			
			/* Registering the Attitude Engine. */
			this.propagator.setAttitudeProvider(
					guidance.getAttitudeProvider());
			
			/* Registering the additional equations. */
			/*  + Torque Dynamic 					*/
			this.propagator.addAdditionalEquations(torques.getTorqueToSpinEquation());
			
		} catch (OrekitException e) {
			e.printStackTrace();
		}

		Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
				"Propagator Configured."));
	}

	/**
	 * @return The Instance of Propagator
	 */
	public NumericalPropagator getPropagator() {
		return propagator;
	}

}
