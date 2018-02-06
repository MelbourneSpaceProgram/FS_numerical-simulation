/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.torques;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.time.AbsoluteDate;

/**
 * Interface to normalize the provision of torques
 * to the dynamic engine.
 *
 * @author Florian CHAUBEYRE
 */
public interface TorqueProvider {
	
	/**
	 * Provide a torque interaction on the satellite
	 * in the satellite frame.
	 * @return Vector3D in Satellite Frame.
	 */
	public Vector3D getTorque(AbsoluteDate date);
	

}
