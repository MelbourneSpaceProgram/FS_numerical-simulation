/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.propagation.integration;

import org.hipparchus.ode.ODEIntegrator;
import org.hipparchus.ode.nonstiff.ClassicalRungeKuttaIntegrator;

import msp.simulator.dynamic.torques.TorqueProvider;
import msp.simulator.satellite.Satellite;

/**
 * This class gathers and manage all of the integration tools
 * in use in the simulation.
 *
 * @author Florian CHAUBEYRE
 */
public class Integration {
	
	/* ******* Public Static Attributes ******* */

	/** Time Step in use for integration step calculation. (s) 
	 * <p>Default value is 0.1 s.
	 * */
	public static double integrationTimeStep = 0.1 ;

	/* **************************************** */

	/** Instance of the rotational acceleration Provider. */
	private RotAccProvider rotAccProvider;

	/** Instance of the additional equation leading the spin. */
	private SpinODE spinEquation;
	
	/** Instance of the additional equation leading the rotation angle. */
	private ThetaODE thetaEquation;
	
	/** Instance of the integrator. */
	private ODEIntegrator integrator;
	
	/** Time size of an integration step. */
	private double stepSize;
	
	/**
	 * 
	 */
	public Integration(Satellite satellite, TorqueProvider torqueProvider) {
		this.stepSize = integrationTimeStep;
		this.integrator = new ClassicalRungeKuttaIntegrator(this.stepSize);
		this.rotAccProvider = new RotAccProvider(
				torqueProvider,
				satellite.getAssembly().getBody());
		this.spinEquation = new SpinODE(
				rotAccProvider);
		this.thetaEquation = new ThetaODE();
	}

	/**
	 * @return the spinEquation
	 */
	public SpinODE getSpinEquation() {
		return spinEquation;
	}

	/**
	 * @return the rotAccProvider
	 */
	public RotAccProvider getRotAccProvider() {
		return rotAccProvider;
	}

	/**
	 * @return the integrator
	 */
	public ODEIntegrator getIntegrator() {
		return integrator;
	}

	/**
	 * @return the stepSize
	 */
	public double getStepSize() {
		return stepSize;
	}

	/**
	 * @return the thetaEquation
	 */
	public ThetaODE getThetaEquation() {
		return thetaEquation;
	}

}
