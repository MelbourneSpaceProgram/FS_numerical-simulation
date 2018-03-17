/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;

import org.hipparchus.util.FastMath;
import org.orekit.errors.OrekitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.dynamic.Dynamic;
import msp.simulator.satellite.Satellite;
import msp.simulator.user.Dashboard;
import msp.simulator.utils.architecture.OrekitConfiguration;
import msp.simulator.utils.logs.CustomLoggingTools;
import msp.simulator.utils.logs.ephemeris.EphemerisGenerator;

/**
 * This class is responsible to create the instance of the
 * numerical simulator. 
 * 
 * @author Florian CHAUBEYRE
 */
public class NumericalSimulator {

	/* ******* Public Static Attributes ******* */

	/** Precision threshold to be considered to be zero. */
	public static final double EPSILON = 1e-13;

	/* **************************************** */

	/** Logger of the instance. */
	private static final Logger logger = LoggerFactory.getLogger(NumericalSimulator.class);

	/* The different modules of the simulator. */
	/** Environment Instance in the Simulation. */
	private msp.simulator.environment.Environment environment;

	/** Satellite Instance of the Simulation. */
	private msp.simulator.satellite.Satellite satellite;

	/** Dynamic Module of the Simulation. */
	private msp.simulator.dynamic.Dynamic dynamic;

	/** Ephemeris Generator Instance of the simulator. */
	private msp.simulator.utils.logs.ephemeris.EphemerisGenerator ephemerisGenerator;

	/* TODO: Enumerate the execution status. */
	private int executionStatus;

	private final LocalDateTime startDate;
	private LocalDateTime endDate;

	/** 
	 * Time duration of the simulation : the double max value
	 * states for an "infinite loop" duration.
	 */
	public static double simulationDuration = Double.MAX_VALUE;

