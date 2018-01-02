/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * This class enables to write messages into a specific log.<p>
 * E.g. a file or the std output.
 * 
 * @author Florian CHAUBEYRE
 */
public class LogWriter {

	private boolean[] verboseStateArray;
	private FileWriter logFile;
	
	/**
	 * Constructor of a LogWriter.
	 * @param outputStateArray Table of boolean allowing - or not - the storing
	 * of the log under the related form.
	 * @param filePath The path of the log file in the workspace
	 * @see LogNature_E
	 */
	public LogWriter(boolean[] outputStateArray, String filePath) {
		try {
			this.verboseStateArray = outputStateArray;
		} catch (Exception e) {
				e.printStackTrace();
		}
		
		/* FILE Processing */
		if (this.verboseStateArray[LogNature_E.FILE.index]) {
			try {
				boolean fileExecStatus = true;
				File file = new File(filePath);
				File directory = new File(file.getParentFile().getAbsolutePath());
				
				/* Creating directories. */
				if(!directory.exists() && !directory.mkdirs()) {
					this.verboseStateArray[LogNature_E.FILE.index] = false;
					fileExecStatus = false;
					this.printError("Fail to create log file directories.");
				}
				
				/* Creating Log File. */
				if(!file.createNewFile()) {
					this.verboseStateArray[LogNature_E.FILE.index] = false;
					fileExecStatus = false;
					this.printError("Fail to create log file at:" 
							+ file.getCanonicalPath().toString());
				}
				
				/* Associating the FileWriter to the log file. */
				if (fileExecStatus) {
					this.logFile = new FileWriter(file.getAbsoluteFile());
					this.printMsg("Log file successfully created.");
				} else {
					this.printError("Processing of creating a log file failed.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Set the verbose state array of the log.
	 * @param newStateArray boolean array
	 * @see LogNature_E
	 */
	public void setVerboseStateArray(boolean[] newStateArray) {
		this.verboseStateArray = newStateArray;
	}
	
	/**
	 * Print a log message in the appropriate log, e.g. file or std output.
	 * The current date is automatically append.
	 * @param string Information to store in the log.
	 */
	public void printMsg(String string) {
		/* Add the current date to the message. */
		string += "\n\t\t" + LocalDateTime.now() + "\n\n";
		
		if(this.verboseStateArray[LogNature_E.STD_OUT.index]) {
			System.out.print(string);
		}
		if(this.verboseStateArray[LogNature_E.FILE.index]) {
			try {
				this.logFile.write(string);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void printError(String string) {
		string = "Error: " + string;
		this.printMsg(string);
	}
	
	/**
	 * Close any of the required entity of the LogWriter, e.g. the FileWriter.
	 */
	public void close() {
		if(this.verboseStateArray[LogNature_E.FILE.index]) {
			try {
				this.logFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
