/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.utils.architecture;

import java.io.File;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.errors.OrekitException;

import msp.simulator.utils.logs.LogWriter;

/**
 * This class provides methods to configure OreKit.
 * It especially provide external data samplings for the good computation
 * of some models like Atmosphere, CelestialBodies etc...
 * 
 * @author Florian CHAUBEYRE
 */
public final class OrekitConfiguration {

	/**
	 * The directory location of the external data.
	 */
	private static final File orekitDataDir = 
			new File("src/main/resources/orekit-data/");
    
	/** Private constructor.
     * <p>This class is a utility class, it should neither have a public
     * nor a default constructor. This private constructor prevents
     * the compiler from generating one automatically.</p>
     */
	private OrekitConfiguration() {}
	
	/**
	 * Process the configuration of Orekit:<p>
	 * - Set the Data Directory.
	 * @param logWriter Print a trace of the configuration in the log file.
	 */
	public static void processConfiguration(LogWriter logWriter) {
		DataProvidersManager dataManager = DataProvidersManager.getInstance();
		try {
			logWriter.printMsg("Set Orekit External Data directory to:\n\t"
					+ OrekitConfiguration.orekitDataDir.getAbsolutePath(),
					new OrekitConfiguration()
					);
			dataManager.addProvider(new DirectoryCrawler(OrekitConfiguration.orekitDataDir));

		} catch (OrekitException e) {
			e.printStackTrace();
		}
	}

}
