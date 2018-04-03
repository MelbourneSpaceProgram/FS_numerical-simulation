/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.propagation;

import org.hipparchus.complex.Quaternion;
import org.hipparchus.geometry.euclidean.threed.Rotation;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
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
import msp.simulator.dynamic.propagation.integration.Integration;
import msp.simulator.dynamic.propagation.integration.SecondaryStates;
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

		/* Building the integration manager. */
		this.integrationManager = new Integration(satellite, torques.getTorqueProvider());

		try {
			/* Creating the Instance of Propagator. 
			 * Be aware that this new instance will have its default parameters.
			 * The user needs to configure it afterwards.
			 */
			this.propagator = new NumericalPropagator(
					this.integrationManager.getIntegrator());

			/* Set the propagation mode. More information about step handling
			 * and master mode in this package-info.
			 */
			this.propagator.setSlaveMode();

			/* Set the orbit type. */
			/* NB: otherwise the propagation converts the initial orbit to
			 * the corresponding orbit of its default set type (equinoctial).
			 */
			this.propagator.setOrbitType(environment.getOrbit().getType());

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

			/* Registering the different providers. */
			/*  + Attitude						*/
			this.propagator.setAttitudeProvider(
					guidance.getAttitudeProvider());

			/*  + Additional Provided State		*/
			this.propagator.addAdditionalStateProvider(
					this.integrationManager.getRotAccProvider());

			/*  + Additional Integrated States	*/
			this.propagator.addAdditionalEquations(
					this.integrationManager.getSecondaryStatesEquation());

		} catch (OrekitException e) {
			e.printStackTrace();
		}

		Propagation.logger.info(CustomLoggingTools.indentMsg(Propagation.logger,
				"Propagator Configured."));
	}

	/**
	 * Propagate the satellite states for a single step of time.
	 */
	public void propagateStep() {

		/* Step before the integration s(t). */
		SpacecraftState s_t = null;

		/* Step after the integration s(t + dt). */
		SpacecraftState s_t_dt = null;

		/* Step at the end of all of the propagations.
		 * (Integration, orbit, additional states and attitude)
		 */
		SpacecraftState propagatedState = null;

		try {
			/* Get s(t) */
			s_t = this.satelliteStates.getCurrentState();

			/* Get s(t+dt) */
			s_t_dt = this.propagator.propagate(
					this.satelliteStates.getCurrentState().getDate()
					.shiftedBy(
							this.getIntegrationManager().getStepSize())
					);

			/* Debug log. */
			logger.debug("#### PROPAGATION STEP: " 
					+ s_t.getDate().toString()
					+ " ---> "
					+ s_t_dt.getDate().toString()
					);


			/* Propagate the attitude. */
			propagatedState = this.propagateAttitude(s_t, s_t_dt);

			/* Set the updated satellite state. */
			this.satelliteStates.setCurrentState(
					propagatedState);

		} catch (OrekitException e) {
			logger.error("Progation failed - Step "
					+ this.satelliteStates.getCurrentState().getDate().toString()
					+ " ---> "
					+ this.satelliteStates.getCurrentState().getDate()
					.shiftedBy(Integration.integrationTimeStep)
					);
			e.printStackTrace();
		}
	}

	/**
	 * Compute the satellite rotational state at the next time step.
	 * <p>
	 * The dynamic computation is as follow: 
	 * Torque -- Spin -- Attitude.
	 * <p>
	 * The main OreKit integration takes care of the orbital 
	 * parameters and integrates the additional data - e.g. the 
	 * spin and angle of rot. - where this secondary integration
	 * resolves the new attitude through the Wilcox algorithm.
	 * 
	 * @param currentState The state s(t), i.e. the one before integration.
	 * @param integratedState The state s(t + dt), i.e. resulting 
	 * from the integration of the step s(t).
	 * @return The fully updated state s(t+dt) with its propagated attitude.
	 * 
	 * @see NumericalPropagator#propagate(AbsoluteDate)
	 * 
	 */
	public SpacecraftState propagateAttitude(
			SpacecraftState currentState,
			SpacecraftState integratedState
			) {

		/* Final state fully propagated at t + dt. */
		SpacecraftState secondaryPropagatedState = null;

		try {
			/* At that point the integration of the orbital parameters
			 * and the additional states (angle of rotation, spin and 
			 * rotational acceleration) should be done.
			 * Thus we can compute the rotational parameters at the next 
			 * time step.
			 */

			/* Rotational Acceleration */
			Vector3D rotAcc = new Vector3D(integratedState.getAdditionalState("RotAcc"));

			/* Spin */
			Vector3D spin = new Vector3D(
					SecondaryStates.extractState(
							integratedState.getAdditionalState(SecondaryStates.key),
							SecondaryStates.SPIN)
					);

			/* dTheta: small angle of rotation during the step. */
			/* theta(t + dt) */
			Vector3D theta_T_DT = new Vector3D(
					SecondaryStates.extractState(
							integratedState.getAdditionalState(SecondaryStates.key),
							SecondaryStates.THETA)
					);

			/* theta(t) */
			Vector3D theta_T = new Vector3D(
					SecondaryStates.extractState(
							currentState.getAdditionalState(SecondaryStates.key),
							SecondaryStates.THETA)
					);

			/* dTheta = theta(t + dt) - theta(t) */
			Vector3D dTheta = theta_T_DT.subtract(theta_T) ;

			/* Attitude determination: it needs to be propagated. */
			Attitude currentAttitude =
					this.satelliteStates.getCurrentState().getAttitude();

			Quaternion currentQuaternion = new Quaternion (
					currentAttitude.getRotation().getQ0(),
					currentAttitude.getRotation().getQ1(),
					currentAttitude.getRotation().getQ2(),
					currentAttitude.getRotation().getQ3()
					);

			/* 		-> Propagate the attitude quaternion. */
			Quaternion propagatedQuaternion =
					wilcox(		
							currentQuaternion, 
							dTheta, 
							this.integrationManager.getStepSize()
							);




			/* 		-> Build the final attitude. */
			Attitude propagatedAttitude = new Attitude (
					integratedState.getDate(),
					integratedState.getFrame(),
					new AngularCoordinates(
							new Rotation(
									propagatedQuaternion.getQ0(),
									propagatedQuaternion.getQ1(),
									propagatedQuaternion.getQ2(),
									propagatedQuaternion.getQ3(),
									true /* Normalize the quaternion. */
									),
							spin,
							rotAcc
							)
					);

			/* Finally mount the new propagated state: only the attitude is modified. */
			secondaryPropagatedState = new SpacecraftState(
					integratedState.getOrbit(),
					propagatedAttitude,
					integratedState.getMass(),
					integratedState.getAdditionalStates()
					);

		} catch (OrekitException e) {
			e.printStackTrace();
		}

		return secondaryPropagatedState;
	}

	/**
	 * The Wilcox Algorithm allows to compute the differential equation
	 * leading the quaternion kinematic. It means this can provide
	 * the quaternion at the next integration step (t+dt) regarding the orientation 
	 * and the rotational speed at the time t.
	 * <p>
	 * Be aware that this processing considers the orientation of the spin
	 * vector to be constant over a step. In the case the rotational speed
	 * vector is not constant all along the step, an error said an error
	 * of commutation appears and one should use the Edward's algorithm
	 * that propose a correction.
	 * 
	 * @param Qi Initial quaternion to propagate
	 * @param theta Small rotation angle between the two step.
	 * (integral of the spin from (t) to (t+dt)
	 * @param dt Integration time step
	 * @return Qj The final quaternion after the rotation due 
	 * to the spin during the step of time.
	 */
	public static Quaternion wilcox(Quaternion Qi, Vector3D theta, double dt) {

		/* Compute the change-of-frame Quaternion dQ */
		double dQ0 = 1. - theta.getNormSq() / 8. ;
		double dQ1 = theta.getX() / 2. * (1. - theta.getNormSq() / 24.);
		double dQ2 = theta.getY() / 2. * (1. - theta.getNormSq() / 24.);
		double dQ3 = theta.getZ() / 2. * (1. - theta.getNormSq() / 24.);

		Quaternion dQ = new Quaternion(dQ0, dQ1, dQ2, dQ3);

		/* Compute the final state Quaternion. */
		Quaternion Qj = Qi.multiply(dQ).normalize();

		return Qj ;
	}

	/**
	 * Edward's algorithm enables to integrate the differential equation
	 * leading the Quaternion kinematic by trying to reduce the error said
	 * of commutation that appears when the spin vector does not have a
	 * constant direction during the integration step.
	 * <p>
	 * Nonetheless, the error of commutation is zero when the spin
	 * vector and the integrated spin vector over the step (or theta)
	 * are linear. Then the algorithm is equivalent to the typical Wilcox 
	 * algorithm.
	 * 
	 * @param Qi Initial Quaternion
	 * @param theta Integrated spin vector ( integral(spin, t, t+dt) )
	 * @param spin Rotational Speed vector
	 * @param dt Integration step
	 * @return The quaternion at t+dt
	 */
	public static Quaternion edwards(Quaternion Qi, Vector3D theta, Vector3D spin, double dt) {

		/* Compute the error of commutation. */
		Vector3D commutation = 
				spin.scalarMultiply(1. / 2.)
				.crossProduct(theta.scalarMultiply(1. / 2.))
				.scalarMultiply(1. / 12.);

		/* Compute the transition quaternion. */
		double scalarPart = 1. - theta.getNormSq() / 8. ;
		Vector3D vectorPart = 
				theta
				.scalarMultiply( ( 1. - theta.getNormSq() / 24.) / 2. )
				.add(commutation);

		Quaternion dQ = new Quaternion(scalarPart, vectorPart.toArray());

		/* Finally compute the final quaternion. */
		Quaternion Qf = Qi.multiply(dQ).normalize();

		return Qf;
	}

	/**
	 * @return the integrationManager
	 */
	public Integration getIntegrationManager() {
		return integrationManager;
	}
}
