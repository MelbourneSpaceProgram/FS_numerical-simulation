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

/**
 *
 * @author Jack McRobbie
 */
public class LowPassFilter {
	private final String Constructor3d = "Vector3D"; 
	private final String ConstructorDouble = "double";
			
	private double timeConstant; 
	private double samplePeriodMili; 
	private double state; 
	private Vector3D state3d;
	private double alpha;
	private String flag;  
	public LowPassFilter(double tc, double spms,double initialState) {
		this.timeConstant = tc; 
		this.samplePeriodMili = spms; 
		this.alpha = Math.exp(spms/tc);
		state = initialState;
		this.flag = this.ConstructorDouble;
	}
	public LowPassFilter(double tc, double spms, Vector3D initState) {
		this.timeConstant = tc; 
		this.samplePeriodMili = spms; 
		this.alpha = Math.exp(-spms/tc);
		
		/* Utilize multiplicative class constructor returns same as initState */
		state3d = new Vector3D(1.0,initState); 
		this.flag = this.Constructor3d; 
	}
	public double ProcessSample(double new_sample) {
		this.state = this.alpha * this.state + (1 - this.alpha) * new_sample;
		return this.state;
	}
	public Vector3D ProcessSample(Vector3D new_sample) {
		double x = this.alpha * this.state3d.getX() + (1 - this.alpha) * new_sample.getX();
		double y = this.alpha * this.state3d.getY() + (1 - this.alpha) * new_sample.getY();
		double z = this.alpha * this.state3d.getZ() + (1 - this.alpha) * new_sample.getZ();
		this.state3d = new Vector3D(x,y,z);
		return this.state3d;
	}
	public double getSamplePeriod() {
		return this.samplePeriodMili/1000.0;
	}
	
	public double getTimeConstant() {
		return this.timeConstant;
	}
}
