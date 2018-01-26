/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.torques;

import java.util.ArrayList;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.time.AbsoluteDate;

/**
 * This class implements a simple torque manoeuvre 
 * over time in a MGT-like way: ON/OFF at the maximum
 * intensity torque, positive or negative.
 * <p>
 * NB: It is mainly use in the simulator as a tool used to test
 * the implementation of the dynamic engine to provide some
 * context scenario of validation
 *
 * @author Florian CHAUBEYRE
 */
public class AutomaticManoeuvre implements TorqueProvider {

	/**
	 * This embedded class represents a step of the torque
	 * law over time.
	 * A torque is applied at its start time offset and during
	 * a certain duration otherwise the returned torque is zero
	 * on the defined rotation axis.
	 *
	 * @author Florian CHAUBEYRE
	 */
	private class Step {
		private double start;
		private double duration;
		private Vector3D vector;

		private Step(double start, double duration, Vector3D vector){
			this.start = start;
			this.duration = duration;
			this.vector = vector;
		}
		private double getStart() {return start;}
		private double getDuration() {return duration;}
		private Vector3D getVector() {return vector;}
	}

	private AbsoluteDate startDate;
	private ArrayList<Step> scenario;

	/* Default torque intensity to apply to the satellite. */
	private static final double maxTorqueIntensity = 1e-4 /* N.m */ ;

	/** Default Scenario of control of the satellite. */
	private static ArrayList<Step> DEFAULT_SCENARIO = new ArrayList<Step>();

	public AutomaticManoeuvre(AbsoluteDate startDate) {
		this(startDate, DEFAULT_SCENARIO);
	}

	public AutomaticManoeuvre(AbsoluteDate startDate, ArrayList<Step> scenario) {
		/* Building the Default Scenario. */
		DEFAULT_SCENARIO.add(new Step(1, 20, Vector3D.PLUS_I));
		DEFAULT_SCENARIO.add(new Step(25, 20, Vector3D.MINUS_I));
		DEFAULT_SCENARIO.add(new Step(50, 10, new Vector3D(1,1,1).normalize()));
		DEFAULT_SCENARIO.add(new Step(65, 10, new Vector3D(-1,-1,-1).normalize()));

		this.startDate = startDate;
		this.scenario = scenario;
	}


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
			torque = step.getVector().scalarMultiply(maxTorqueIntensity);
		} else {
			torque = Vector3D.ZERO;
		}
		return torque;
	}





}
