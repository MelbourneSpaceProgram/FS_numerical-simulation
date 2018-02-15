/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite.sensors;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.propagation.SpacecraftState;

import msp.simulator.environment.Environment;
import msp.simulator.environment.geomagneticField.EarthMagneticField;
import msp.simulator.environment.solarSystem.Earth;
import msp.simulator.satellite.assembly.Assembly;

/**
 * This class represents the magnetometer of the
 * satellite.
 * 
 * TODO: write the class.
 *
 * @author Florian CHAUBEYRE
 */
public class Magnetometer {
	
	@SuppressWarnings("unused")
	private EarthMagneticField geomagField;
	
	@SuppressWarnings("unused")
	private Earth earth;
	
	private Assembly assembly;
	
	public Magnetometer(Environment environment, Assembly assembly) {
		this.geomagField = environment.getGeoMagneticField();
		this.assembly = assembly;
		this.earth = environment.getSolarSystem().getEarth();
	}
	/**
	 * Do nothing.
	 * @return null
	 */
	public Vector3D getData() {
		
		@SuppressWarnings("unused")
		SpacecraftState satState = this.assembly.getStates().getCurrentState() ;
		
		//this.geomagField.getMagField().calculateField(latitude, longitude, height);
		
		return null;
	}

}