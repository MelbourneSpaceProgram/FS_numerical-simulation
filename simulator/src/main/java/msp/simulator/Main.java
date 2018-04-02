/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator;

import msp.simulator.NumericalSimulator;
import msp.simulator.user.Dashboard;

/**
 * @author Florian CHAUBEYRE
 *
 */
public class Main {
	
	/**
	 * Main method: Create an instance of the numerical simulator
	 * and launch it.
	 * @param args - unused
	 */
	public static void main(String[] args) {
		try {
			/* *** Configuration of the simulator. */
			Dashboard.setDefaultConfiguration();
			
			/* *** Creating and launching the simulation. */
			NumericalSimulator simulator = new NumericalSimulator();
			simulator.initialize();
			simulator.process();
			simulator.exit();
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

}
