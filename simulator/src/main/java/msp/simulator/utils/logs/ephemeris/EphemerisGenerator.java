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

package msp.simulator.utils.logs.ephemeris;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.hipparchus.geometry.euclidean.threed.Rotation;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.errors.OrekitException;
import org.orekit.frames.FramesFactory;
import org.orekit.models.earth.GeoMagneticElements;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.satellite.Satellite;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * This class provides a set of tools and methods to
 * generate the ephemeris to export along the simulation.
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class EphemerisGenerator {

	/** Instance of the Logger of the class. */
	private static final Logger logger = 
			LoggerFactory.getLogger(EphemerisGenerator.class);

	/* ****** Default Values **** */

	/** Ephemeris time step in seconds. */
	public static double ephemerisTimeStep = 1.0; /* seconds */

	/** Default absolute path of the ephemeris folder. */
	public static String DEFAULT_PATH =
			System.getProperty("user.dir") + System.getProperty("file.separator") 
			+ "src" + System.getProperty("file.separator")
			+ "main" + System.getProperty("file.separator")
			+ "resources" + System.getProperty("file.separator")
			+ "ephemeris" + System.getProperty("file.separator")
			;


	/** Default Name of the Simulation. */
	public static String DEFAULT_SIMU_NAME =
			"MSP-SIM-V.0.1-" + 
					/* Date Format. */
					LocalDateTime.now().format(
							DateTimeFormatter.ofPattern("dd.MM@HH.mm"))
					+ "-";

	/** New Line Symbol. */
	protected final static String LS = System.getProperty("line.separator");

	/** Name of the orbital object. */
	public static String OBJECT_NAME = "MSP_CubeSat_1";

	/** Name of the current Simulation. */
	public static String SIMU_ID = "MSP_SIM_0.1";

	/* ************************* */

	/** Absolute path of the ephemeris folder. */
	private String path ;

	/** Common name of the ephemeris. */
	private String simuName;

	/** OrbitWrapper OEM ephemeris. */
	private File fileOEM;

	/** Attitude AEM ephemeris. */
	private File fileAEM;
	
	/** Vector for the magnetic field */
	
	private File fileAEM_mag; 
	
	/** Vector for the angular momentum */ 
	
	private File fileAEM_angMomentum; 
	
	/** vector for the angular velocity */
	
	private File fileAEM_angVelocity; 
	
	/** Vector for applied torque */ 
	
	private File fileAEM_torque; 

	/** Attitude AEM File Writer. */
	private FileWriter writerAEM_mag; 
	
	private FileWriter writerAEM;
	
	private FileWriter writerAEM_angMomentum; 
	private FileWriter writerAEM_angVel;
	private FileWriter writerAEM_torque;

	/** OrbitWrapper OEM File Writer */
	private FileWriter writerOEM;

	/** Capture the first date of the ephemeris. */
	boolean isStartDateCaptured = false;

	/**
	 * Create the ephemeris generator.
	 */
	public EphemerisGenerator() {
		this(DEFAULT_PATH, DEFAULT_SIMU_NAME);
	}

	/**
	 * Create the ephemeris generator.
	 * @param simuName Name of the simulation - Append to the ephemeris.
	 */
	public EphemerisGenerator(String simuName) {
		this(DEFAULT_PATH, simuName);
	}

	/**
	 * Create the ephemeris generator.
	 * @param path Folder Path
	 * @param simuName Name of the simulation - Append to the ephemeris.
	 */
	public EphemerisGenerator(
			String path,
			String simuName) {
		logger.info(CustomLoggingTools.indentMsg(logger, 
				"Building the Ephemeris Generator..."));

		this.simuName = simuName;
		this.path = path;
	}

	/**
	 * Initialize the ephemeris by creating the appropriate files
	 * and writing down the required headers.
	 */
	public void start() {
		/* Creating the directory. */
		String common = this.path + this.simuName;
		this.fileOEM = new File(common + "OEM.txt");
		this.fileAEM = new File(common + "AEM.txt");
		this.fileAEM_mag = new File(common + "body_mag-" + "AEM.txt");
		this.fileAEM_angVelocity = new File(common + "ang_mom-" + "AEM.txt");
		this.fileAEM_angMomentum = new File(common + "ang_vel-" + "AEM.txt");
		this.fileAEM_torque = new File(common + "torque-" + "AEM.txt");

		if (!fileOEM.getParentFile().exists()) {
			fileOEM.getParentFile().mkdirs();
		}
		if (!fileAEM.getParentFile().exists()) {
			fileAEM.getParentFile().mkdir();
		}
		if (!fileAEM_mag.getParentFile().exists()) {
			fileAEM_mag.getParentFile().mkdir(); 
		}
		if (!fileAEM_angVelocity.getParentFile().exists()) {
			fileAEM_angVelocity.getParentFile().mkdir(); 
		}
		if (!fileAEM_angMomentum.getParentFile().exists()) {
			fileAEM_angMomentum.getParentFile().mkdir(); 
		}
		if (!fileAEM_torque.getParentFile().exists()) {
			fileAEM_torque.getParentFile().mkdir(); 
		}
		

		try {
			
			/* Creating the files. */
			fileOEM.createNewFile();
			fileAEM.createNewFile();
			fileAEM_mag.createNewFile(); 
			fileAEM_angMomentum.createNewFile();
			fileAEM_angVelocity.createNewFile(); 
			fileAEM_torque.createNewFile();
			
			/* Creating each associated Writer. */
			this.writerOEM = new FileWriter(this.fileOEM);
			this.writerAEM = new FileWriter(this.fileAEM);
			this.writerAEM_mag = new FileWriter(this.fileAEM_mag);
			this.writerAEM_angVel = new FileWriter(this.fileAEM_angVelocity); 
			this.writerAEM_angMomentum = new FileWriter(this.fileAEM_angMomentum);
			this.writerAEM_torque = new FileWriter(this.fileAEM_torque);
			/* Generating the headers. */
			this.writerAEM.write(this.getAemHeader(OBJECT_NAME, SIMU_ID));
			this.writerOEM.write(this.getOemHeader(OBJECT_NAME, SIMU_ID));
			this.writerAEM_mag.write(this.getVectorVisHeader("MAGNETIC_FIELD",OBJECT_NAME, SIMU_ID));
			this.writerAEM_angMomentum.write(this.getVectorVisHeader("ANGULAR-MOMENTUM", OBJECT_NAME,SIMU_ID));
			this.writerAEM_angVel.write(this.getVectorVisHeader("ANGULAR-VELOCITY", OBJECT_NAME,SIMU_ID));
			this.writerAEM_torque.write(this.getVectorVisHeader("TORQUE", OBJECT_NAME,SIMU_ID));
			this.writerOEM.flush();
			this.writerAEM.flush();
			this.writerAEM_mag.flush();
			this.writerAEM_angMomentum.flush();
			this.writerAEM_angVel.flush();
			this.writerAEM_torque.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stopping the generation of the ephemeris.
	 */
	public void stop() {
		try {
			this.writerOEM.close();
			this.writerAEM.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Append the line corresponding the current satellite step to the
	 * ephemeris file.
	 * @param satState The satellite state to register in the ephemeris
	 */
	public void writeStep(Satellite satellite) {
		SpacecraftState newState = satellite.getStates().getCurrentState();
		try {
	

			/* Determining the time of the state (in offset). */
			AbsoluteDate currentDate = newState.getDate();
			AbsoluteDate referenceDate = AbsoluteDate.MODIFIED_JULIAN_EPOCH;

			double seconds = currentDate.offsetFrom(
					referenceDate,
					TimeScalesFactory.getUTC()
					);
			int days = (int) (seconds / Constants.JULIAN_DAY);
			seconds = seconds - days * Constants.JULIAN_DAY;
		
			StringBuffer buff = new StringBuffer();
			
			/** Writing to the torque file */ 
			
			/** TODO - rewrite this section to be more elegant and correct*/
			
			Vector3D ang_accel = satellite.getStates().getCurrentState().getAttitude().getRotationAcceleration();
			
			/** 
			 * 
			 *  WARNING!: THis only works for diagonal inertia matrices. 
			 *  TODO Resolve this so it works for non diagonal systems.
			 *  */
			
			double torqueX = ang_accel.getX()/satellite.getAssembly().getBody().getInertiaMatrix()[0][0];
			double torqueY = ang_accel.getY()/satellite.getAssembly().getBody().getInertiaMatrix()[1][1];
			double torqueZ = ang_accel.getZ()/satellite.getAssembly().getBody().getInertiaMatrix()[2][2];
			
			buff
			.append(days)
			.append(" ") 					/* Column Separator */
			.append(seconds)
			.append(" ")
			.append(torqueX)
			.append(" ")
			.append(torqueY)
			.append(" ")
			.append(torqueZ)
			;
			this.writerAEM_torque.append(buff+LS);
			this.writerAEM_torque.flush();
			
			buff = new StringBuffer();
			
			/** Writing to the angular velocity file */ 
			Vector3D vel = satellite.getStates().getCurrentState().getAttitude().getSpin();
			buff
			.append(days)
			.append(" ") 					/* Column Separator */
			.append(seconds)
			.append(" ")
			.append(vel.getX()) 	
			.append(" ")
			.append(vel.getY())
			.append(" ")
			.append(vel.getZ())
			;
			this.writerAEM_angVel.append(buff+LS);
			this.writerAEM_angVel.flush();
			
			buff = new StringBuffer();
			
			/** Writing to the angular momentum file */ 
			Vector3D mom = satellite.getAssembly().getAngularMomentum();
			buff
			.append(days)
			.append(" ") 					/* Column Separator */
			.append(seconds)
			.append(" ")
			.append(mom.getX()) 	
			.append(" ")
			.append(mom.getY())
			.append(" ")
			.append(mom.getZ())
			;
			this.writerAEM_angMomentum.append(buff+LS);
			this.writerAEM_angMomentum.flush();
			
			buff = new StringBuffer();
			
			/* Writing the current mag field to the log file. */ 
			Vector3D mag_field = satellite.getSensors().getMagnetometer().retrievePerfectField().getFieldVector();
			buff
			.append(days)
			.append(" ") 					/* Column Separator */
			.append(seconds)
			.append(" ")
			.append(mag_field.getX()) 	
			.append(" ")
			.append(mag_field.getY())
			.append(" ")
			.append(mag_field.getZ())
			;
			this.writerAEM_mag.append(buff.toString()+ LS);
			this.writerAEM_mag.flush();
			buff = new StringBuffer();
			/* Writing the OEM Ephemeris. */
			Vector3D position = newState
					.getPVCoordinates(FramesFactory.getEME2000())
					.getPosition();

			buff
			.append(days)
			.append(" ") 					/* Column Separator */
			.append(seconds)
			.append(" ")
			.append(position.getX() * 1e-3) 	/* Conversion to KM */
			.append(" ")
			.append(position.getY() * 1e-3)
			.append(" ")
			.append(position.getZ() * 1e-3)
			;
			

			this.writerOEM.append(buff.toString() + LS);
			this.writerOEM.flush();

			/* Writing the AEM ephemerides. */
			Rotation inertialRotation= newState
					.getAttitude()
					.getRotation()
					.revert(); /* Reverse the Rotation */ 

			buff = new StringBuffer();
			buff
			.append(days)
			.append(" ")
			.append(seconds)
			.append(" ")
			.append(inertialRotation.getQ0())
			.append(" ")
			.append(inertialRotation.getQ1())
			.append(" ")
			.append(inertialRotation.getQ2())
			.append(" ")
			.append(inertialRotation.getQ3())
			;

			this.writerAEM.append(buff.toString() + LS);
			this.writerAEM.flush();

			/* For DEBUG only. */
			logger.info(
					"Satellite State to store in the ephemeris:\n" +
							"State Date: " + newState.getDate().toString() + 
							"\n" +
							"Attitude: [{}, {}, {}, {}] \n" +
							"Spin    : {} \n" +
							"RotAcc  : {}\n" +
							"Momentum: {}",
							newState.getAttitude().getRotation().getQ0(),
							newState.getAttitude().getRotation().getQ1(),
							newState.getAttitude().getRotation().getQ2(),
							newState.getAttitude().getRotation().getQ3(),
							newState.getAttitude().getSpin().toString(),
							newState.getAttitude().getRotationAcceleration().toString(),
							satellite.getAssembly().getAngularMomentum()
					);

		} catch (OrekitException | IOException e) {
			e.printStackTrace();
		}
	}
	/** 
	 * Returns the header of the Attitude file for VTS vector visualization
	 * @param vector name - name of the vector
	 * @param objectName For the Satellite Object
	 * @param simuIdentifier Current Simulation ID
	 * @return Header as a string
	 */
private String getVectorVisHeader(String vectorName, String objectName, String simuIdentifier) {
	try {
		String headerAEM = new String();

		AbsoluteDate currentDate = new AbsoluteDate(
				new GregorianCalendar(TimeZone.getTimeZone("GMT+00")).getTime(),
				TimeScalesFactory.getUTC()
				);

		headerAEM += "CIC_AEM_VERS = 1.0" + LS ;
		headerAEM += ("CREATION_DATE = " + 
				currentDate.toString(TimeScalesFactory.getUTC()) ) + LS ;
		headerAEM += ("ORIGINATOR = ") + simuIdentifier + LS ;
		headerAEM += ("     ") + LS ;
		headerAEM += ("META_START") + LS ;
		headerAEM += ("") + LS ;
		headerAEM += ("OBJECT_NAME = ") + objectName +"-"+ vectorName + LS ;
		headerAEM += ("OBJECT_ID = MSP001") + LS ;
		headerAEM += ("CENTER_NAME = EARTH") + LS ;
		headerAEM += ("REF_FRAME_A = EME2000") + LS ;
		headerAEM += ("REF_FRAME_B = SC_BODY_1") + LS ;
		headerAEM += ("ATTITUDE_DIR = A2B") + LS ;
		headerAEM += ("TIME_SYSTEM = UTC") + LS ;
		headerAEM += ("ATTITUDE_TYPE = QUATERNION") + LS ;
		headerAEM += ("") + LS ;
		headerAEM += ("META_STOP") + LS ;

		return headerAEM; 
		}
	catch (OrekitException e) {
		e.printStackTrace();
	}
	return null; 
}
	/**
	 * Return the header of an AEM ephemeris.
	 * @param objectName For the Satellite Object
	 * @param simuIdentifier Current Simulation ID
	 * @return Header as a string
	 */
	private String getAemHeader(String objectName, String simuIdentifier) {
		try {
			String headerAEM = new String();

			AbsoluteDate currentDate = new AbsoluteDate(
					new GregorianCalendar(TimeZone.getTimeZone("GMT+00")).getTime(),
					TimeScalesFactory.getUTC()
					);

			headerAEM += "CIC_AEM_VERS = 1.0" + LS ;
			headerAEM += ("CREATION_DATE = " + 
					currentDate.toString(TimeScalesFactory.getUTC()) ) + LS ;
			headerAEM += ("ORIGINATOR = ") + simuIdentifier + LS ;
			headerAEM += ("     ") + LS ;
			headerAEM += ("META_START") + LS ;
			headerAEM += ("") + LS ;
			headerAEM += ("OBJECT_NAME = ") + objectName + LS ;
			headerAEM += ("OBJECT_ID = MSP001") + LS ;
			headerAEM += ("CENTER_NAME = EARTH") + LS ;
			headerAEM += ("REF_FRAME_A = EME2000") + LS ;
			headerAEM += ("REF_FRAME_B = SC_BODY_1") + LS ;
			headerAEM += ("ATTITUDE_DIR = A2B") + LS ;
			headerAEM += ("TIME_SYSTEM = UTC") + LS ;
			headerAEM += ("ATTITUDE_TYPE = QUATERNION") + LS ;
			headerAEM += ("") + LS ;
			headerAEM += ("META_STOP") + LS ;

			return headerAEM;

		} catch (OrekitException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Return the header of an OEM ephemeris.
	 * @param objectName of the Satellite Object
	 * @param simuIdentifier Unique identifier.
	 * @return Header as a string
	 */
	private String getOemHeader(String objectName, String simuIdentifier) {
		try {
			String headerOEM = new String();

			AbsoluteDate currentDate = new AbsoluteDate(
					new GregorianCalendar(TimeZone.getTimeZone("GMT+00")).getTime(),
					TimeScalesFactory.getUTC()
					);

			headerOEM += "CIC_OEM_VERS = 2.0" + LS ;
			headerOEM += ("CREATION_DATE = " + 
					currentDate.toString(TimeScalesFactory.getUTC()) ) + LS ;
			headerOEM += ("ORIGINATOR = ") + simuIdentifier + LS ;
			headerOEM += ("     ") + LS ;
			headerOEM += ("META_START") + LS ;
			headerOEM += ("") + LS ;
			headerOEM += ("OBJECT_NAME = ") + objectName + LS ;
			headerOEM += ("OBJECT_ID = MSP001") + LS ;
			headerOEM += ("CENTER_NAME = EARTH") + LS ;
			headerOEM += ("REF_FRAME = EME2000") + LS ;
			headerOEM += ("TIME_SYSTEM = UTC") + LS ;
			headerOEM += ("") + LS ;
			headerOEM += ("META_STOP") + LS ;

			return headerOEM;

		} catch (OrekitException e) {
			e.printStackTrace();
		}
		return null;
	}

}
