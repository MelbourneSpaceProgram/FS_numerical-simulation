/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator;

import msp.simulator.NumericalSimulator;

/**
 * @author Florian CHAUBEYRE
 *
 */
public class Main {
	
	/**
	 * Main method: Create an instance of the numerical simulator
	 * and launch it.
	 * @param args (unused)
	 */
	public static void main(String[] args) {
		NumericalSimulator simulator = new NumericalSimulator();
		simulator.launch();
	}

}
