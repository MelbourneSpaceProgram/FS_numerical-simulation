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
package msp.simulator.satellite.ADCS.Estimators.BdotEstimator;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.satellite.Satellite;
import msp.simulator.satellite.ADACS.sensors.Magnetometer;
import msp.simulator.satellite.ADCS.Actuators.Actuators;
import msp.simulator.utils.logs.CustomLoggingTools;
/**
 *
 * @author Jack McRobbie
 */
public class BdotEstimator {
	private LowPassFilter lowpassfilter;
	private Magnetometer mag; 
	private Vector3D lastMagFieldReading; 
	private final double timestep;
	private Satellite satellite; 
	private static final Logger logger = LoggerFactory.getLogger(BdotEstimator.class);
	
	public BdotEstimator(Satellite sat) {
		Vector3D initalState = new Vector3D(0.0,0.0,0.0);
		lowpassfilter = new LowPassFilter(5.0,0.25,initalState);
		timestep = 0.1; // TODO make equal to Controller frequency!
		satellite = sat;
		this.lastMagFieldReading= Vector3D.NEGATIVE_INFINITY;
		BdotEstimator.logger.info(CustomLoggingTools.indentMsg(BdotEstimator.logger,
				"Building the Bdot Estimator..."));
	}
	public Vector3D computeBdot() {
		Vector3D bDotUnfiltered = this.getFirstOrderDiff(); 
		Vector3D bdot = lowpassfilter.ProcessSample(bDotUnfiltered);
		logger.info("Bdot estimate" + bdot.toString());
		return bdot; 
	}
	private Vector3D getFirstOrderDiff() {
		this.mag = this.satellite.getADCS().getSensors().getMagnetometer();
		Vector3D magreading = mag.retrieveNoisyField().getFieldVector().scalarMultiply(10^-9);
		if(this.lastMagFieldReading == Vector3D.NEGATIVE_INFINITY) {
			this.lastMagFieldReading = magreading;
			return Vector3D.ZERO;
		}
		double x  = magreading.getX() - this.lastMagFieldReading.getX();
		double y  = magreading.getY() - this.lastMagFieldReading.getY();
		double z  = magreading.getZ() - this.lastMagFieldReading.getZ();
		this.lastMagFieldReading = magreading;
		Vector3D result = new Vector3D(x/this.timestep,y/this.timestep,z/this.timestep); 
		return result;
	}

}
