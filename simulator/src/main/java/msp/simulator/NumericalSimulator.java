/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator;

import java.time.LocalDateTime;

import msp.simulator.utils.LogWriter;

/**
 * This class is responsible to create the instance of the
 * numerical simulator. 
 * 
 * @author Florian CHAUBEYRE
 */
public class NumericalSimulator {

	/* TODO: Enumerate the execution status. */
	private int executionStatus;
	private LogWriter logWriter;
	
	private final LocalDateTime startDate;
	@SuppressWarnings("unused")
	private LocalDateTime endDate;
	private boolean[] logState;
	
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
		this.executionStatus = 0;
		
		/* Instance of the LogWriter. */
		String logFilePath = "src/main/resources/logs/log-" + this.startDate.toString() +".txt";
		this.logState = new boolean[]{true, true};
		this.logWriter = new LogWriter(this.logState, logFilePath);
	}
	
	public void process() {
		this.executionStatus = 1;
		this.logWriter.printMsg("Processing...", this);
		
		this.logWriter.printError("Ceci est un test.", this);

	}
	
	public void exit() {
		this.endDate = LocalDateTime.now();
		this.logWriter.printMsg("Simulation exits with execution status: " 
				+ this.executionStatus, this);
		this.logWriter.close();
	}
	
	
}
