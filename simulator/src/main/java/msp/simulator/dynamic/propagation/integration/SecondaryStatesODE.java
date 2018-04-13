/* Copyright 20017-2018 Melbourne Space Program
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package msp.simulator.dynamic.propagation.integration;

import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.integration.AdditionalEquations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the additional equation that lead
 * the behavior of the secondary additional states. Only
 * one additional equation is allowed.
 * <p>
 * This equation mainly represents the rotational dynamic 
 * especially the kinetic momentum theorem.
 * It aims to compute, from the overall torque interaction on
 * the satellite at the instant t, the spin (or rotationnal
 * speed) of the satellite at the instant t+dt. Then it integrates
 * this spin to obtain the rotation angle position.
 * <p>
 * The overall torque being responsible for the rotational
 * acceleration through the satellite inertia, this data is
 * stored in the additional state "RotAcc".
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class SecondaryStatesODE implements AdditionalEquations {

	/** Instance of the Logger of the class. */
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(
			SecondaryStatesODE.class);

	/** Provider of the rotationnal acceleration. */
	private RotAccProvider rotAccProvider;

	/** Constructor of the secondary states equation. 
	 * @param rotAccProvider Provider of the rotational acceleration in use
	 */
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
