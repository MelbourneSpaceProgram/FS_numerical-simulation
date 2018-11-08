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

import java.util.ArrayList;
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
 * Provider of the additional state "rotational acceleration".
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class RotAccProvider implements AdditionalStateProvider {

	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory
			.getLogger(RotAccProvider.class);

	/** Name of the related additional state. */
	private static final String name = "RotAcc";

	/** Provider of the torque interaction on the satellite. */
	private ArrayList<TorqueProvider> torqueProviders;

	/** Satellite body instance in the simulation. */
	private SatelliteBody satelliteBody;

	/**
	 * Constructor of the provider.
	 * @param torqueProvider Instance of the simulation in use
	 * @param satelliteBody Instance of the simulation
	 */
	public RotAccProvider(ArrayList<TorqueProvider> torqueProviders, SatelliteBody satelliteBody) {
		this.torqueProviders = torqueProviders;
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
	 * Provide the rotational acceleration of the satellite in the body
	 * frame.
	 * The torque provider in use is called and the rotational acceleration
	 * is deduced.
	 * <p>
	 * The current algorithm uses the Euler equations of motion for a rotating 
	 * rigid body to provide the spin derivative, i.e. the rotational acceleration.
	 * {@inheritDoc}
	 */
	@Override
	public double[] getAdditionalState(SpacecraftState state) throws OrekitException {

		/* Compute the overall interaction of all of the registered torques
		 * in the satellite frame. */
		Vector3D overallTorque = Vector3D.ZERO;
		for (TorqueProvider provider : this.torqueProviders) {
			overallTorque = overallTorque.add(
					provider.getTorque(state.getDate())
					);
		}

		/* Compute the rotational acceleration from the overall torque interaction. */
		double[] rotAcc = computeEulerEquations(
				overallTorque, 
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
	 * @param inertiaMatrix of the satellite
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
		rotAcc[1] = (M2 - (I1 - I3) * W3 * W1) / I2 ; 
		rotAcc[2] = (M3 - (I2 - I1) * W1 * W2) / I3 ;

		return rotAcc;
	}

}
