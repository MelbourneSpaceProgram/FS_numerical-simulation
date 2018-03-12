/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.torques;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.time.AbsoluteDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.satellite.Satellite;
import msp.simulator.satellite.io.IO;
import msp.simulator.utils.logs.CustomLoggingTools;
import net.spy.memcached.MemcachedClient;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class MemCachedTorqueProvider implements TorqueProvider {

	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(
			MemCachedTorqueProvider.class);

	private MemcachedClient memcached;


	/**
	 * 
	 */
	public MemCachedTorqueProvider(Satellite satellite) {
		if (IO.connectMemCached) {
			logger.info(CustomLoggingTools.indentMsg(logger,
					"Connecting to the MemCached Torque Provider..."));
			this.memcached = satellite.getIO().getMemcached();
		} else {
			logger.error(CustomLoggingTools.indentMsg(logger,
					"Memcached connection is not enable!"));
		}
	}
	
	private double i = 0 ;

	/**
	 * Retrieve the torque command from the MemCached common memory
	 * hash table. This command is then ideally set by the real flight
	 * software controller. 
	 */
	@Override
	public Vector3D getTorque(AbsoluteDate date) {
		String torqueCommandKey = "torque";
		
		/* TODO Test Purpose - To remove. */
		this.memcached.set(
				torqueCommandKey, 
				0, 
				new double[] {(i++ / 100.), 0, 0}
				);
		
		
		/* Reading the torque command into memcached. */
		Vector3D torqueCommand = new Vector3D(
				(double[]) this.memcached.get(torqueCommandKey)
				);

		System.out.println("------ Date: " + date.toString() +"\n" +
				torqueCommand.toString());

		return torqueCommand;
	}

}
