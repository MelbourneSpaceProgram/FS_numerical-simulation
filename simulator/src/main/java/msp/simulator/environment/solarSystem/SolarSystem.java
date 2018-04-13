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

package msp.simulator.environment.solarSystem;

import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class represents the instance of the Solar System
 * in the simulation environment.<p>
 * 
 * The Solar System is the class at the roots of the
 * different Celestial Bodies and is responsible to 
 * provide tools and methods to build them, access them
 * and manage them.
 * 
 * @see Earth
 * @see Sun
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class SolarSystem {
	
	/** Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(
			SolarSystem.class);
	
	/** Earth Instance of the Solar System in the simulation. */
	private Earth earth;
	
	/** Sun Instance of the Solar System in the simulation. */
	private Sun sun;
	
	/** Moon Instance of the Solar System in the simulation. */
	private Moon moon;
	
	/**
	 * Constructor of the SolarSystem
	 */
	public SolarSystem() {
		logger.info(CustomLoggingTools.indentMsg(logger,
				"Building the Solar System..."));
		
		this.earth = new Earth();
		this.sun = new Sun();
		this.moon = new Moon();
	}
	
	/**
	 * Return the Earth instance of the simulation.
	 * @return Earth instance of the simulator
	 * @see Earth
	 */
	public Earth getEarth() {
		return this.earth;
	}
	
	/**
	 * Return the Sun instance of the simulation.
	 * @return Sun instance of the simulation
	 * @see Sun
	 */
	public Sun getSun() {
		return this.sun;
	}
	
	/**
	 * Return the Moon instance of the simulation.
	 * @return Moon instance of the simulation
	 * @see Moon
	 */
	public Moon getMoon() {
		return this.moon;
	}
	
	/** Return the inertial frame of the Solar System,
	 * i.e. the EME2000 singleton instance from OreKit.
	 * @return FramesFactory.getEME2000()
	 * 
	 * @see FramesFactory
	 */
	public Frame getIntertialFrame() {
		return FramesFactory.getEME2000();
	}

}
