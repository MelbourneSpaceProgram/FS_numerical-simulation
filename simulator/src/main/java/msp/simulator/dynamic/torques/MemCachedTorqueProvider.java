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
import msp.simulator.satellite.sensors.Magnetometer;
/**
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class MemCachedTorqueProvider implements TorqueProvider {

	/* ******* Public Static Attributes ******* */

	/** Public key to access the MemCached hash table. */
	public static String torqueCommandKey = "Simulation_Torque_";
	public static String pwmCommandKey = "Satellite_PWM_";
	/* **************************************** */
	
	/** Private Key to store the public key. */
	private String torqueKey;
	private String pwmKey; 
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
	private static final double stepSize = Integration.integrationTimeStep;
	
	/* Copy of the magnetic field*/
	private Vector3D b_field; 
	/* Copy of the sensor object*/ 
	private Magnetometer magnetometer; 
	
	private final double[] magnetorquerMaxDipole = {0.2,0.2,0.05};
	
	private Vector3D Pwm2Torque(Vector3D pwm) {
		double x = pwm.getX() * magnetorquerMaxDipole[0];
		double y = pwm.getY() * magnetorquerMaxDipole[1]; 
		double z = pwm.getZ() * magnetorquerMaxDipole[2]; 
		Vector3D dipole = new Vector3D(x,y,z);
		Vector3D torque; 
		torque = Vector3D.crossProduct(dipole,this.b_field);
		return torque;
	}

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
			this.b_field = satellite.getSensors().getMagnetometer().retrievePerfectField().getFieldVector();
			this.magnetometer = satellite.getSensors().getMagnetometer(); 
			this.torqueKey = MemCachedTorqueProvider.torqueCommandKey;
			this.pwmKey = MemCachedTorqueProvider.pwmCommandKey; 
			
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
		this.b_field = magnetometer.retrievePerfectField().getFieldVector().scalarMultiply(1E-9); // gets magnetic field (nT) then converts to SI 
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
				Vector3D pwmCommand;

				double pwm_x = ByteBuffer.wrap(
						this.memcached.get(
								this.pwmKey + "X",
								this.memcachedTranscoder))
						.getDouble();

				double pwm_y = ByteBuffer.wrap(
						this.memcached.get(
								this.pwmKey + "Y",
								this.memcachedTranscoder))
						.getDouble();

				double pwm_z = ByteBuffer.wrap(
						this.memcached.get(
								this.pwmKey + "Z",
								this.memcachedTranscoder))
						.getDouble();

				pwmCommand = new Vector3D(
						pwm_x,
						pwm_y,
						pwm_z
						);
				Vector3D torqueCommand = this.Pwm2Torque(pwmCommand);
				
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
