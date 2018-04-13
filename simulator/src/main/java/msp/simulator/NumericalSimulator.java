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

package msp.simulator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.hipparchus.util.FastMath;
import org.orekit.errors.OrekitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.dynamic.Dynamic;
import msp.simulator.environment.Environment;
import msp.simulator.groundStation.GroundStation;
import msp.simulator.satellite.Satellite;
import msp.simulator.user.Dashboard;
import msp.simulator.utils.architecture.OrekitConfiguration;
import msp.simulator.utils.logs.CustomLoggingTools;
import msp.simulator.utils.logs.ephemeris.EphemerisGenerator;

/**
 * This class is responsible to create the instance of the
 * numerical simulator. 
 * 
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
 */
public class NumericalSimulator {

	/* ******* Public Static Attributes ******* */

	/** Time duration of the simulation. (Always numerically finite) 
	 * Default value is Long.MAX_VALUE. */
	public static long simulationDuration = Long.MAX_VALUE;

	/** Real-time processing flag. */
	public static boolean realTimeUserFlag = false;

	/** Double precision threshold to be considered to be zero in the simulation. 
	 * This enables to avoid failure due to any numerical approximation. */
	public static final double EPSILON = 1e-10;

	/* **************************************** */

	/** Logger of the instance. */
	private static final Logger logger = LoggerFactory.getLogger(NumericalSimulator.class);

	/** Private real-time processing flag. */
	private boolean realTimeProcessing;

	/* The different modules of the simulator. */
	/** Environment Instance in the Simulation. */
	private Environment environment;

	/** Satellite Instance of the Simulation. */
	private Satellite satellite;

	/** Dynamic Module of the Simulation. */
	private Dynamic dynamic;

	/** Ephemeris Generator Instance of the simulator. */
	private EphemerisGenerator ephemerisGenerator;

	/** Ground Station Instance of the simulator. */
	private GroundStation groundStation;

	/** Execution status of the simulation.
	 * TODO: Enumerate the execution status of the simulator.
	 * (but also normalize the exception handling)
	 */
	private int executionStatus;

	/** Computer date at simulation start. */
	private final LocalDateTime startDate;

	/** Computer date at simulation exit. */
	private LocalDateTime endDate;

	/** Constructor of an instance of numerical simulator. */
	public NumericalSimulator() {
		this.startDate = LocalDateTime.now();
		this.realTimeProcessing = NumericalSimulator.realTimeUserFlag;

		NumericalSimulator.logger.info("Simulation Instance Created.");
	}

	/**
	 * This method performs in order the initialization,
	 * the processing and the exit of the simulation.
	 * <p>
	 * NOTE: The configuration of the simulation should be
	 * set prior to the initialization or launch.
	 * @throws Exception when initialization fails.
	 * @see msp.simulator.user.Dashboard
	 */
	public void launch() throws Exception {
		this.initialize();
		this.process();
		this.exit();
	}

