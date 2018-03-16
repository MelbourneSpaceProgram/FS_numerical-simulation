/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.propagation.integration;

import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.integration.AdditionalEquations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements an additional equation for the 
 * numerical propagator.
 * <p>
 * This equation represents the kinetic momentum theorem.
 * It aims to compute, from the overall torque interaction on
 * the satellite at the instant t, the spin (or rotationnal
 * speed) of the satellite at the instant t+dt.
 * <p>
 * The overall torque being responsible for the rotational
 * acceleration through the satellite inertia, this data is
 * stored in the additional state "RotAcc".
 *
 * @author Florian CHAUBEYRE
 */
public class SecondaryStatesODE implements AdditionalEquations {

	/** Instance of the Logger of the class. */
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(
			SecondaryStatesODE.class);

	/** Provider of the rotationnal acceleration. */
	private RotAccProvider rotAccProvider;

	/** Constructor of the secondary states equation. */
	public SecondaryStatesODE(RotAccProvider rotAccProvider) {
		this.rotAccProvider = rotAccProvider;
	}

	/** Name of the Equation.
	 * <p>This name is directly related to the n-uplet of
	 * additional states added to the satellite.
	 * 
	 * @see org.orekit.propagation.integration.AdditionalEquations
	 */
	@Override
	public String getName() {
		return SecondaryStates.key;
	}

	/** 
	 * From the current related additional states, compute the 
	 * derivative of these states in order to compute the value
	 * at the next step of time through the integrator of the 
	 * propagator.
	 * <p>
	 * This method returns the potentially new updated main 
	 * propagation state, i.e.
	 * the orbital parameters (a, ex, ey, i, Omega, Alpha) if
	 * it changed else null.
	 * <p>
	 * NB: The Attitude doens't belong to the propagation state
	 * and that's why we need to add ourselves the spin and 
	 * compute our own new attitude quaternion.
	 * 
	 * @param s The current Spacecraft state provided by the Orekit core.
	 * @param pDot Placeholder for the derivative of the additional
	 * states. This array should end up containing the derivative
	 * of the each add. states in order as defined in the SpacecraftState.
	 * The mathcing is actually done through the name of the equation /
	 * the name of the related array of add. states. It behaves as an 
	 * "output" for the method.
	 * 
	 * @see org.orekit.propagation.integration.AdditionalEquations#computeDerivatives(org.orekit.propagation.SpacecraftState, double[])
	 */
	@Override
	public double[] computeDerivatives(SpacecraftState s, double[] pDot) throws OrekitException {
				
				/* Compute the spin derivative: torque provider. */
				System.arraycopy(
						s.getAdditionalState(this.rotAccProvider.getName()),
						0, 
						pDot, 
						SecondaryStates.SPIN.getIndex(), 
						SecondaryStates.SPIN.getSize()
						);
				
				/* Compute the theta derivative: spin. */
				System.arraycopy(
						s.getAdditionalState(SecondaryStates.key), 
						SecondaryStates.SPIN.getIndex(), 
						pDot,
						SecondaryStates.THETA.getIndex(), 
						SecondaryStates.THETA.getSize()
						);
				

		/* 
		 * Return the potentially new updated main propagation state, i.e.
		 * the orbital parameters (a, ex, ey, i, Omega, Alpha) for a circular
		 * orbit. 
		 * E.g. return new double[]{Rt+600, 0, 0, 98, 0, 0)
		 * 
		 * NB: The Attitude doesn't belong to the main integrated state.
		 * That's why it is computed here, as additional parameters.
		 * 
		 * As we do not update the main state through this computation
		 * we return null.
		 * A typical main state change would be a change of mass through gas 
		 * consumption.
		 */
		return null;
	}

}
