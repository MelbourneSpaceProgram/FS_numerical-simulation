/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.torques;

import java.nio.ByteBuffer;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.time.AbsoluteDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.dynamic.propagation.integration.Integration;
import msp.simulator.satellite.Satellite;
import msp.simulator.satellite.assembly.SatelliteStates;
import msp.simulator.satellite.io.IO;
import msp.simulator.satellite.io.MemcachedRawTranscoder;
import msp.simulator.utils.logs.CustomLoggingTools;
import net.spy.memcached.MemcachedClient;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class MemCachedTorqueProvider implements TorqueProvider {

	/* ******* Public Static Attributes ******* */

	/** Public key to access the MemCached hash table. */
	public static String torqueCommandKey = "Simulation_Torque_";

	/* **************************************** */
	
	/** Private Key to store the public key. */
	private String torqueKey;

	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(
			MemCachedTorqueProvider.class);

	/** Instance of the MemCached Client of the simulation. */
	private MemcachedClient memcached;

	/** Instance of the "raw data" transcoder of the IO. */
	private MemcachedRawTranscoder memcachedTranscoder;

	/** Buffered date of the beginning of the step. */
	private AbsoluteDate stepStart;

	/** Satellite states keeping the start date of the step all 
	 * along the step. */
	private SatelliteStates satState;
	
	
	/** Buffered date of the next acquisition date. */
	private AbsoluteDate nextAcquisitionDate;

	/** Buffered torque for the current step. */
	private Vector3D stepTorque;

	/** Copy of the fixed integration time step. */
	private final double stepSize = Integration.integrationTimeStep;

	/**
	 * Create the instance of memcached torque provider.
	 * Note that the MemCached connection should be enable
	 * in the satellite IO prior to this constructor.
	 * @param satellite Instance of the simulation
	 */
	public MemCachedTorqueProvider(Satellite satellite) {
		if (IO.connectMemCached) {
			logger.info(CustomLoggingTools.indentMsg(logger,
					"Connecting to the MemCached Torque Provider..."));

			/* The beginning date of the step is actually given by the state of
			 * the satellite during the propagation. */
			this.satState = satellite.getStates();
			this.stepStart = this.satState.getCurrentState().getDate();
			this.nextAcquisitionDate = this.satState.getInitialState().getDate();
			this.stepTorque = Vector3D.ZERO;

			this.torqueKey = MemCachedTorqueProvider.torqueCommandKey;
			this.memcached = satellite.getIO().getMemcached();
			this.memcachedTranscoder = satellite.getIO().getRawTranscoder();

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
		/* Flag to enable the acquisition of the torque for the step. */
		boolean acquisition;
		
		/* As the torque is considered constant over a step, we only need 
		 * to acquire the torque once at the very beginning of the step. */
		this.stepStart = this.satState.getCurrentState().getDate();
		
		acquisition = 
				(date.compareTo(this.nextAcquisitionDate) == 0)
				&&
				(date.compareTo(this.stepStart) == 0)
				;
		
		/* Retrieve the torque command if a new step is detected. */
		if (acquisition) {

			/* Reading the torque command from MemCached. */
			try {
				Vector3D torqueCommand;

				double torque_x = ByteBuffer.wrap(
						this.memcached.get(
								this.torqueKey + "X",
								this.memcachedTranscoder))
						.getDouble();

				double torque_y = ByteBuffer.wrap(
						this.memcached.get(
								this.torqueKey + "Y",
								this.memcachedTranscoder))
						.getDouble();

				double torque_z = ByteBuffer.wrap(
						this.memcached.get(
								this.torqueKey + "Z",
								this.memcachedTranscoder))
						.getDouble();

				torqueCommand = new Vector3D(
						torque_x,
						torque_y,
						torque_z
						);

				/* Checking the data transmission. */
				if (torqueCommand.isNaN() || torqueCommand.isInfinite()) {
					throw new Exception("Torque acquisition: MemCached transmission failed.");
				} 

				/* Then update the buffered data. */
				this.nextAcquisitionDate = this.stepStart.shiftedBy(this.stepSize);
				this.stepTorque = torqueCommand;
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			/* Debug Information */
			logger.debug("Torque Provider (Acquisition): " + date.toString() +" - " +
					this.stepTorque.toString());

		} else {
			/* Else the torque is already computed for the current step. */
			logger.debug("------------- Torque Provider: " + date.toString() +" - " +
					this.stepTorque.toString());
		}
		
		/* Finally returns the torque of the step (updated if needed). */
		return this.stepTorque;
	}

}
