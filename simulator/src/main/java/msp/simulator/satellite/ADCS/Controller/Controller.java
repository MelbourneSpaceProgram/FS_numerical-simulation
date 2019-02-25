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
package msp.simulator.satellite.ADCS.Controller;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.satellite.Satellite;
import msp.simulator.satellite.ADCS.ADCSPhysics.ADCSPhysics;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Jack McRobbie 
 */
public class Controller {
	
	private static final Logger logger = 
			LoggerFactory.getLogger(Controller.class);
	
	B_Dot bdot;
	Satellite sat; 
	public Controller(Satellite satellite) {
		this.sat = satellite; 
		bdot = new B_Dot(this.sat);
		Controller.logger.info(CustomLoggingTools.indentMsg(this.logger, 
				" -> Building the ADCS Controller: Success."));
		
	}
	public Vector3D getDipole() {
		Vector3D result  = this.bdot.computeDipole();
		Controller.logger.info(result.toString());
		return result;
		
	}
}
