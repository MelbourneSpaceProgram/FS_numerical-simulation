/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.propagation.integration;

import java.util.Arrays;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.AdditionalStateProvider;
import org.orekit.propagation.SpacecraftState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.dynamic.torques.TorqueProvider;
import msp.simulator.satellite.assembly.SatelliteBody;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class RotAccProvider implements AdditionalStateProvider {

	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory
			.getLogger(RotAccProvider.class);
	
	/** Name of the related additional state. */
	private static final String name = "RotAcc";

	/** Provider of the torque interaction on the satellite. */
	private TorqueProvider torqueProvider;
	
	/** Satellite body instance in the simulation. */
	private SatelliteBody satelliteBody;

	/**
	 * 
	 * @param torqueProvider
	 */
	public RotAccProvider(TorqueProvider torqueProvider, SatelliteBody satelliteBody) {
		this.torqueProvider = torqueProvider;
		this.satelliteBody = satelliteBody;
	}

	/** Name of the additional state provider.
	 * <p>
	 * This name is directly related to the n-uplet of
	 * additional states added to the satellite.
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Reverse the differential equation of motion to provide the rotational
	 * acceleration - i.e. the derivative of the spin.
	 * <p>
	 * The current algorithm uses the Euler equations of motion for a rotating 
	 * rigid body to provide the spin derivative.
	 */
	@Override
	public double[] getAdditionalState(SpacecraftState state) throws OrekitException {
		
		double[] rotAcc = computeEulerEquations(
				this.torqueProvider.getTorque(state.getDate()), 
				state.getAttitude().getSpin(), 
				this.satelliteBody.getInertiaMatrix()
				);
		
		logger.debug("Acc Provided - " + state.getDate().toString() + " - " +
		Arrays.toString(rotAcc));
		
		return rotAcc;
	}
	
	/**
	 * Compute the rotational acceleration through the Euler equations
	 * of motion for a rotating rigid body.
	 * 
	 * @param torque Current interaction in satellite frame
	 * @param spin Current rotational speed in satellite frame.
	 * @return The corresponding rotational acceleration vector as an array.
	 */
	public static double[] computeEulerEquations(
			Vector3D torque, 
			Vector3D spin, 
			double[][] inertiaMatrix) {
		
		/* Rotational acceleration array to complete. */
		double[] rotAcc = new double[3];
		
		/* Rotational speed */
		double W1 = spin.getX();
		double W2 = spin.getY();
		double W3 = spin.getZ();
		
		/* Torque interaction in the satellite frame. */
		double M1 = torque.getX();
		double M2 = torque.getY();
		double M3 = torque.getZ();
		
		/* Inertia Matrix of the satellite. */
		double I1 = inertiaMatrix[0][0];
		double I2 = inertiaMatrix[1][1];
		double I3 = inertiaMatrix[2][2];
		
		/* To explain the coupling between the different axis, one can refer to 
		 * the Euler Equations of motion for a rotating rigid body. 
		 */
		rotAcc[0] = (M1 - (I3 - I2) * W2 * W3) / I1 ;
		rotAcc[1] = (M2 - (I1 - I3) * W3 * W1) / I1 ; 
		rotAcc[2] = (M3 - (I2 - I1) * W1 * W2) / I1 ;
						
		return rotAcc;
	}

}
