/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.propagation;

import org.hipparchus.complex.Quaternion;
import org.hipparchus.geometry.euclidean.threed.Rotation;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
import org.orekit.errors.OrekitException;
import org.orekit.orbits.CircularOrbit;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.AngularCoordinates;

import msp.simulator.environment.orbit.OrbitWrapper;
import msp.simulator.satellite.assembly.SatelliteStates;
import msp.simulator.utils.logs.ephemeris.EphemerisGenerator;

/**
 * This class implements the propagation step handler
 * for our own simulation.
 * <p>
 * It mainly add the attitude propagation after the main
 * orbital parameters and additional states integration 
 * at the end of each steps. Then it enables to synchronize 
 * both main and secondary propagation.
 *
 * @author Florian CHAUBEYRE
 */
public class StepHandler implements OrekitFixedStepHandler {

	/** Instance of the satellite in the simulation. */
	private SatelliteStates satelliteStates;

	/** Fixed integration time step of the propagation services. */
	private final double integrationTimeStep;

	/** Buffer for the last target date of propagation. */
	private AbsoluteDate exitDateOfStep;

	/**
	 * Build the additional step processing of the propagation services.
	 * @param satellite Instance of the simulation.
	 * @param stepSize Integration time step of the propagation, i.e. "dt".
	 */
	public StepHandler(SatelliteStates satelliteStates, double stepSize) {
		this.satelliteStates = satelliteStates;
		this.integrationTimeStep = stepSize;
		this.exitDateOfStep = 
				satelliteStates.getInitialState().getDate()
				.shiftedBy(this.integrationTimeStep);
	
	}

	/**
	 * This method handles any processing that has to be done
	 * after the main orbital parameters and additional states
	 * propagation at each step.
	 * <p>
	 * This is mainly used in this simulation to process the attitude
	 * propagation of the satellite according to our rotational
	 * dynamic and ensures the synchronization between the main
	 * propagation and our user-defined extra propagation.
	 * 
	 * @param mainPropagatedState Satellite state after the main propagation (orbit and additionals)
	 * @param isLast True if this step is the last step of the propagation
	 * at the target date.
	 * @throws OrekitException when the step fail to process.
	 * @see org.orekit.propagation.sampling.OrekitFixedStepHandler
	 */
	@Override
	public void handleStep(SpacecraftState mainPropagatedState, boolean isLast) throws OrekitException {

		/* **************************** READ ME *************************************	*
		 * 																			*
		 * Let's say the main propagation compute a step from t to t+dt.				*
		 * Then it will iterate the main state of the satellite, i.e. 				*
		 * the orbital parameters qnd the additional parameter from the date at t		* 
		 * to the the date at t+dt.													*
		 * According the fact that numerous objects are time stamped in the			*
		 * simulation, a specific attention should be given to the synchronization	*
		 * of the different modules.													*
		 * 																			*
		 * Here the main propagation will provide a time stamped Spacecraft state		*
		 * with the START DATE of the integration step - Date(t). Nonetheless this	*
		 * is not exactly the state at t because the main parameters have been		*
		 * integrated as well as the additional ones.								*
		 * Thus the StepHandler has to compute this provided step time stamped at		*
		 * t and bring it to t+dt.													*
		 * 																			*
		 * As a consequence, we do not have access to the target date, but to the		*
		 * beginning date of the integration!										*
		 * 																			*
		 * Taking into account the wrong date of the step, for instance to mistake 	*
		 * the beginning date and the target date, could lead to an asynchronization	*
		 * between the main and the secondary propagation (provided by the step 		*
		 * handler). This asynchronization will probably be of one time step so it 	*
		 * could be tricky to detect.												*
		 * 																			*
		 * On the other side when the end date of a set of steps is reached, a final	*
		 * computation is proposed through the boolean "isLast".						*
		 * If the last state is time stamped with a date said Tf and computed, then	*
		 * at the next step - from Ti to Ti+DT - in the case where Ti = Tf, then		*
		 * the step Ti = Tf is computed twice by the step handler: one time at the	*
		 * end of the previous step and one time at the beginning of the new step.	*
		 * 																			*
		 * In order to avoid this double processing, that lead to a twice-as-quick	*
		 * step handler propagation, we have to prevent the computation of the same	*
		 * date.																		*
		 * 																			*
		 * Also the previous discussed point proved us that we have access to the		*
		 * entry date of the integration step. Thus we should avoid the double step 	*
		 * AT THE END and not the one at the start to avoid to be always a single 	*
		 * integration time step late during the secondary propagation.				*
		 * 																			*
		 * **************************************************************************	*/
		
		if (!isLast) {
			/* ***** Handle the step. ******	*/

			this.propagateAttitude(mainPropagatedState);

			/* *****************************	*/

		}

		/* Update the buffered date to the next step. */
		this.exitDateOfStep = this.exitDateOfStep.shiftedBy(EphemerisGenerator.ephemerisTimeStep);
	}

