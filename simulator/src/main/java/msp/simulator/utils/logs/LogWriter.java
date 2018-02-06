/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.utils.logs;

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

	/**
	 * This array contains the availability of each output form.
	 */
	private boolean[] verboseStateArray ;
	private FileWriter logFile;
	
	/**
	 * Default constructor with file and standard outputs activated.
	 * @param filePath Relative workspace name of the log file.
	 */
	public LogWriter(String filePath) {
		this(new boolean[] {true, false}, filePath);
	}
	
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
					this.printError("Fail to create log file directories.", this);
				}
				
				/* Creating Log File. */
				if(!file.createNewFile()) {
					this.verboseStateArray[LogNature_E.FILE.index] = false;
					fileExecStatus = false;
					this.printError("Fail to create log file at:" 
							+ file.getCanonicalPath().toString(),
							this);
				}
				
				/* Associating the FileWriter to the log file. */
				if (fileExecStatus) {
					this.logFile = new FileWriter(file.getAbsoluteFile());
					this.printMsg("Log file successfully created.", this);
				} else {
					this.printError("Processing of creating a log file failed.", this);
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
	 * Getter of the log file of this instance.
	 * @return The FileWriter Object related to the log file of the instance.
	 */
	public FileWriter getLogFile() {
		return this.logFile;
	}
	
	/**
	 * Print a log message in the appropriate log, e.g. file or std output.
	 * The current date is automatically append and the calling class is
	 * added to the message as well as an automatic indentation.
	 * 
	 * @param string Information to store in the log.
	 * @param Object The calling class. This is used to indent the
	 * output regarding the deepness of the calling package. The user
	 * should put here "this".
	 * @see LogWriter.normalizeMsg
	 */
	public void printMsg(String string, Object theCallingClass) {
		string = this.normalizeMsg(string, theCallingClass);
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
	
	/**
	 * Print an error message in the appropriate log, e.g. file or std output.
	 * The current date is automatically append and the calling class is
	 * added as well as an automatic indentation.
	 * 
	 * @param string Error to store in the log.
	 * @param Object The calling class. This is used to indent the
	 * output regarding the deepness of the calling package.
	 * @see LogWriter.normalizeMsg
	 */
	public void printError(String string, Object theCallingClass) {
		string = "## Error ## " + string ;
		this.printMsg(string, theCallingClass);
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
	
	/**
	 * This function is responsible to normalize in a standard form
	 * the user message to print in the log.<p>
	 * 
	 * E.g. "2018-01-04T15:41:08.158069 - NumericalSimulator - Processing..."
	 * 
	 * @param string User message to print.
	 * @param theCallingClass The initial calling class for a log write is used
	 * 				   to add the class name and indent as required.
	 * @return
	 */
	public String normalizeMsg(String string, Object theCallingClass) {
		String message = "" ;
		int indentation = theCallingClass.getClass().getCanonicalName().split("\\.").length - 3;
		
		/* Add the current date to the message. */
		message += LocalDateTime.now() + " - " ;
		/* Indent as required. */
		for (int i = 0; i < indentation; i++) {
			message += "\t" ;
		}
		/* Add the calling Class. */
		message += theCallingClass.getClass().getSimpleName() + " - " ;
		/* Add the user message. */
		message += string ;
		/* Line Break. */
		message += "\n" ;
		return message;
	}
	
}
