/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.geometry.euclidean.threed.Rotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.orekit.frames.Transform;

import msp.simulator.environment.Environment;
import msp.simulator.satellite.assembly.Assembly;
import msp.simulator.satellite.assembly.SatelliteStates;
import msp.simulator.satellite.io.IO;
import msp.simulator.satellite.io.MemcachedRawTranscoder;
import msp.simulator.satellite.sensors.Sensors;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class Satellite {

	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(Satellite.class);

	/** Instance of Assembly of the Satellite. */
	private Assembly assembly;

	/** Instance of the Sensors of the satellite. */
	private Sensors sensors;

	/** Instance of the IO Manager of the satellite. */
	private IO io;

	/**
	 * Build the intance of the Satellite in the simulation and connect
	 * the required IO.
	 * @param environment Instance of the Simulation
	 */
	public Satellite(Environment environment) {
		Satellite.logger.info(CustomLoggingTools.indentMsg(Satellite.logger,
				"Building the Satellite..."));

		/* Building the Assembly of the Satellite. */
		this.assembly = new Assembly(environment);

		/* Building the sensors. */
		this.sensors = new Sensors(environment, assembly);

		/* Build the IO Manager. */
		this.io = new IO();
		Satellite.logger.info(CustomLoggingTools.indentMsg(Satellite.logger,
				"  -> Connecting to the IO modules..."));
		this.io.start();

	}

	/**
	 * Process the mission of the satellite for the current step.
	 * It includes all of the payload processing but also the
	 * externalization of the sensors etc.
	 */
	public void executeStepMission() {

		/* Sensor Measurement */
		if (this.io.isConnectedToMemCached()) {

			/* Magnetometer Measurement of the Geomagnetic field vector. */
			/* This import is done once to avoid multiple noise computation.
			 * But it actually does not matter.*/
			Vector3D mmtMeasuredData = this.getSensors().getMagnetometer().getData_magField();
			
			/* Note that the double types are converted into an array of bytes
			 * before being send to the Memcached common memory to avoid both 
			 * serialization and deserialization issues. */
			byte[] rawMag_x = MemcachedRawTranscoder.toRawByteArray(mmtMeasuredData.getX());
			byte[] rawMag_y = MemcachedRawTranscoder.toRawByteArray(mmtMeasuredData.getY());
			byte[] rawMag_z = MemcachedRawTranscoder.toRawByteArray(mmtMeasuredData.getZ());
			
			this.io.getMemcached().set(
					"Simulation_Magnetometer_X", 0, 
					rawMag_x,
					this.io.getRawTranscoder()
					);
			this.io.getMemcached().set(
					"Simulation_Magnetometer_Y", 0, 
					rawMag_y,
					this.io.getRawTranscoder()
					);
			this.io.getMemcached().set(
					"Simulation_Magnetometer_Z", 0, 
					rawMag_z,
					this.io.getRawTranscoder()
					);			


      /* Infrared Sensor Measurement */
      Vector3D nadir_ecef = Vector3D.MINUS_K;
      // This might be backwards
      Rotation rotation_ecef_to_body =
          this.assembly.getStates()
          .getCurrentState().getAttitude().getRotation();
      Transform ecef_to_body = new Transform(null, rotation_ecef_to_body);
      Vector3D nadir_body = ecef_to_body.transformVector(nadir_ecef);

      double posXIR = this.sensors.getPosXIRSensor()
          .calculateInfraredReading(nadir_body);
			byte[] rawIR_xPos = MemcachedRawTranscoder.toRawByteArray(posXIR);
			this.io.getMemcached().set(
          "Simulation_IR_X_Pos", 0,
          rawIR_xPos,
          this.io.getRawTranscoder()
          );

      double negXIR = this.sensors.getNegXIRSensor()
          .calculateInfraredReading(nadir_body);
			byte[] rawIR_xNeg = MemcachedRawTranscoder.toRawByteArray(negXIR);
			this.io.getMemcached().set(
          "Simulation_IR_X_Neg", 0,
          rawIR_xNeg,
          this.io.getRawTranscoder()
          );

      double posYIR = this.sensors.getPosYIRSensor()
          .calculateInfraredReading(nadir_body);
			byte[] rawIR_yPos = MemcachedRawTranscoder.toRawByteArray(posYIR);
			this.io.getMemcached().set(
          "Simulation_IR_Y_Pos", 0,
          rawIR_yPos,
          this.io.getRawTranscoder()
          );

      double negYIR = this.sensors.getNegYIRSensor()
          .calculateInfraredReading(nadir_body);
			byte[] rawIR_yNeg = MemcachedRawTranscoder.toRawByteArray(negYIR);
			this.io.getMemcached().set(
          "Simulation_IR_Y_Neg", 0,
          rawIR_yNeg,
          this.io.getRawTranscoder()
          );

      double posZIR = this.sensors.getPosZIRSensor()
          .calculateInfraredReading(nadir_body);
			byte[] rawIR_zPos = MemcachedRawTranscoder.toRawByteArray(posZIR);
			this.io.getMemcached().set(
          "Simulation_IR_Z_Pos", 0,
          rawIR_zPos,
          this.io.getRawTranscoder()
          );

      double negZIR = this.sensors.getNegZIRSensor()
          .calculateInfraredReading(nadir_body);
			byte[] rawIR_zNeg = MemcachedRawTranscoder.toRawByteArray(negZIR);
			this.io.getMemcached().set(
          "Simulation_IR_Z_Neg", 0,
          rawIR_zNeg,
          this.io.getRawTranscoder()
          );
		}
		
	}




	/**
	 * Return the assembly of the satellite.
	 * @return Assembly
	 * @see msp.simulator.satellite.assembly.Assembly
	 */
	public Assembly getAssembly() {
		return this.assembly;
	}

	/**
	 * Return the States object of the satellite.
	 * @return SpacecraftState
	 * @see msp.simulator.satellite.assembly.SatelliteStates
	 */
	public SatelliteStates getStates() {
		return this.getAssembly().getStates();
	}

	/**
	 * Return the satellite sensors.
	 * @return Sensors
	 * @see msp.simulator.satellite.sensors.Sensors
	 */
	public Sensors getSensors() {
		return this.sensors;
	}

	/**
	 * Return the satellite IO manager.
	 * @return IO Instance of the satellite.
	 */
	public IO getIO() {
		return this.io;
	}

}
