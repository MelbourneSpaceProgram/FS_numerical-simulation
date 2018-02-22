/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.propagation;

import java.util.Arrays;

import org.hipparchus.complex.Quaternion;
import org.hipparchus.geometry.euclidean.threed.Rotation;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;
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
	private double integrationTimeStep;
	
	/**
	 * Build the additional step processing of the propagation services.
	 * @param satellite Instance of the simulation.
	 * @param stepSize Integration time step of the propagation, i.e. "dt".
	 */
	public StepHandler(Satellite satellite, double stepSize) {
		this.satellite = satellite;
		this.integrationTimeStep = stepSize;
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
	 * @param currentState Satellite state after main propagation of the state.
	 * @param isLast True if this step is the last step of the propagation
	 * from beginDate to endDate - discard here.
	 * @throws OrekitException when the step fail to process.
	 * @see org.orekit.propagation.sampling.OrekitFixedStepHandler
	 */
	@Override
	public void handleStep(SpacecraftState currentState, boolean isLast) throws OrekitException {
		/* We do not desire any additional processing at the end of the overall
		 * propagation. This would induce the processing of another more step
		 * over.
		 */
		if (isLast) {
			return ;
		}
		
		
		
		
		
		
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
	public void propagateAttitude(SpacecraftState currentState) {
		try {
			/* At that point
			
			/* We need to take into account the non-integrated attitude from the
			 * current satellite state to propagate.
			 */
			SpacecraftState currentSatState = this.satellite.getStates().getCurrentState();
			
			/* If this is not the initial step. */
			if (!targetDate.equals(satellite.getStates().getInitialState().getDate())) {
				/* Compute the Attitude from the new integrated spin. */
				/*	-> Compute the current Acceleration Rate; */
				double[] currentAccArray = new double[3];

				/* The equation is computed again so we have the exact same
				 * spin derivatives as the propagator. */
				this.torques.getTorqueToSpinEquation().computeDerivatives(
						currentSatState,
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
						currentSatState.getAttitude().getRotation().getQ0(),
						currentSatState.getAttitude().getRotation().getQ1(),
						currentSatState.getAttitude().getRotation().getQ2(),
						currentSatState.getAttitude().getRotation().getQ3()
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
			}

		} catch (OrekitException e) {
			e.printStackTrace();
		}

	}

}
