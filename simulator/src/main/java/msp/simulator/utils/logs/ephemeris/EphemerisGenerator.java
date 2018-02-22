/* Copyright 2017-2018 Melbourne Space Program */

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
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * This class provides a set of tools and methods to
 * generate the ephemeris to export along the simulation.
 *
 * @author Florian CHAUBEYRE
 */
public class EphemerisGenerator {

	/** Instance of the Logger of the class. */
	private static final Logger logger = 
			LoggerFactory.getLogger(EphemerisGenerator.class);

	/* ****** Default Values **** */

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

	/** Orbit OEM ephemeris. */
	private File fileOEM;

	/** Attitude AEM ephemeris. */
	private File fileAEM;

	/** Attitude AEM File Writer. */
	private FileWriter writerAEM;

	/** Orbit OEM File Writer */
	private FileWriter writerOEM;

	/** First Date known by the generator. */
	private AbsoluteDate extractedStartDate;

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

		if (!fileOEM.getParentFile().exists()) {
			fileOEM.getParentFile().mkdirs();
		}
		if (!fileAEM.getParentFile().exists()) {
			fileAEM.getParentFile().mkdir();
		}

		try {
			/* Creating the files. */
			fileOEM.createNewFile();
			fileAEM.createNewFile();

			/* Creating each associated Writer. */
			this.writerOEM = new FileWriter(this.fileOEM);
			this.writerAEM = new FileWriter(this.fileAEM);

			/* Generating the headers. */
			this.writerAEM.write(this.getAemHeader(OBJECT_NAME, SIMU_ID));
			this.writerOEM.write(this.getOemHeader(OBJECT_NAME, SIMU_ID));

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
	 * @param newState The satellite state to register in the ephemeris
	 */
	public void writeStep(SpacecraftState newState) {
		/* Extracting the start date of the generation. */
		if (!this.isStartDateCaptured) {
			this.extractedStartDate = newState.getDate();
			this.isStartDateCaptured = true;
		}

		try {
			StringBuffer buff = new StringBuffer();

			/* Determining the time of the state (in offset). */
			AbsoluteDate currentDate = newState.getDate();
			AbsoluteDate referenceDate = AbsoluteDate.MODIFIED_JULIAN_EPOCH;

			double seconds = currentDate.offsetFrom(
					referenceDate,
					TimeScalesFactory.getUTC()
					);
			int days = (int) (seconds / Constants.JULIAN_DAY);
			seconds = seconds - days * Constants.JULIAN_DAY;

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
							"Offset: " +
							newState.getDate().durationFrom(this.extractedStartDate) + 
							"\n" +
							"Attitude: [{}, {}, {}, {}] \n" +
							"Spin    : {} \n" +
							"RotAcc  : {}",
							newState.getAttitude().getRotation().getQ0(),
							newState.getAttitude().getRotation().getQ1(),
							newState.getAttitude().getRotation().getQ2(),
							newState.getAttitude().getRotation().getQ3(),
							newState.getAttitude().getSpin().toString(),
							newState.getAttitude().getRotationAcceleration().toString()
					);

		} catch (OrekitException | IOException e) {
			e.printStackTrace();
		}
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