	/**
	 * Initialize the simulation.
	 * @throws Exception when the initialization of a module fails
	 */
	public void initialize() throws Exception {
		NumericalSimulator.logger.info(CustomLoggingTools.indentMsg(logger,
				"Initialization in Process..."));

		/* Checking the user configuration first. */
		Dashboard.checkConfiguration();

		/* Instance of the Simulator. */
		this.executionStatus = 1;

		/* Configure OreKit. */
		OrekitConfiguration.processConfiguration();

		try {
			/* Building the Environment Module. */
			this.environment = new msp.simulator.environment.Environment();

			/* Building the Satellite Module. */
			this.satellite = new msp.simulator.satellite.Satellite(
					this.environment
					);

			/* Building the Dynamic Module. */
			this.dynamic = new msp.simulator.dynamic.Dynamic(
					this.environment,
					this.satellite
					);

			/* Ground Station Module */
			this.groundStation = new GroundStation(
					this.environment,
					this.satellite
					);

			/* Ephemeris Generator Module */
			this.ephemerisGenerator = new EphemerisGenerator();
			this.ephemerisGenerator.start();


			/* ********* Initial State Processing before propagation. ********  */
			/* Writing initial step into the ephemeris. */ 
			this.ephemerisGenerator.writeStep(this.satellite);

			/* Sending the initial ground station data to the satellite. */
			this.groundStation.executeMission(
					this.satellite.getStates().getInitialState().getDate()
					);

			/* Pushing the initial state into the VTS socket. */
			if (this.satellite.getIO().isConnectedToVts()) {
				this.satellite.getIO().exportToVts(
						this.satellite.getStates().getInitialState()
						);
			}

		} catch (OrekitException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Execute the main processing of the simulation.
	 * @throws Exception 
	 */
	public void process() {
		if (this.executionStatus == 1) {
			NumericalSimulator.logger.info(CustomLoggingTools.indentMsg(logger,
					"Processing the Simulation..."));
		}

		/* Creating the main simulation loop task. */
		MainSimulationTask mainSimulationTask = new MainSimulationTask(
				this.environment,
				this.dynamic,
				this.satellite,
				this.groundStation,
				this.ephemerisGenerator
				);

		/* Wall clock processing. */
		if (!this.realTimeProcessing) {
			while(mainSimulationTask.isRunning()) {
				mainSimulationTask.run();
			}

			/* Real-time processing. */
		} else {

			final ScheduledExecutorService scheduler =
					Executors.newScheduledThreadPool(1);

			/* Launch the main task periodically. */
			final ScheduledFuture<?> realTimeLoopHandler =
					scheduler.scheduleAtFixedRate(
							mainSimulationTask, 
							0, 
							(long) (this.dynamic.getPropagation().getIntegrationManager()
									.getStepSize() * 1000), 
							TimeUnit.MILLISECONDS);


			/* Launch the cancellation task. */
			scheduler.schedule(
					new Runnable() {
						public void run() { 
							logger.info("Terminating the main simulation task.");
							realTimeLoopHandler.cancel(true); 

							logger.info("Shutting down the scheduler.");
							scheduler.shutdown();
						}
					}, 
					simulationDuration, 
					TimeUnit.SECONDS
					);


			/* Wait for the main processing task termination. */
			try {
				boolean execStatus =
						scheduler.awaitTermination(
								simulationDuration == Long.MAX_VALUE ? 
										Long.MAX_VALUE 
										: simulationDuration + 1 ,
										TimeUnit.SECONDS
								);

				if (!execStatus ) {
					logger.error("Main computation loop failed to terminate.");
					throw (new Exception("Main computation loop failed to terminate."));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/* End of processing. */
		logger.info(CustomLoggingTools.indentMsg(logger,
				"End of Processing Stage."));	
	}

	/**
	 * Performs the exit processing of the simulation.
	 */
	public void exit() {
		/* Properly closing the IO interfaces. */
		NumericalSimulator.logger.info(CustomLoggingTools.indentMsg(logger,
				"Shutting down the Satellite IO interfaces."));
		this.satellite.getIO().stop();

		/* End of execution statistics. */
		this.endDate = LocalDateTime.now();
		NumericalSimulator.logger.info(CustomLoggingTools.indentMsg(logger,
				"Simulation exits with execution status: "
						+ this.executionStatus 
						+ "\n"
						+ "\t Execution Time: " 
						+ this.startDate.until(this.endDate, ChronoUnit.SECONDS)
						+ "s."
				)
				);
	}

	/**
	 * Nested class of the simulator responsible to manage and run the primary 
	 * task of the main loop.
	 * <p>
	 * This class implements Runnable in order to allow a scheduled time 
	 * processing of each step for real-time services.
	 * <p>
	 * <i>Note that this implementation of JAVA does not insure strict real time
	 * accuracy.</i>
	 *
	 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
	 */
	private final class MainSimulationTask implements Runnable {

		/* Main simulation fields. */

		/** Environment module of the simulation. */
		@SuppressWarnings("unused")
		private Environment environment;

		/** Dynamic module of the simulation. */
		private Dynamic dynamic;

		/** Satellite module of the simulation. */
		private Satellite satellite;

		/** Ground Station Instance of the simulation. */
		private GroundStation groundStation;

		/** Ephemeris Generator module of the simulation. */
		private EphemerisGenerator ephemerisGenerator;

		/* Other fields needed by the task. */

		/** Integration time step of the simulation. */
		private double integrationTimeStep;

		/** Current offset in the main loop. */
		private double currentOffset;

		/** Time period of the ephemeris generation. */
		private int ephemerisPeriod;

		/** Counter of steps before the ephemeris generation. */
		private int ephemerisStepCounter;

		/**
		 * Create the main simulation loop task as a runnable object ready to
		 * be scheduled periodically.
		 * 
		 * @param environment Instance of the simulation.
		 * @param dynamic Instance of the simulation.
		 * @param satellite Instance of the simulation.
		 * @param ephemerisGenerator Instance of the simulation.
		 */
		public MainSimulationTask(
				Environment environment,
				Dynamic dynamic,
				Satellite satellite,
				GroundStation groundStation,
				EphemerisGenerator ephemerisGenerator) {

			this.environment = environment;
			this.dynamic = dynamic;
			this.satellite = satellite;
			this.groundStation = groundStation;
			this.ephemerisGenerator = ephemerisGenerator;

			this.integrationTimeStep = dynamic.getPropagation().getIntegrationManager().getStepSize();
			this.currentOffset = 0;

			this.ephemerisPeriod = (int) FastMath.round(
					EphemerisGenerator.ephemerisTimeStep  / this.integrationTimeStep
					);
			this.ephemerisStepCounter = 1;
		}

		/**
		 * Main processing loop of the simulator.
		 */
		public void run() {
			if (this.isRunning()) {

				/* Propagate the current state s(t) to s(t + dt) */
				this.dynamic.getPropagation().propagateStep();

				/* Incrementing the current offset.
				 * We are now at the new offset after the propagation. */
				currentOffset += integrationTimeStep;

				/* ******** GROUND STATION UPDATES ******** */

				this.groundStation.executeMission(
						this.satellite.getStates().getCurrentState().getDate()
						);

				/* **************************************** */


				/* *************** PAYLOAD **************** */

				/* Execute the mission of the satellite for the step. */
				this.satellite.executeStepMission();

				/* Export the satellite state to VTS for visualization. */
				if(this.satellite.getIO().isConnectedToVts()) {
					this.satellite.getIO().exportToVts(
							this.satellite.getStates().getCurrentState());
				}
				/* **************************************** */


				/* ********** Generate the Ephemeris ********** */
				/* Compute the ephemeris generation flag. */
				boolean renderEphemeris = 
						FastMath.floorMod(ephemerisStepCounter, ephemerisPeriod) < EPSILON 
						? true : false;

				/* Render the ephemeris step if required. */
				if (renderEphemeris) { 
					this.ephemerisGenerator.writeStep(this.satellite);
				}

				/* Increment the counter. */
				ephemerisStepCounter++;
				/* **************************************************************	*/

			} else {
				try {
					throw (new Exception());
				} catch (Exception e) {
				}
			}
		}

		/**
		 * Assert if the primary task of the simulation is running.
		 * @return True if running, false otherwise.
		 */
		public boolean isRunning() {
			/* Basically run until the end of the simulation duration. */
			boolean status = (currentOffset + EPSILON < simulationDuration);

			return status;
		}
	} /* End of nested class */

	/**
	 * @return The space environment of the simulation.
	 */
	public msp.simulator.environment.Environment getEnvironment() {
		return environment;
	}

	/**
	 * @return The satellite instance of the simulation.
	 */
	public msp.simulator.satellite.Satellite getSatellite() {
		return satellite;
	}

	/**
	 * @return The satellite IO manager of the simulation.
	 */
	public msp.simulator.satellite.io.IO getIo() {
		return satellite.getIO();
	}

	/**
	 * @return The dynamic engine of the simulation.
	 */
	public msp.simulator.dynamic.Dynamic getDynamic() {
		return dynamic;
	}

}
