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

package msp.simulator.dynamic.forces;

import java.util.ArrayList;

import org.orekit.forces.ForceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.satellite.Satellite;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * Top class to represents all of the linear forces
 * in the simulation.
 * This class is responsible to build all of its sub-classes
 * and to provide a set of tools and methods to access and 
 * maintain them.
 * 
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class Forces {
	
	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(Forces.class);
	
	/** List of all of the registered Force Models. */
	private ArrayList<ForceModel> listOfForces;
	
	/** Instance of Atmospheric Drag force model. */
	private AtmosphericDrag atmosphericDrag;
	
	/** Instance of Solar Radiation Pressure force model. */
	private RadiationPressure solarRadiationPressure;
	
	/** Instance of Earth Ghravity Attraction force model. */
	private EarthGravityAttraction earthGravityAttraction;
	
	/** Instance of the gravity attraction of a third body. */
	private MoonThirdBodyAttraction moonThirdBodyAttraction;
	
	public Forces(Environment environment, Satellite satellite) {
		Forces.logger.info(CustomLoggingTools.indentMsg(logger, 
				"Creating the Force Models..."));
		
		this.atmosphericDrag = new AtmosphericDrag(environment, satellite);
		this.solarRadiationPressure = new RadiationPressure(environment, satellite);
		this.earthGravityAttraction = new EarthGravityAttraction(environment);
		this.moonThirdBodyAttraction = new MoonThirdBodyAttraction(environment);
		
		/* Build a list of implemented forces. */
		this.listOfForces = new ArrayList<ForceModel>() ;
		this.listOfForces.add(this.atmosphericDrag);
		this.listOfForces.add(this.solarRadiationPressure);
		this.listOfForces.add(this.earthGravityAttraction);

	}

	/**
	 * @return the Atmospheric Drag Force Model.
	 */
	public AtmosphericDrag getAtmosphericDrag() {
		return atmosphericDrag;
	}

	/**
	 * @return the Solar Radiation Pressure Froce Model.
	 */
	public RadiationPressure getSolarRadiationPressure() {
		return solarRadiationPressure;
	}

	/**
	 * @return the Earth Gravity Attraction Force Model.
	 */
	public EarthGravityAttraction getEarthGravityAttraction() {
		return earthGravityAttraction;
	}

	/**
	 * @return ArrayList of the implemented Force Models.
	 */
	public ArrayList<ForceModel> getListOfForces() {
		return listOfForces;
	}

	/**
	 * @return the Moon Third Body Attraction
	 */
	public MoonThirdBodyAttraction getMoonThirdbodyAttraction() {
		return moonThirdBodyAttraction;
	}
	
	

}
