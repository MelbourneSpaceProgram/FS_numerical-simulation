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

package msp.simulator;

import msp.simulator.NumericalSimulator;
import msp.simulator.user.Dashboard;

/**
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
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
