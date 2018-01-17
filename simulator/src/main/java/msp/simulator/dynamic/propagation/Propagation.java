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
	
	/** Instance of Propagator in the simulation. */
	private NumericalPropagator propagator;
	
	/** Instance of Integrator of the Propagator. */
	private ODEIntegrator integrator ;
	
	/** Time Step in use for integration step calculation. (s) */
	private double integrationTimeStep;
	
	public Propagation(Environment environment, Satellite satellite, Forces forces) {
		Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
				"Registering to the Propagation Services..."));
		
		/* Creating the Instance of Propagator. */
		this.integrationTimeStep = 1.0;
		this.integrator = new ClassicalRungeKuttaIntegrator(this.integrationTimeStep);
		this.propagator = new NumericalPropagator(this.integrator);
		
		/* Registering the implmented force models. */
		Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
				"-> Registering the implemented Force Models..."));
		for (ForceModel forceModel : forces.getListOfForces() ) {
			this.propagator.addForceModel(forceModel);
		}
		
		/* Registering the initial state of the satellite. */
		Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
				"-> Registering the initial Satellite State..."));
		try {
			this.propagator.setInitialState(satellite.getAssembly().getSatelliteState());
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
