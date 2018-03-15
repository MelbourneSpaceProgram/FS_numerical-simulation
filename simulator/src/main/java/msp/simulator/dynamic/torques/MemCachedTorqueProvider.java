/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.torques;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.errors.OrekitException;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.dynamic.propagation.Propagation;
import msp.simulator.satellite.Satellite;
import msp.simulator.satellite.io.IO;
import msp.simulator.utils.logs.CustomLoggingTools;
import net.spy.memcached.MemcachedClient;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class MemCachedTorqueProvider implements TorqueProvider {

	/* ******* Public Static Attributes ******* */

	/** Public key to access the MemCached hash table. */
	public static String torqueCommandKey = "torque";

	/* **************************************** */

	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(
			MemCachedTorqueProvider.class);

	/** Instance of the MemCached Client of the simulation. */
	private MemcachedClient memcached;

	/** Buffer date to avoid several processing and several torque
	 * command value at the same date in the simulation. Indeed the
	 * command is considered constant over a single propagation step.
	 */
	private AbsoluteDate nextComputationDate;

	/** The last stored torque command is return in case the last date 
	 * is already processed.
	 */
	private Vector3D lastTorque;

	/** Private Key to store the public key. */
	private String torqueKey;
	
	/** Copy of the fixed integration time step. */
	private final double integrationTimeStep = Propagation.integrationTimeStep;

	/**
	 * Create the instance of memcached torque provider.
	 * Note that the MemCached connection should be enable
	 * in the satellite IO prior to this constructor.
	 */
	public MemCachedTorqueProvider(Satellite satellite) {
		if (IO.connectMemCached) {
			logger.info(CustomLoggingTools.indentMsg(logger,
					"Connecting to the MemCached Torque Provider..."));

			this.nextComputationDate = satellite.getStates().getInitialState().getDate();
			this.lastTorque = Vector3D.ZERO;

			this.torqueKey = MemCachedTorqueProvider.torqueCommandKey;
			this.memcached = satellite.getIO().getMemcached();

		} else {
			logger.error(CustomLoggingTools.indentMsg(logger,
					"Memcached connection is not enable!"));
		}
	}
	
	/**
	 * Retrieve the torque command from the MemCached common memory
	 * hash table. This command is then ideally set by the real flight
	 * software controller.
	 * <p>
	 * Note that the value of the torque command is set as a constant 
	 * along a single step (included the intermediary steps of the
	 * integration).
	 */
	@Override
	public Vector3D getTorque(AbsoluteDate date) {
		/* Comparing strings avoids numerical approximation of the dates
		 * and a potential false comparison. */
		if (date.toString().equals(this.nextComputationDate.toString())) {
			
			/* Reading the torque command from MemCached. */
			Vector3D torqueCommand = new Vector3D(
					(double[]) this.memcached.get(this.torqueKey)
					);

			/* Updating the buffer data. */
			try {
				this.nextComputationDate = new AbsoluteDate(
						date.shiftedBy(this.integrationTimeStep).toString(),
						TimeScalesFactory.getUTC()
						);
			} catch (OrekitException e) {
				e.printStackTrace();
			}
			
			this.lastTorque = torqueCommand;

			/* Debug */
			logger.debug("Torque Acquisition: " + date.toString() +" - " +
					torqueCommand.toString());

			return torqueCommand;
			
		} else {
			logger.debug("--- Torque Provider: " + date.toString() +" - " +
					this.lastTorque.toString());
			
			return this.lastTorque;
		}
	}

}
