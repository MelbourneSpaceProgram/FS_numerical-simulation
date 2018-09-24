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

package msp.simulator.satellite.sensors;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.errors.OrekitException;
import org.orekit.models.earth.GeoMagneticElements;
import org.orekit.propagation.SpacecraftState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.environment.Environment;
import msp.simulator.environment.geomagneticField.EarthMagneticField;
import msp.simulator.environment.solarSystem.Earth;
import msp.simulator.satellite.assembly.Assembly;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class represents the magnetometer sensor of the
 * satellite.
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class Magnetometer {

	/* ******* Public Static Attributes ******* */

	/** This intensity is used to generate a random number to be
	 * added to each components of the true magnetic field. 
	 * (nanoTesla)
	 */
	public static double defaultMagnetoNoiseIntensity = 1e2 ;

	/* **************************************** */

	/** Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(
			Magnetometer.class);

	/** Instance of the OreKit Magnetic Field. */
	private EarthMagneticField geomagField;

	/** Instance of the Earth. */
	private Earth earth;

	/** Assembly of the satellite. */
	private Assembly assembly;

	/** Private attribute for the noise intensity. */
	private double noiseIntensity;

	public Magnetometer(Environment environment, Assembly assembly) {
		logger.info(CustomLoggingTools.indentMsg(logger,
				" -> Building the Magnetometer..."));

		/* Linking the class to the rest of the simulation. */
		this.geomagField = environment.getGeoMagneticField();
		this.earth = environment.getSolarSystem().getEarth();
		this.assembly = assembly;

		/* Initializing the class. */
		this.noiseIntensity = Magnetometer.defaultMagnetoNoiseIntensity;
	}

	/**
	 * Return a measurement disturbed by a random noise.
	 * The intensity of the noise factor can be modified at
	 * initialization.
	 * @return GeoMagneticElements (where field vector is expressed in nT)
	 * @see #retrievePerfectMeasurement()
	 */
	public GeoMagneticElements retrieveNoisyField() {
		/* Perfect Measure. */
		GeoMagneticElements perfectMeasure = this.retrievePerfectField();

		/* Normally distributed random noise contribution. */
		Vector3D noise = new Vector3D ( new double[] { 
				2 * (FastMath.random() - 0.5) * this.noiseIntensity,
				2 * (FastMath.random() - 0.5) * this.noiseIntensity,
				2 * (FastMath.random() - 0.5) * this.noiseIntensity
		});

		/* Disturbing the perfect measurement. */
		Vector3D noisyFieldVector = 
				perfectMeasure.getFieldVector().add(noise);

		/* Creating the noisy measure. */
		GeoMagneticElements noisyMeasure = new GeoMagneticElements(noisyFieldVector);	

		logger.debug("Noisy Geo" + noisyMeasure.toString());


		return noisyMeasure;
	}



	/**
	 * Retrieve a perfect measured data from the sensors, i.e. an
	 * ideal measurement without any noise or interference.
	 * 
	 * @return GeoMagneticElements at the location of the satellite.
	 * (where field vector is expressed in nT)
	 * @see GeoMagneticElements 
	 */
	public GeoMagneticElements retrievePerfectField() {

		SpacecraftState satState = this.assembly.getStates().getCurrentState() ;

		Vector3D positionOnEarth = 
				satState.getOrbit().getPVCoordinates().getPosition();

		GeodeticPoint geodeticPosition = null;

		try {
			/* The transformation from cartesian to geodetic coordinates is actually
			 * not straight as it needs to solve some 2-unknowns non-linear equations.
			 * So it needs a processing algorithm like the one presented by OreKit
			 * in the following method.
			 */
			geodeticPosition = earth.getEllipsoid().transform(
					positionOnEarth, 
					satState.getOrbit().getFrame(), 
					satState.getDate()
					);
		} catch (OrekitException e) {
			e.printStackTrace();
		}

		/* Calculate the magnetic field at the projected geodetic point.
		 * Note that the algorithm brings some approximation, for instance
		 * the altitude of the satellite is slightly shifted from the true 
		 * one.
		 */
//		GeoMagneticElements trueField_itrf = this.geomagField.getField().calculateField(
//				FastMath.toDegrees(geodeticPosition.getLatitude()),	/* decimal deg */
//				FastMath.toDegrees(geodeticPosition.getLongitude()),	/* decimal deg */
//				(satState.getA() - this.earth.getRadius()) / 1e3		/* km */
//				);
		
		GeoMagneticElements trueField_itrf = this.geomagField.getField().calculateField(
				0.007707323868690944,	/* decimal deg */
				-156.00687854677085,	/* decimal deg */
				574.9999944513394/* km */
				);

		logger.debug("Magnetometer Measurement: \n" +
				"Latitude: " + FastMath.toDegrees(geodeticPosition.getLatitude()) + " °\n" +
				"Longitud: " + FastMath.toDegrees(geodeticPosition.getLongitude()) + " °\n" +
				"Altitude: " + (satState.getA() - this.earth.getRadius()) / 1e3 + " km\n" +
				"(ITRF): " + trueField_itrf.toString()
				);

		/* Rotate the magnetic field reading into the body frame */
		Vector3D trueField_body = this.assembly.getItrf2body(satState.getDate())
				.transformVector(trueField_itrf.getFieldVector());

		GeoMagneticElements trueGeoMag_body = new GeoMagneticElements(trueField_body);
		
		return trueGeoMag_body;
	}

	/**
	 * Retrieve the measured data from the magnetometer sensors.
	 * <p>
	 * WARNING: a noise is always introduced component by component
	 * on the returned value. Storing the vector before working on
	 * it may be required.
	 * 
	 * @return Geomagnetic Field Vector (in Tesla)
	 */
	public Vector3D getData_magField() {
		/* Retrieve the noisy magnetic field. */
		Vector3D data = this.retrieveNoisyField().getFieldVector();

		/* Convert nTesla into Tesla. */
		data = data.scalarMultiply(1e-9);

		return data;
	}

	/**
	 * @return The noise intensity in nTesla.
	 */
	public double getNoiseIntensity() {
		return noiseIntensity;
	}

}
