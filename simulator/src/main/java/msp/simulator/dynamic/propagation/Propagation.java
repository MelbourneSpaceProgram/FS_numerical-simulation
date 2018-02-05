/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.propagation;

import org.hipparchus.complex.Quaternion;
import org.hipparchus.geometry.euclidean.threed.Rotation;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.ode.ODEIntegrator;
import org.hipparchus.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.orekit.attitudes.Attitude;
import org.orekit.errors.OrekitException;
import org.orekit.forces.ForceModel;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.AngularCoordinates;
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

	/** Instance of the satellite in the simulation. */
	private Satellite satellite;

	/** Instance of the Torques Manager. */
	private Torques torques;

	/**
	 * Create and Configure the Instance of Propagator
	 * of the Simulation.
	 * 
	 * @param environment The Space Environment 
	 * @param satellite The Satellite Object
	 * @param forces The Forces Object of the Simulation
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
			Propagation.integrationTimeStep = 0.1 ;
			this.integrator = new ClassicalRungeKuttaIntegrator(Propagation.integrationTimeStep);
			this.propagator = new NumericalPropagator(this.integrator);

			/* Registering the implemented force models. */
			Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
					"-> Registering the implemented Linear Force Models..."));
			for (ForceModel forceModel : forces.getListOfForces() ) {
				this.propagator.addForceModel(forceModel);
				Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
						"   + " + forceModel.toString()));
			}

			/* Registering the initial state of the satellite. */
			Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
					"-> Registering the initial Satellite State..."));
			this.propagator.setInitialState(
					satellite.getAssembly().getStates().getInitialState());

			/* Registering the Attitude Provider Engine. */
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
	 * Compute the Wilcox Algorithm.
	 * <p>
	 * This algorithm allows to propagate an initial quaternion
	 * through a certain rotational speed during a small step
	 * of time.
	 * 
	 * @param Qi Initial quaternion to propagate
	 * @param spin Instant rotational speed
	 * @param dt Step of time
	 * @return Qj The final quaternion after the rotation due 
	 * to the spin during the step of time.
	 */
	private Quaternion wilcox(Quaternion Qi, Vector3D spin, double dt) {

		/* Vector Angle of Rotation: Theta = dt*W */
		Vector3D theta = new Vector3D(dt, spin);

		/* Compute the change-of-frame Quaternion dQ */
		double dQ0 = 1. - 1./8. * theta.getNormSq();
		double dQ1 = theta.getX() / 2. * (1. - 1./24. * theta.getNormSq());
		double dQ2 = theta.getY() / 2. * (1. - 1./24. * theta.getNormSq());
		double dQ3 = theta.getZ() / 2. * (1. - 1./24. * theta.getNormSq());

		Quaternion dQ = new Quaternion(dQ0, dQ1, dQ2, dQ3);

		/* Compute the final state Quaternion. */
		Quaternion Qj = Qi.multiply(dQ);

		return Qj ;
	}


	/**
	 * Compute and Update the satellite state at the next time step.
	 * <p>
	 * The dynamic computation is as follow: 
	 * Torque -> Spin -> Attitude.
	 * <p>
	 * The main OreKit integration takes care of the orbital 
	 * parameters and integrates the additional data - e.g. the 
	 * spin - where the secondary integration resolves the new 
	 * attitude through the Wilcox algorithm.
	 * <p>
	 * This process is called "Propagation".
	 * 
	 * @param targetDate The target date. Note that the time
	 * resolution is given by the integrator time step.
	 * 
	 * @see NumericalPropagator#propagate(AbsoluteDate)
	 * 
	 */
	public void propagate(AbsoluteDate targetDate) {
		try {
			SpacecraftState mainPropagatedState;

			/* Compute the main new state:
			 * - Orbital Parameters
			 * - Spin integrated from the Torque Provider. 
			 */
			mainPropagatedState = this.propagator.propagate(targetDate);

			/* Compute the Attitude from the new integrated spin. */
			/*	-> Compute the current Acceleration Rate; */
			double[] currentAccArray = new double[3];

			/* The equation is computed again so we have the exact same
			 * spin derivatives as the propagator. */
			this.torques.getTorqueToSpinEquation().computeDerivatives(
					mainPropagatedState,
					currentAccArray);

			Vector3D currentAcc = new Vector3D(currentAccArray);

			/* Get the current satellite spin normally just integrated. */
			Vector3D currentSpin = new Vector3D(
					mainPropagatedState.getAdditionalState("Spin")
					);

			/* Compute the Attitude by propagating the current attitude
			 * quaternion at the current spin with the Wilcox Algorithm. 
			 */
			Quaternion currentQuaternion = new Quaternion (
					mainPropagatedState.getAttitude().getRotation().getQ0(),
					mainPropagatedState.getAttitude().getRotation().getQ1(),
					mainPropagatedState.getAttitude().getRotation().getQ2(),
					mainPropagatedState.getAttitude().getRotation().getQ3()
					);

			Quaternion propagatedQuaternion = this.wilcox(
					currentQuaternion, 
					currentSpin, 
					integrationTimeStep
					);

			Attitude finalAttitude = new Attitude(
					mainPropagatedState.getDate(),
					mainPropagatedState.getFrame(),
					new AngularCoordinates(
							new Rotation(
									propagatedQuaternion.getQ0(),
									propagatedQuaternion.getQ1(),
									propagatedQuaternion.getQ2(),
									propagatedQuaternion.getQ3(),
									true
									),
							currentSpin,
							currentAcc
							)
					);

			/* Finally mounting the new propagated state. */
			SpacecraftState secondaryPropagatedState = new SpacecraftState(
					mainPropagatedState.getOrbit(),
					finalAttitude,
					mainPropagatedState.getMass(),
					mainPropagatedState.getAdditionalStates()
					);

			/* Updating the satellite reference. */
			this.satellite.getStates().setCurrentState(secondaryPropagatedState);

		} catch (OrekitException e) {
			e.printStackTrace();
		}

	}

}
