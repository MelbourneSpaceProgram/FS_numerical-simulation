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

package msp.simulator.utils.architecture;

import java.io.File;

import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.errors.OrekitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class provides methods to configure OreKit.
 * It especially provide external data samplings for the good computation
 * of some models like Atmosphere, CelestialBodies etc...
 * 
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public final class OrekitConfiguration {

	/** The Logger of the instance. */
	private static final Logger logger = LoggerFactory.getLogger(OrekitConfiguration.class);
	
	/**
	 * The directory location of the external data.
	 */
	private static final File orekitDataDir = 
			new File("src/main/resources/orekit-data/");
	
	/** Version of the GeoLagnetic Field data: WMM2015. */
	public static final int GeoMagneticDataYear = 2015;
    
	/** Private constructor.
     * <p>This class is a utility class, it should neither have a public
     * nor a default constructor. This private constructor prevents
     * the compiler from generating one automatically.</p>
     */
	private OrekitConfiguration() {}
	
	/**
	 * Process the configuration of Orekit:<p>
	 * - Set the Data Directory.
	 */
	public static void processConfiguration() {
		DataProvidersManager dataManager = DataProvidersManager.getInstance();
		try {
			OrekitConfiguration.logger.info(CustomLoggingTools.indentMsg(logger,
					"Setting Orekit External Data directory to:\n\t"
					+ OrekitConfiguration.orekitDataDir.getAbsolutePath()
					));
			dataManager.addProvider(new DirectoryCrawler(OrekitConfiguration.orekitDataDir));

		} catch (OrekitException e) {
			e.printStackTrace();
		}
	}

}
