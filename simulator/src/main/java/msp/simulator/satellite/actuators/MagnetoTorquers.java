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

package msp.simulator.satellite.actuators;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 * @author Jack McRobbie
 */
public class MagnetoTorquers {
	private Vector3D orientation; 
	private Vector3D MaxDipole; 
	

	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(MagnetoTorquers.class);

	/**
	 * 
	 */
	public MagnetoTorquers() {
		logger.info(CustomLoggingTools.indentMsg(logger,
				"Building the MagnetoTorquers..."));
		this.orientation = new Vector3D(1,1,1);
		this.MaxDipole = new Vector3D(0.1,0.1,0.1); //TODO make configurable from simulation initialization 
		
	}
	public Vector3D computeDipole(Vector3D dutyCycle) {
		Vector3D dipole = new Vector3D(
				 dutyCycle.getX() * orientation.getX(),
				 dutyCycle.getY() * orientation.getY(), 
				 dutyCycle.getZ() * orientation.getZ()
				 ); 
		return dipole;
	}

}
