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

import org.hipparchus.ode.ODEIntegrator;
import org.hipparchus.ode.nonstiff.ClassicalRungeKuttaIntegrator;

import msp.simulator.dynamic.torques.TorqueProvider;
import msp.simulator.satellite.Satellite;

/**
 * This class gathers and manage all of the integration tools
 * in use in the simulation.
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class Integration {
	
	/* ******* Public Static Attributes ******* */

	/** Time Step in use for integration step calculation. (s) 
	 * <p>Default value is 0.1 s. The step size should not be
	 * inferior than 1ms.
	 * */
	public static double integrationTimeStep = 0.1 ;

	/* **************************************** */

	/** Instance of the rotational acceleration Provider. */
	private RotAccProvider rotAccProvider;

	/** Instance of the additional equation leading the spin. */
	private SecondaryStatesODE secondaryStatesEquation;
	
	/** Instance of the integrator. */
	private ODEIntegrator integrator;
	
	/** Time size of an integration step. */
	private double stepSize;
	
	/**
	 * Constructor of the integration manager.
	 * @param satellite Instance of the simulation
	 * @param torqueProvider Instance of the simulation in use
	 */
	public Integration(Satellite satellite, TorqueProvider torqueProvider) {
		/* Integration parameters. */
		this.stepSize = integrationTimeStep;
		this.integrator = new ClassicalRungeKuttaIntegrator(this.stepSize);
		
		/* Providers for additional states. */
		this.rotAccProvider = new RotAccProvider(
				torqueProvider,
				satellite.getAssembly().getBody());
		
		/* Equation for additional states. */
		this.secondaryStatesEquation = new SecondaryStatesODE(
				rotAccProvider);
	}

	/**
	 * @return the secondaryStatesEquation
	 */
	public SecondaryStatesODE getSecondaryStatesEquation() {
		return secondaryStatesEquation;
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
	 * @return the stepSize in seconds.
	 */
	public double getStepSize() {
		return stepSize;
	}

}
