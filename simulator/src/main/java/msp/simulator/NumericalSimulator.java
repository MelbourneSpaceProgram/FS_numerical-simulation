/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.LogManager;

import org.orekit.errors.OrekitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.VTSTools;
import msp.simulator.utils.architecture.OrekitConfiguration;
import msp.simulator.utils.logs.CustomLoggingTools;

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

			/* Building the Dynamic Module. */
			this.dynamic = new msp.simulator.dynamic.Dynamic(
					this.environment,
					this.satellite
					) ;

		} catch (OrekitException e) {
			e.printStackTrace();
		}


	}

	public void process() {
		if (this.executionStatus == 1) {
			NumericalSimulator.logger.info(CustomLoggingTools.indentMsg(logger,
					"Processing the Simulation..."));
		}
		
		VTSTools.generateAEMFile(
				this.satellite.getAssembly().getStates().getInitialState().getDate(),
				this.satellite.getAssembly().getStates().getInitialState().getDate()
				.shiftedBy(100), /* Second */
				this.dynamic.getPropagation().getPropagator(), 
				"test",
				this.satellite);	
		
//		VTSTools.generateOEMFile(
//				this.satellite.getAssembly().getStates().getInitialState().getDate(),
//				this.satellite.getAssembly().getStates().getInitialState().getDate().
//				shiftedBy(60*60*1), 
//				this.dynamic.getPropagation().getPropagator(), 
//				"test");


	}

	public void exit() {
		this.endDate = LocalDateTime.now();
		NumericalSimulator.logger.info(CustomLoggingTools.indentMsg(logger,
				"Simulation exits with execution status: "
						+ this.executionStatus 
						+ "\n"
						+ "\t Processing Time: " 
						+ this.startDate.until(this.endDate, ChronoUnit.SECONDS)
						+ "s."
						)
				);
	}

}
