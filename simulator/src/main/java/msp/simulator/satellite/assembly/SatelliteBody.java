/* Copyright 2017-2018 Melbourne Space Program */

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
 * @author Florian CHAUBEYRE
 */
public class SatelliteBody extends BoxAndSolarArraySpacecraft {

	/* ******* Public Static Attributes ******* */

	/** Size of the satellite box in meters - (x, y, z) */
	public static double[] satBoxSizeWithNoSolarPanel = new double[] {0.01, 0.01, 0.01} ;

	/* Note that the size of the solar panels is set to zero in the class constructor.*/

	/** Mass of the satellite in kilogram. */
	public static double satelliteMass = 1.04;

	/* TODO(rskew) update inertia matrix. */
	/** Inertia matrix of the satellite. */
	public static double[][] satInertiaMatrix =  /* kg.m^2 */ {
			{1191.648 * 1.3e-6,           0       ,           0        },
			{         0       ,  1169.506 * 1.3e-6,           0        },
			{         0       ,           0       ,  1203.969 * 1.3e-6 },
	};

	/** Simple balance inertia matrix (Unit matrix). */
	public static double[][] simpleBalancedInertiaMatrix = {
			{ 1,   0,   0 },
			{ 0,   1,   0 },
			{ 0,   0,   1 }
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

	/** TODO: Initialize properly. */
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
