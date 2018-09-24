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

package msp.simulator.satellite.assembly;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.forces.BoxAndSolarArraySpacecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * Represents the physical satellite body of the satellite: 
 * dimensions, mass, inertia...
 * 
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class SatelliteBody extends BoxAndSolarArraySpacecraft {

	/* ******* Public Static Attributes ******* */

	/** Size of the satellite box in meters - (x, y, z) */
	public static final double[] satBoxSizeWithNoSolarPanel = new double[] {0.01, 0.01, 0.01} ;

	/* Note that the size of the solar panels is set to zero in the class constructor.*/

	/** Mass of the satellite in kilogram. */
	public static final double satelliteMass = 1.04;

	/** Default inertia matrix, set by the Dashboard. */
	public static double[][] satInertiaMatrix =  /* kg.m^2 */ {
			{1191.648 * 1.3e-6,           0       ,           0        },
			{         0       ,  1169.506 * 1.3e-6,           0        },
			{         0       ,           0       ,  1203.969 * 1.3e-6 },
	};

  /* TODO(rskew) update inertia matrix. */
  /** Inertia matrix of ACRUX1. */
  public static final double[][] ACRUX1InertiaMatrix =  /* kg.m^2 */ {
      {1191.648 * 1.3e-6,           0       ,           0        },
      {         0       ,  1169.506 * 1.3e-6,           0        },
      {         0       ,           0       ,  1203.969 * 1.3e-6 },
  };

	/** Simple balance inertia matrix (Unit matrix). */
	public static final double[][] simpleBalancedInertiaMatrix = {
			{ 1e-3,   0   ,   0 },
			{ 0   ,   1e-3,   0 },
			{ 0   ,   0   ,   1e-3 }
	};

	/* **************************************** */

	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(SatelliteBody.class);

	/** Satellite Box Size with no Solar Array. */
	private double[] satBoxSize;

	/** Mass of the satellite. */
	private double satMass;

	/** Inertia matrix of the satellite. */
	private double[][] inertiaMatrix;

	/** 
	 * Drag coefficient of the satellite.
	 * <p>
	 * TODO: Define properly and implement a dynamic drag coefficient.
	 * Calculate the coefficient at each step using the atmospheric model
	 * (to access the density) and compute the apparent face of the satellite.
	 * The coefficient is at the moment always zero, it needs to be updated
	 * through the ParameterDriver.
	 */
	private static final double initialDragCoeff = 0;

	/**
	 * Build the Satellite Body as a CubeSat (Cube with no Solar Arrays)
	 * sensitive to drag and radiation.
	 * @param environment Instance of the Simulation
	 */
	public SatelliteBody(Environment environment) {
		super(
				satBoxSizeWithNoSolarPanel[0],
				satBoxSizeWithNoSolarPanel[1],
				satBoxSizeWithNoSolarPanel[2],
				environment.getSolarSystem().getSun().getPvCoordinateProvider(),
				0,	/* Solar Array Area */
				Vector3D.PLUS_I, /* Solar Array Axis */
				initialDragCoeff,	/* Drag Coefficient: */
				0,	/* Absorption Coefficient */
				0	/* Reflection Coefficient */
				);

		/* Copy the user value into a protected variable. */
		this.satBoxSize = SatelliteBody.satBoxSizeWithNoSolarPanel;
		this.satMass = SatelliteBody.satelliteMass;
		this.inertiaMatrix = SatelliteBody.satInertiaMatrix;
		//this.inertiaMatrix = SatelliteBody.simpleBalancedInertiaMatrix;

		SatelliteBody.logger.info(CustomLoggingTools.indentMsg(SatelliteBody.logger, 
				" -> Building the CubeSat body: Success."));
	}


	/**
	 * @return The satellite size as a box without solar panel.
	 */
	public double[] getSatBoxSize() {
		return satBoxSize;
	}


	/**
	 * @return the satellite mass.
	 */
	public double getSatMass() {
		return satMass;
	}


	/**
	 * @return the inertiaMatrix
	 */
	public double[][] getInertiaMatrix() {
		return inertiaMatrix;
	}

}
