/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author Florian CHAUBEYRE
 *
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
				File file = new File(filePath);
				File directory = new File(file.getParentFile().getAbsolutePath());
				directory.mkdirs();
				file.createNewFile();
				this.logFile = new FileWriter(file.getAbsoluteFile());
				
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
	 * @param string Information to store in the log.
	 */
	public void print(String string) {
		/* Add the current date to the message. */
		string += "\n\t\t" + LocalDateTime.now() + "\n";
		
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
