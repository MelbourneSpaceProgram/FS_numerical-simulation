/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dashboard;

import java.time.LocalDateTime;

import msp.simulator.utils.LogWriter;

/**
 * @author Florian CHAUBEYRE
 *
 */
public class NumericalSimulator {

	/* TODO: Enumerate the execution status. */
	protected int executionStatus;
	protected LogWriter logWriter;
	protected String logFilePath;
	
	@SuppressWarnings("unused")
	private final LocalDateTime startDate;
	@SuppressWarnings("unused")
	private LocalDateTime endDate;
	private boolean[] logState;
	
	public NumericalSimulator() {
		this.executionStatus = 0;
		this.logFilePath = "src/resources/logs/log-simu.txt";
		this.logState = new boolean[]{true, true};
		this.logWriter = new LogWriter(this.logState, this.logFilePath);
		
		this.startDate = LocalDateTime.now(); 
	}
	
	public void process() {
		/* LAUNCHING SIMULATION */
		this.logWriter.print("Launching Simulation.");
		
		/* PROCESSING */
		this.executionStatus = 1;
		
		/* EXITING SIMULATION */
		this.endDate = LocalDateTime.now();
		this.logWriter.print("Simulation exits with execution status: " 
				+ this.executionStatus);
		this.logWriter.close();
	}
	
	
	
	
}
