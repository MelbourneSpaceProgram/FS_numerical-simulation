/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.orekit.attitudes.Attitude;
import org.orekit.errors.OrekitException;
import org.orekit.frames.FramesFactory;
import org.orekit.utils.AngularCoordinates;

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
	private Logger logger;

	/* The different modules of the simulator. */
	/** Environment Instance in the Simulation. */
	private msp.simulator.environment.Environment environment;

	/** Satellite Instance of the Simulation. */
	private msp.simulator.satellite.Satellite satellite;

	/** Dynamic Module of the Simulation. */
	@SuppressWarnings("unused")
	private msp.simulator.dynamic.Dynamic dynamic;

	/* TODO: Enumerate the execution status. */
	private int executionStatus;

	@SuppressWarnings("unused")
	private final LocalDateTime startDate;
	@SuppressWarnings("unused")
	private LocalDateTime endDate;

	public NumericalSimulator() {
		this.startDate = LocalDateTime.now();

		LogManager myLogManager = LogManager.getLogManager();

		System.setProperty(
				"java.util.logging.config.file", 
				"src/main/resources/config/log-config-file.txt");

		try {
			myLogManager.readConfiguration();

		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

		this.logger = LoggerFactory.getLogger(this.getClass());
		this.logger.info("Launching the Simulation...");
	}

	public void launch() {
		this.initialize();
		this.process();
		this.exit();
	}

	public void initialize() {
		this.logger.info(CustomLoggingTools.indentMsg(this.logger,
				"Initialization in Process..."));

		/* Instance of the Simulator. */
		this.executionStatus = 1;

		/* Configure OreKit. */
		OrekitConfiguration.processConfiguration();

		try {
			/* Building the environment. */
			this.environment = new msp.simulator.environment.Environment();

			/* Building the satellite. */
			this.satellite = new msp.simulator.satellite.Satellite(
					this.environment,
					new Attitude (
							this.environment.getOrbit().getDate(),
							FramesFactory.getEME2000(),
							new AngularCoordinates()
							)
					);

			/* Building the Dynamic Module. */
			this.dynamic = new msp.simulator.dynamic.Dynamic(
					this.satellite,
					this.environment
					) ;

		} catch (OrekitException e) {
			e.printStackTrace();
		}


	}

	public void process() {
		if (this.executionStatus == 1) {
			this.logger.info(CustomLoggingTools.indentMsg(this.logger,
					"Processing the Simulation..."));
		}



	}

	public void exit() {
		this.endDate = LocalDateTime.now();
		this.logger.info(CustomLoggingTools.indentMsg(this.logger,
				"Simulation exits with execution status: "
						+ this.executionStatus)
				);
	}

}
