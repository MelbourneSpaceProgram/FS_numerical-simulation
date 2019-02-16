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

package msp.simulator.satellite.ADCS.Actuators;

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
		MagnetoTorquers.logger.info(CustomLoggingTools.indentMsg(logger,
				"Building the MagnetoTorquers..."));
		this.orientation = new Vector3D(1,1,1);
		this.MaxDipole = new Vector3D(0.1,0.1,0.1); //TODO make configurable from simulation initialization 
		
	}
	public Vector3D computeDipole(Vector3D dutyCycle) {
		return this.constrainDipole(dutyCycle);
	}
	/**
	 * @param dutyCycle requested duty cycle for the magnetorquers
	 */
	private Vector3D constrainDipole(Vector3D dutyCycle) {
		double x = dutyCycle.getX();
		double y = dutyCycle.getY(); 
		double z = dutyCycle.getZ();
		int sign;
		Vector3D result = new Vector3D(x,y,z);		
		if(Math.abs(x)>this.MaxDipole.getX()) {
			sign = (0 > dutyCycle.getX())?-1:1;
			x = this.MaxDipole.getX() * sign;
		}
		if(Math.abs(y)>this.MaxDipole.getY()) {
			sign = (0 > dutyCycle.getY())?-1:1;
			y = this.MaxDipole.getY() * sign;
		}
		if(Math.abs(z)>this.MaxDipole.getZ()) {
			sign = (0 > dutyCycle.getZ())?-1:1;
			z = this.MaxDipole.getZ() * sign;
		}
		result = new Vector3D(x,y,z);
		return result;
	}

}
