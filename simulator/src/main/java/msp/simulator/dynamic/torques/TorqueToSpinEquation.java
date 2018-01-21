/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.torques;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.integration.AdditionalEquations;
import org.orekit.utils.AngularCoordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.satellite.Satellite;
import msp.simulator.satellite.assembly.Assembly;

/**
 * This class implements an additional equation for the 
 * numerical propagator.
 * <p>
 * This equation represents the kinetic momentum theorem.
 * It aims to compute, from the overall torque interaction on
 * the satellite at the instant t, the spin (or rotationnal
 * speed) of the satellite at the instant t+dt.
 *
 * @author Florian CHAUBEYRE
 */
public class TorqueToSpinEquation implements AdditionalEquations {

	/** Name of the addition equation matching
	 * the additional state to differentiate.
	 */
	private static final String name = "Spin";

	/** Instance of the Logger of the class. */
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(
			TorqueToSpinEquation.class);

	/** Instance of the Satellite of the simulation.*/
	private Satellite satellite;

	/** Instance of torque provider of the dynamic engine. */
	private TorqueProvider torqueProvider;


	/**
	 * Constructor of the equation.
	 * @param torqueProvider The Provider of the overall
	 * torque interaction on the satellite in the satellite
	 * frame.
	 */
	public TorqueToSpinEquation(Satellite satellite, TorqueProvider torqueProvider) {
		this.satellite = satellite;
		this.torqueProvider = torqueProvider;
	}

	/** Name of the Equation.
	 * <p>This name is directly related to the n-uplet of
	 * additional states added to the satellite.
	 * 
	 * @see org.orekit.propagation.integration.AdditionalEquations
	 */
	@Override
	public String getName() {
		return name;
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
	 * compute on our own the new attitude quaternion.
	 * 
	 * @param s The current Spacecraft state provided by the Orekit core.
	 * @param pDot Placeholder for the derivative of the additional
	 * states. This array should end up containing the derivative
	 * of the each add. states in order as defined in the SpacecraftState.
	 * The mathcing is actually done through the name of the equation /
	 * the name of the related array of add. states. It behaves as an 
	 * "output" fot the method.
	 * 
	 * @see org.orekit.propagation.integration.AdditionalEquations#computeDerivatives(org.orekit.propagation.SpacecraftState, double[])
	 */
	@Override
	public double[] computeDerivatives(SpacecraftState s, double[] pDot) throws OrekitException {

		/* ************************* Example ****************************	*
		 * Extract the current rotational speed in the satellite frame:	*
		 * 	double wx = s.getAdditionalState(this.getName())[0];			*
		 * 	double wy = s.getAdditionalState(this.getName())[1];			*
		 * 	double wz = s.getAdditionalState(this.getName())[2];			*
		 * **************************************************************	*/

		/* Implementation of the equation.
		 * 		wDot = M / I  	on each axis in the case the inertia
		 * 						is the same on the three axis.
		 */
		double wxDot = this.torqueProvider.getTorque().getX() / Assembly.cs1_inertia;
		double wyDot = this.torqueProvider.getTorque().getY() / Assembly.cs1_inertia;
		double wzDot = this.torqueProvider.getTorque().getZ() / Assembly.cs1_inertia;

		/* Updating the Reference.
		 * 	NB: The following does not update the reference:
		 * 	pDot = new double[] {wxDot, wyDot, wzDot};
		 */
		pDot[0] = wxDot;
		pDot[1] = wyDot;
		pDot[2] = wzDot;

		/* Return the potentially new updated main propagation state, i.e.
		 * the orbital parameters (a, ex, ey, i, Omega, Alpha).
		 * NB: The Attitude doesn't belong to the propagation state.
		 * 
		 * As we do not update the main state through this computation
		 * we return null.
		 */
		return null;
	}

}
