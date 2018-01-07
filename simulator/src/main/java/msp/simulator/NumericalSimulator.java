/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator;

import java.time.LocalDateTime;

import org.orekit.errors.OrekitException;

import msp.simulator.utils.architecture.OrekitConfiguration;
import msp.simulator.utils.logs.LogWriter;

/**
 * This class is responsible to create the instance of the
 * numerical simulator. 
 * 
 * @author Florian CHAUBEYRE
 */
public class NumericalSimulator {
	
	/* The different modules of the simulator. */
	@SuppressWarnings("unused")
	private msp.simulator.environment.Environment environment;

	/* TODO: Enumerate the execution status. */
	private int executionStatus;
	private LogWriter logWriter;
	
	private final LocalDateTime startDate;
	@SuppressWarnings("unused")
	private LocalDateTime endDate;
	
	public NumericalSimulator() {
		this.startDate = LocalDateTime.now();
	}
	
	public void launch() {
		
		this.initialize();
		this.process();
		
		this.exit();
	}
	
	public void initialize() {
		/* Instance of the Simulator. */
		this.executionStatus = 1;
		
		/* Instance of the LogWriter. */
		String logFilePath = "src/main/resources/logs/log-" + this.startDate.toString() +".txt";
		this.logWriter = new LogWriter(logFilePath);
		this.logWriter.printMsg("Launching the Initialization...", this);
		
		/* Configure OreKit. */
		OrekitConfiguration.processConfiguration(this.logWriter);
		
		/* Instanciate the environment. */
		try {
			this.environment = new msp.simulator.environment.Environment(this.logWriter);
			
		} catch (OrekitException e) {
			this.logWriter.printError("Building the Environment Failed."	, this);
			e.printStackTrace();
		}
		
		
	}
	
	public void process() {
		if (this.executionStatus == 1) {
			this.logWriter.printMsg("Processing...", this);
		}
	}
	
	public void exit() {
		this.endDate = LocalDateTime.now();
		this.logWriter.printMsg("Simulation exits with execution status: " 
				+ this.executionStatus, this);
		this.logWriter.close();
	}
	
}
