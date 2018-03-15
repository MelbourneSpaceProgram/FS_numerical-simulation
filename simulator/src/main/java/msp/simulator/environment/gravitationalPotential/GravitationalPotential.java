/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment.gravitationalPotential;

import org.orekit.errors.OrekitException;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.ICGEMFormatReader;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class represents the potential gravity of the Earth used
 * in the numerical simulator.<p>
 * 
 * This class is directly related to OREKIT but for a better
 * understanding of the modules of the simulator, the functionalities
 * are linked here.
 * It also provides some other methods and tools useful to some other
 * modules of the simulator, e.g. the forces and the dynamic.
 * 
 * @author Florian CHAUBEYRE
 */
public class GravitationalPotential {

	/** Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(
			GravitationalPotential.class);

	private NormalizedSphericalHarmonicsProvider spericalHarmonicProvider;

	public GravitationalPotential() {
		logger.info(CustomLoggingTools.indentMsg(logger,
				"Loading the Earth Potential Gravity Field..."));
		
		/* Loading the Earth Gravity Field in OreKit. */
		GravityFieldFactory.addPotentialCoefficientsReader(
				new ICGEMFormatReader(GravityFieldFactory.ICGEM_FILENAME, true));
		try {
			this.spericalHarmonicProvider = 
					GravityFieldFactory.getConstantNormalizedProvider(6, 6);
		} catch (OrekitException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Return the provider of the gravity model harmonic fields.
	 * The coefficients are initially read in a resource file.
	 * 
	 * @return Provider of the gravity field coefficient.
	 * @see GravityFieldFactory
	 */
	public NormalizedSphericalHarmonicsProvider 
	getNormalizedSphericalHarmonicCoeffProvider() {
		return this.spericalHarmonicProvider;
	}
}