	/**
	 * Compute and Update the satellite state at the next time step.
	 * <p>
	 * The dynamic computation is as follow: 
	 * Torque -- Spin -- Attitude.
	 * <p>
	 * The main OreKit integration takes care of the orbital 
	 * parameters and integrates the additional data - e.g. the 
	 * spin - where the secondary integration resolves the new 
	 * attitude through the Wilcox algorithm.
	 * <p>
	 * This process is called "Propagation".
	 * 
	 * @param mainPropagatedState The main already-propagated 
	 * state at the date of the beginning of the integration.
	 * 
	 * @see NumericalPropagator#propagate(AbsoluteDate)
	 * 
	 */
	public void propagateAttitude(SpacecraftState mainPropagatedState) {
		try {
			/* At that point the main propagation of the orbital parameters
			 * and the additional states (spin and rotational acceleration)
			 * is done and we have access to a SpacecraftState containing 
			 * the current Attitude and the new integrated spin.
			 * Thus we can compute the attitude at the next step.
			 */
			/* Nonetheless as said in the main introduction of the class
			 * the date contained in the main propagated state is not updated,
			 * it means that we have access to the right orbit but with the
			 * previous date. Thus we have to update this date ourselves to
			 * store it in our own satellite states.
			 * As the date is accessed through the orbit, we have to build
			 * a clone of the orbit but with the right date.
			 */

			/* Determine the data of the rotation at the next step. 
			 * The additional states are already updated.
			 */

			/* Rotational Acceleration */
			Vector3D rotAcc = new Vector3D(mainPropagatedState.getAdditionalState("RotAcc"));

			/* Spin */
			Vector3D spin = new Vector3D(mainPropagatedState.getAdditionalState("Spin"));

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
							spin, 
							this.integrationTimeStep
							);

			/* 		-> Build the final attitude. */
			Attitude propagatedAttitude = new Attitude (
					mainPropagatedState.getDate().shiftedBy(integrationTimeStep),
					mainPropagatedState.getFrame(),
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
			SpacecraftState secondaryPropagatedState = new SpacecraftState(
					OrbitWrapper.clone(
							new CircularOrbit(mainPropagatedState.getOrbit()),
							mainPropagatedState.getOrbit().getDate().shiftedBy(integrationTimeStep)),
					propagatedAttitude,
					mainPropagatedState.getMass(),
					mainPropagatedState.getAdditionalStates()
					);

			/* Updating the satellite reference at the end of the step. */
			this.satelliteStates.setCurrentState(secondaryPropagatedState);

		} catch (OrekitException e) {
			e.printStackTrace();
		}
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
	 * @param spin Instant rotational speed
	 * @param dt Integration time step
	 * @return Qj The final quaternion after the rotation due 
	 * to the spin during the step of time.
	 * 
	 * @see StepHandler#edwards
	 */
	public static Quaternion wilcox(Quaternion Qi, Vector3D spin, double dt) {

		/* Vector Angle of Rotation: Theta = dt*W */
		Vector3D theta = new Vector3D(dt, spin);

		/* Compute the change-of-frame Quaternion dQ */
		double dQ0 = 1. - 1./8. * theta.getNormSq();
		double dQ1 = theta.getX() / 2. * (1. - 1./24. * theta.getNormSq());
		double dQ2 = theta.getY() / 2. * (1. - 1./24. * theta.getNormSq());
		double dQ3 = theta.getZ() / 2. * (1. - 1./24. * theta.getNormSq());

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
	 * 
	 * @see StepHandler#wilcox
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

}
