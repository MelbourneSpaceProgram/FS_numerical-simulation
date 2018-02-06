/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.LogManager;

import org.orekit.errors.OrekitException;
import org.orekit.time.AbsoluteDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public NumericalSimulator() {
		this.startDate = LocalDateTime.now();

		/* Setting the configuration of the Logging Services. */
		LogManager myLogManager = LogManager.getLogManager();
		System.setProperty(
				"java.util.logging.config.file", 
				"src/main/resources/config/log-config-file.txt");
		try {
			myLogManager.readConfiguration();
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

		NumericalSimulator.logger.info("Launching the Simulation...");
	}

	public void launch() {
		this.initialize();
		this.process();
		this.exit();
	}

	public void initialize() {
		NumericalSimulator.logger.info(CustomLoggingTools.indentMsg(logger,
				"Initialization in Process..."));

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

			/* Configure here a new initial state of the satellite
			 * if needed.
			 */
			// Empty
			/* **************************** */

			/* Building the Dynamic Module. */
			this.dynamic = new msp.simulator.dynamic.Dynamic(
					this.environment,
					this.satellite
					) ;

			/* Ephemeris Generator Module */
			this.ephemerisGenerator = new EphemerisGenerator();
			this.ephemerisGenerator.start();

		} catch (OrekitException e) {
			e.printStackTrace();
		}


	}

	public void process() {
		if (this.executionStatus == 1) {
			NumericalSimulator.logger.info(CustomLoggingTools.indentMsg(logger,
					"Processing the Simulation..."));
		}
		double duration = 60*60*1 ; /* s */
		double currentOffset = 0;
		AbsoluteDate startDate = 
				this.satellite.getAssembly().getStates().getInitialState().getDate();

		while (currentOffset <= duration ) {
			
			//System.out.println("Summary at t = " + currentOffset + "--------");
			
			this.dynamic.getPropagation().propagate(startDate.shiftedBy(currentOffset));

			/* Generate the related ephemeris line. */
			this.ephemerisGenerator.writeStep(
					this.satellite.getAssembly().getStates().getCurrentState()
					);

			/* Incrementing the ephemeris time step. */
			currentOffset = currentOffset + 1. ;
			//System.out.println("---------------------------------");
		}

		/* End of processing. */
		logger.info(CustomLoggingTools.indentMsg(logger,
				"Processing End."));
	}

	public void exit() {
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

}
