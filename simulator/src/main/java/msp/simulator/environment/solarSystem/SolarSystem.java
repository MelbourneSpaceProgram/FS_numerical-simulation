/* Copyright 2017-2018 Melbourne Space Program */

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
 * @author Florian CHAUBEYRE
 */
public class SolarSystem {
	
	/** Logger instance of the instance. */
	private final Logger logger;
	
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
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.logger.info(CustomLoggingTools.indentMsg(this.logger,
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
