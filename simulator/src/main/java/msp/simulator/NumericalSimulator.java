/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.orekit.errors.OrekitException;

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
	@SuppressWarnings("unused")
	private msp.simulator.environment.Environment environment;

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
		
		/* Building the environment. */
		try {
			this.environment = new msp.simulator.environment.Environment();
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
