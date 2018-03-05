/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment.geomagneticField;

import org.orekit.errors.OrekitException;
import org.orekit.models.earth.GeoMagneticField;
import org.orekit.models.earth.GeoMagneticFieldFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.architecture.OrekitConfiguration;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class is responsible to simulate the Earth Magnetic
 * Field in the environment.
 * It creates a link through the singleton object created in
 * OreKit and provides some other tools to access and maintain
 * the object.<p>
 * 
 * This class is explicity in the simulator package for clarity
 * purpose.
 * 
 * @see GeoMagneticField
 * @author Florian CHAUBEYRE
 */
public class EarthMagneticField {
	
	/** Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(
			EarthMagneticField.class);
	
	/** Private instance of the GeoMagneticField. */
	private GeoMagneticField geomagneticField;
	
	
	/**
	 * Create the instance of Earth Magnetic Field following
	 * the WMM data as the current Year.
	 */
	public EarthMagneticField() {
		logger.info(CustomLoggingTools.indentMsg(logger,
				"Loading the Earth Magnetic Field..."));
		try {
			/* Create and link the instance of GeoMagnetic Field. */
			this.geomagneticField = GeoMagneticFieldFactory.
					getWMM(OrekitConfiguration.GeoMagneticDataYear);
		
		} catch (OrekitException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Return the GeoMagneticField object.
	 * @return GeoMagneticField
	 * @see GeoMagneticField
	 */
	public GeoMagneticField getField() {
		return this.geomagneticField;
	}
	
}
