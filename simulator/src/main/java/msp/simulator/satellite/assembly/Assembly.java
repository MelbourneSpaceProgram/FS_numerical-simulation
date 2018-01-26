/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite.assembly;

import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class Assembly {

	/* **************************************************************	*/
	/* 				STATIC FIELD FOR CUBESAT PARAMETERS.				*/
	/* **************************************************************	*/
	
	/** Static field describing the length of the sides of the cube body. */
	public static double cs1_Length = 0.010; /* m */
	
	/** Static field describing the Satellite Body Mass. */
	public static double cs1_Mass = 1.04 ; /* kg */
	
	/** Static field describing the satellite inertia matrix. */
//	public static double[][] cs1_IMatrix =  /* kg.m^2 */ {
//			{1191.648 * 1.3e-6,           0       ,           0        },
//			{         0       ,  1169.506 * 1.3e-6,           0        },
//			{         0       ,           0       ,  1203.969 * 1.3e-6 },
//		};

	public static double[][] cs1_IMatrix =  /* kg.m^2 */ {
			{ 1,   0,   0 },
			{ 0,   1,   0 },
			{ 0,   0,   1 }
		};

	
	/* **************************************************************	*/
	
	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(Assembly.class);
	
	/** Instance of the satellite body for the assembly. */
	private SatelliteBody satelliteBody;
	
	/** Instance of the satellite initial state in space. */
	private SatelliteStates satelliteStates;
	
	/**
	 * Build the satellite as a body and a state vector.
	 * 
	 * @param environment Use to extract the Sun body to create a
	 * radiation sensitive satellite body.
	 */
	public Assembly(Environment environment) {
		Assembly.logger.info(CustomLoggingTools.indentMsg(Assembly.logger,
				"Assembly in process..."));
		
		this.satelliteBody = new SatelliteBody(environment);
		this.satelliteStates = new SatelliteStates(environment);
	}
	
	/**
	 * Return the satellite body as a CubeSat box sensitive
	 * to radiation and drag.
	 * @return BoxAndSolarArraySpaceCraft (DragSensitive, RadiationSensitive)
	 */
	public SatelliteBody getBody() {
		return this.satelliteBody;
	}
	
	/**
	 * Return the satellite states of the satellite
	 * directly from the Assembly Object.
	 * 
	 * @return SpacecraftState
	 * @see Assembly
	 */
	public SatelliteStates getStates() {
		return this.satelliteStates;
	}
	
	/**
	 * @return The Satellite frame: a non-inertial rotating
	 * frame fixed with the axis body.
	 */
	public Frame getSatelliteFrame() {
		return new Frame (
				FramesFactory.getEME2000(),
				this.getStates().getCurrentState().toTransform(),
				"SatelliteFrame"
				);
	}
	

}