	public NumericalSimulator() {
		this.startDate = LocalDateTime.now();

		/* Setting the configuration of the Logging Services. */
		LogManager myLogManager = LogManager.getLogManager();

		/* Setting the configuration file location. */
		System.setProperty(
				"java.util.logging.config.file", 
				System.getProperty("user.dir") + System.getProperty("file.separator") 
				+ "src" + System.getProperty("file.separator")
				+ "main" + System.getProperty("file.separator")
				+ "resources" + System.getProperty("file.separator")
				+ "config" + System.getProperty("file.separator")
				+ "log-config-file.txt"
				);

		/* Creating the log directory. */
		File simuLogDir = new File(
				System.getProperty("user.dir") + System.getProperty("file.separator") 
				+ "src" + System.getProperty("file.separator")
				+ "main" + System.getProperty("file.separator")
				+ "resources" + System.getProperty("file.separator")
				+ "logs" + System.getProperty("file.separator")
				);
		if (!simuLogDir.exists()) {
			simuLogDir.mkdirs();
		}

		/* Reading the overall configuration for the logging services. */
		try {
			myLogManager.readConfiguration();
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

		NumericalSimulator.logger.info("Simulation Instance Created.");
	}

	/**
	 * This method performs in order the initialization,
	 * the processing and the exit of the simulation.
	 * <p>
	 * NOTE: Any user defined settings for the simulation
	 * should be registered prior the initialization/launch.
	 * @throws Exception when initialization fails.
	 * @see msp.simulator.user.Dashboard
	 * @deprecated 
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

			/* Ephemeris Generator Module */
			this.ephemerisGenerator = new EphemerisGenerator();
			this.ephemerisGenerator.start();

		} catch (OrekitException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Launch the main processing of the simulation.
	 */
	public void process() {
		if (this.executionStatus == 1) {
			NumericalSimulator.logger.info(CustomLoggingTools.indentMsg(logger,
					"Processing the Simulation..."));
		}

		/* Get the integration time step of the simulation. */
		double integrationTimeStep = this.getDynamic().getPropagation().getIntegrationManager()
				.getStepSize();

		/* Set the current offset of the main loop. */
		double currentOffset = 0 + integrationTimeStep;

		/* Flag to render the ephemeris services. */
		boolean renderEphemeris = false;

		/* Time step period of ephemeris generation. */
		final int kEphemeris = (int) FastMath.round(
				EphemerisGenerator.ephemerisTimeStep  
				/ integrationTimeStep );

		/* Counter of steps before the ephemeris generation. */
		int countEphemeris = 1;

		final class RealTimeLoop implements Runnable {

			double integrationTimeStep;
			double currentOffset;
			boolean renderEphemeris;
			EphemerisGenerator ephemerisGenerator;
			int kEphemeris;
			int countEphemeris;
			Dynamic dynamic;
			Satellite satellite;

			public RealTimeLoop(
					double integrationTimeStep, 
					double currentOffset, 
					boolean renderEphemeris,
					EphemerisGenerator ephemerisGenerator, 
					int kEphemeris, 
					int countEphemeris,
					Dynamic dynamic,
					Satellite satellite) {
				super();
				this.integrationTimeStep = integrationTimeStep;
				this.currentOffset = currentOffset;
				this.renderEphemeris = renderEphemeris;
				this.ephemerisGenerator = ephemerisGenerator;
				this.kEphemeris = kEphemeris;
				this.countEphemeris = countEphemeris;
				this.dynamic = dynamic;
				this.satellite = satellite;
			}

			public void run() {
				if (
						(simulationDuration == Double.MAX_VALUE)
						||
						(currentOffset <= simulationDuration)
						) 
				{
					//System.out.println("Trigger: " + System.currentTimeMillis());
					
					/* Propagate the current state s(t) to s(t + dt) */
					this.dynamic.getPropagation().propagateStep();

					/* Incrementing the time step: we are at the new offset now. */
					currentOffset += integrationTimeStep;

					/* ********** Generate the Ephemeris ********** */

					/* Update the flag. */
					renderEphemeris = 
							FastMath.floorMod(countEphemeris, kEphemeris) < EPSILON 
							? true : false;
					
					/* Render the epehemeris step if required. */
					if (renderEphemeris) { 
						this.ephemerisGenerator.writeStep(
								this.satellite.getStates().getCurrentState()
								);
					}

					/* Increment the counter. */
					countEphemeris++;
					/* **************************************************************	*/
				}
			}

		} /* End of class */

		
		/* Create and schedule the task. */
		RealTimeLoop realTimeLoop = new RealTimeLoop(
				integrationTimeStep, 
				currentOffset, 
				renderEphemeris,
				this.ephemerisGenerator, 
				kEphemeris, 
				countEphemeris,
				this.dynamic,
				this.satellite
				);
		
		final ScheduledExecutorService scheduler =
			     Executors.newScheduledThreadPool(1);
		
		@SuppressWarnings("unused")
		final ScheduledFuture<?> realTimeLoopHandler =
			       scheduler.scheduleAtFixedRate(
			    		   realTimeLoop, 
			    		   0, 
			    		   100, 
			    		   TimeUnit.MILLISECONDS);
		
		
		
		
		
//		/* Main processing loop. */
//		while ((simulationDuration == Double.MAX_VALUE)
//				||
//				(currentOffset + integrationTimeStep - simulationDuration < EPSILON)
//				) {
//
//			/* Propagate the current state s(t) to s(t + dt) */
//			this.dynamic.getPropagation().propagateStep();
//
//			/* Incrementing the time step: we are at the new offset now. */
//			currentOffset += integrationTimeStep;
//
//			/* ********** Generate the Ephemeris ********** */
//
//			/* Update the flag. */
//			renderEphemeris = 
//					FastMath.floorMod(countEphemeris, kEphemeris) < EPSILON 
//					? true : false;
//
//			/* Render the epehemeris step if required. */
//			if (renderEphemeris) { 
//				this.ephemerisGenerator.writeStep(
//						this.satellite.getStates().getCurrentState()
//						);
//			}
//
//			/* Increment the counter. */
//			countEphemeris++;
//			/* **************************************************************	*/
//
//		}

		/* End of processing. */
		logger.info(CustomLoggingTools.indentMsg(logger,
				"End of Processing Stage."));
	}

	/**
	 * Performs the exit processing of the simulation.
	 */
	public void exit() {
		
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	 * @return the satellite
	 */
	public msp.simulator.satellite.Satellite getSatellite() {
		return satellite;
	}

	/**
	 * Return the Satellite IO Manager.
	 * @return IO instance of the satellite.
	 */
	public msp.simulator.satellite.io.IO getIo() {
		return satellite.getIO();
	}

	/**
	 * @return the environment
	 */
	public msp.simulator.environment.Environment getEnvironment() {
		return environment;
	}

	/**
	 * @return the dynamic
	 */
	public msp.simulator.dynamic.Dynamic getDynamic() {
		return dynamic;
	}

}
