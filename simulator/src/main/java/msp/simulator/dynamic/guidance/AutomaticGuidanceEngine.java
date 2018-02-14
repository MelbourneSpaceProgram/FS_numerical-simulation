/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.guidance;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
import org.orekit.attitudes.AttitudeProvider;
import org.orekit.attitudes.CelestialBodyPointed;
import org.orekit.errors.OrekitException;
import org.orekit.frames.FramesFactory;

import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;

/**
 * This class provides tools to access the defined automatic 
 * attitude provider.
 *
 * @author Florian CHAUBEYRE
 */
public class AutomaticGuidanceEngine {

	/** Link to the Space Simulation Environment. */
	private Environment environment;

	/** Link to the Satellite instance of the simulation. */
	private Satellite satellite;

	/** #1: Earth Pointing Attitude Provider. */
	private AttitudeProvider earthPointing;

	/**
	 * Build the Automatic Guidance Engine.
	 * @param environment
	 * @param satellite
	 */
	public AutomaticGuidanceEngine(Environment environment, Satellite satellite) {
		this.environment = environment;
		this.satellite = satellite;

		this.earthPointing = new CelestialBodyPointed (
				FramesFactory.getGCRF(), 
				this.environment.getSolarSystem().getEarth().getPvCoordinateProvider(),
				Vector3D.PLUS_K, 
				Vector3D.PLUS_I, 
				Vector3D.PLUS_K);

	}

	/**
	 * Get the initial attitude of the registered automatic
	 * attitude provider.
	 * @param attitudeProvider
	 * @return Initial Attitude of the Automatic AttitudeProvider
	 */
	public Attitude getInitialAttitude(AttitudeProvider attitudeProvider) {
		Attitude initialAttitude = this.satellite.getAssembly().getStates().getInitialAttitude();
		try {
			initialAttitude = attitudeProvider.getAttitude(
					this.environment.getOrbit(),
					this.environment.getOrbit().getDate(),
					FramesFactory.getEME2000()
					);
		} catch (OrekitException e) {
			e.printStackTrace();
		}
		
		return initialAttitude;
	}
	
	/**
	 * Return an "Earth-Pointing" Guidance.
	 * @return AttitudeProvider
	 */
	public AttitudeProvider getEarthPointing() {
		return this.earthPointing;
	}

}
