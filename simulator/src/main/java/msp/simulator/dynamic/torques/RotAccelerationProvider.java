/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.torques;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.AdditionalStateProvider;
import org.orekit.propagation.SpacecraftState;

import msp.simulator.satellite.assembly.SatelliteBody;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class RotAccelerationProvider implements AdditionalStateProvider {

	/** Name of the related additional state. */
	private static final String name = "RotAcc";

	/** Provider of the torque interaction on the satellite. */
	private TorqueProvider torqueProvider;
	
	/** Satellite body instance in the simulation. */
	private SatelliteBody satelliteBody;

	/**
	 * Build the instance of rotational acceleration provider.
	 * @param torqueProvider Torque Provider from where the acceleration is computed
	 * @param satelliteBody Take the inertia of the body into account
	 */
	public RotAccelerationProvider(TorqueProvider torqueProvider, SatelliteBody satelliteBody) {
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

	@Override
	public double[] getAdditionalState(SpacecraftState state) throws OrekitException {
		/* Rotationnal Acceleration Array. */
		double[] rotAcc = new double[3];
		
		/* Torque interaction in the satellite frame. */
		Vector3D torque = this.torqueProvider.getTorque(state.getDate());
		
		/* Inertia Matrix of the satellite. */
		double[][] inertiaMatrix = this.satelliteBody.getInertiaMatrix();
		
		/* Implementation of the equation of motion.
		 * 		wDot(t) = M(t) / I  	on each axis considering the inertia
		 * 							independent along the three axis (Hypotheses)
		 * 
		 * To integrate the coupling between the different axis, one can refer to 
		 * the Euler Equation for a rotating rigid body.
		 */
		rotAcc[0] = torque.getX() / inertiaMatrix[0][0]; 
		rotAcc[1] = torque.getY() / inertiaMatrix[1][1]; 
		rotAcc[2] = torque.getZ() / inertiaMatrix[2][2]; 
		
		return rotAcc;
	}

}
