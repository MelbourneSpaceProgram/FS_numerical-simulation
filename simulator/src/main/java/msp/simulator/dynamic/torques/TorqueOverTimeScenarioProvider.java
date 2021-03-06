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

package msp.simulator.dynamic.torques;

import java.util.ArrayList;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.time.AbsoluteDate;

/**
 * This class implements a simple torque maneuver 
 * over time in a MGT-like way: ON/OFF at the maximum
 * intensity torque, positive or negative.
 * <p>
 * NB: It is mainly use in the simulator as a tool used to test
 * the implementation of the dynamic engine to provide some
 * context scenario of validation
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class TorqueOverTimeScenarioProvider implements TorqueProvider {

	/* ******* Public Static Elements ******* */

	/** List of the torque steps over time. Default scenario is empty. */
	public static ArrayList<Step> TORQUE_SCENARIO = 
			new ArrayList<TorqueOverTimeScenarioProvider.Step>();

	/**
	 * This embedded class represents a step of the torque
	 * law over time.
	 * A torque is applied at its start time offset and during
	 * a certain duration otherwise the returned torque is zero
	 * on the defined rotation axis.
	 *
	 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
	 */
	public static class Step {
		private double start;
		private double duration;
		private Vector3D rotVector;

		public Step(double start, double duration, Vector3D vector){
			this.start = start;
			this.duration = duration;
			this.rotVector = vector;
		}
		public double getStart() {return start;}
		public double getDuration() {return duration;}
		public Vector3D getRotVector() {return rotVector;}
	}
	
	/** @return The default torque intensity of the scenario. */
	public static double getTorqueIntensity() {
		return maxTorqueIntensity;
	}
	
	/* **************************************** */
	
	/** Absolute start date of  the scenario in the simulation. */
	private AbsoluteDate startDate;

	/** List of the torque steps over time. */
	private ArrayList<Step> scenario = null;

	/** Default torque intensity to apply to the satellite. */
	private static final double maxTorqueIntensity = 1e-1 /* N.m */ ;

	/**
	 * Constructor with the torque scenario set as default.
	 * @param startDate of the torque scenario over time
	 */
	public TorqueOverTimeScenarioProvider(AbsoluteDate startDate) {
		this(startDate, TorqueOverTimeScenarioProvider.TORQUE_SCENARIO);
	}

	/**
	 * Set the torque scenario over time.
	 * @param startDate Absolute date to start the scenario in the simulation
	 * @param scenario List of the torque steps.
	 */
	public TorqueOverTimeScenarioProvider(AbsoluteDate startDate, ArrayList<Step> scenario) {
		this.scenario = scenario;
		this.startDate = startDate;
	}

	/**
	 * Add a torque step to the scenario over time.
	 * @param startOffset Offset from the beginning of the scenario (start date)
	 * @param duration of the step
	 * @param nRotation Torque Vector / Rotation vector.
	 * @return true (as specified by Collection.add)
	 */
	public boolean addStep(double startOffset, double duration, Vector3D nRotation) {
		return this.scenario.add(new Step(startOffset, duration, nRotation));
	}

	/** {@inheritDoc} */
	@Override
	public Vector3D getTorque(AbsoluteDate currentDate) {
		Vector3D torque;
		double offset = currentDate.durationFrom(this.startDate);
		boolean success = false;
		Step step = null;
		/* Iterate the scenario to find the first applicable step. */
		for (int i = 0; i < this.scenario.size(); i++) {
			step = this.scenario.get(i);
			/* If we find a currently operating step, it's a success. */
			if (	    (step.getStart() <= offset)
					&&
					(step.getStart() + step.getDuration() > offset)) 
			{
				success = true;
				break;
			}
		}
		if (success) {
			torque = step.getRotVector().scalarMultiply(maxTorqueIntensity);
		} else {
			torque = Vector3D.ZERO;
		}
		return torque;
	}

	/**
	 * @return the maxTorqueIntensity
	 */
	public double getMaxTorqueIntensity() {
		return maxTorqueIntensity;
	}

}
