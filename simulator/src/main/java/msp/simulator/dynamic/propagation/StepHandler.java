/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.propagation;

import org.hipparchus.complex.Quaternion;
import org.hipparchus.geometry.euclidean.threed.Rotation;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.AngularCoordinates;

import msp.simulator.satellite.Satellite;

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
	private Satellite satellite;

	/** Fixed integration time step of the propagation services. */
	private final double integrationTimeStep;

	/** Buffer for the last target date of propagation. */
	private AbsoluteDate lastTargetDate;

	/**
	 * Build the additional step processing of the propagation services.
	 * @param satellite Instance of the simulation.
	 * @param stepSize Integration time step of the propagation, i.e. "dt".
	 */
	public StepHandler(Satellite satellite, double stepSize) {
		this.satellite = satellite;
		this.integrationTimeStep = stepSize;
		this.lastTargetDate = satellite.getStates().getInitialState().getDate();
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
		/* 
		 * Avoid that any date is processed twice. This can happen during the
		 * propagation when the end date of a set of steps is computed as well 
		 * as the first date of the next set - both are supposed to be equal.
		 * Only the last step of the set (isLast is true) is computed instead
		 * of the beginning step.
		 */
		
		/* Create a new instance to avoid any numerical approximation leading to a false
		 * comparison between dates.
		 */
		AbsoluteDate actualDate = new AbsoluteDate(
				mainPropagatedState.getDate().toString(),
				TimeScalesFactory.getUTC()
				);
		
		/* Abort the attitude propagation if ... */
		boolean abort = (this.lastTargetDate.compareTo(actualDate) >= 0) ;

		if (!abort) {
			/* ***** Handle the step. ******	*/
			
			this.propagateAttitude(mainPropagatedState);
		
			/* *****************************	*/
		
		}
		
		/* Update the buffered date. */
		this.lastTargetDate = new AbsoluteDate(
				mainPropagatedState.getDate().toString(),
				TimeScalesFactory.getUTC()
				);
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
	 * Torque -- Spin -- Attitude.
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
	public void propagateAttitude(SpacecraftState mainPropagatedState) {
		try {
			/* At that point the main propagation of the orbital parameters
			 * and the additional states (spin and rotational acceleration)
			 * is done and we have access to a SpacecraftState containing 
			 * the current Attitude and the new integrated spin.
			 * Thus we can compute the attitude at the next step.
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
					this.satellite.getStates().getCurrentState().getAttitude();

			Quaternion currentQuaternion = new Quaternion (
					currentAttitude.getRotation().getQ0(),
					currentAttitude.getRotation().getQ1(),
					currentAttitude.getRotation().getQ2(),
					currentAttitude.getRotation().getQ3()
					);

			/* 		-> Propagate the attitude quaternion. */
			Quaternion propagatedQuaternion =
					this.wilcox(
							currentQuaternion, 
							spin, 
							this.integrationTimeStep
							);

			/* 		-> Build the final attitude. */
			Attitude propagatedAttitude = new Attitude (
					mainPropagatedState.getDate(),
					mainPropagatedState.getFrame(),
					new AngularCoordinates(
							new Rotation(
									propagatedQuaternion.getQ0(),
									propagatedQuaternion.getQ1(),
									propagatedQuaternion.getQ2(),
									propagatedQuaternion.getQ3(),
									false /* The quaternion should already be normalized. */
									),
							spin,
							rotAcc
							)
					);

			/* Finally mount the new propagated state: only the attitude is modified. */
			SpacecraftState secondaryPropagatedState = new SpacecraftState(
					mainPropagatedState.getOrbit(),
					propagatedAttitude,
					mainPropagatedState.getMass(),
					mainPropagatedState.getAdditionalStates()
					);

			/* Updating the satellite reference at the end of the step. */
			this.satellite.getStates().setCurrentState(secondaryPropagatedState);

		} catch (OrekitException e) {
			e.printStackTrace();
		}

	}

}
