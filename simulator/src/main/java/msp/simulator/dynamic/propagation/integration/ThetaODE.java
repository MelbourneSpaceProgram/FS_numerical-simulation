/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.propagation.integration;

import java.lang.reflect.Array;

import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.integration.AdditionalEquations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Differential equation leading the rotation angle of the satellite
 * orientation.
 * 
 * @author Florian CHAUBEYRE
 */
public class ThetaODE implements AdditionalEquations {

	/** Instance of the Logger of the class. */
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(
			ThetaODE.class);

	/**
	 * Name of the additional equation matching
	 * the additional state to differentiate.
	 */
	private static final String name = "Theta";

	/**
	 * Build the instance of the equation.
	 * No dependency is created.
	 */
	public ThetaODE() {
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public double[] computeDerivatives(SpacecraftState s, double[] pDot) throws OrekitException {
		System.out.println(s.hasAdditionalState("Spin"));
		System.out.println(Array.getLength(s.getAdditionalState("Theta")));
		if (s.hasAdditionalState("Spin")) {
			/* Get the spin vector, i.e. theta's derivative. */
			pDot[0] = s.getAdditionalState("Spin")[0];
			pDot[1] = s.getAdditionalState("Spin")[1];
			pDot[2] = s.getAdditionalState("Spin")[2];
		} 

		/* Do not modify the main state. (orbital parameters and mass) */
		return null;
	}

}
