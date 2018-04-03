/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.utils.logs;

import org.orekit.propagation.SpacecraftState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class CustomLoggingTools {

	/** Private Constructor to avoid instanciation.
	 * This class only provide tools.
	 */
	private CustomLoggingTools() {}

	/** Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(CustomLoggingTools.class);


	/**
	 * Indent a message regarding its position in the package
	 * hierarchie. 
	 * @param logger The Logger of the calling class to get the position.
	 * @param userMsg The User message to indent.
	 * @return String The user message but indented.
	 */
	public static String indentMsg(Logger logger, String userMsg) {
		String message = "";

		/* Extract the Calling class from the logger. */
		Class<?> theCallingClass = null;
		try {
			theCallingClass = Class.forName(logger.getName());
		} catch (ClassNotFoundException e) {
			/* If the class is not found, abort the indentation. */
			return userMsg;
		} 

		/* Indent as required. */
		int indentation = theCallingClass.getCanonicalName().split("\\.").length - 3;
		CustomLoggingTools.logger.debug("Indentation = " + indentation );
		for (int i = 0; i < indentation; i++) {
			message += "\t" ;
		}

		/* Add the user message. */
		message += userMsg ;

		return message;
	}

	/**
	 * Return a descriptive string of the state containing attitude, 
	 * spin and acceleration
	 * @param state to convert into a string
	 * @param message to append prior the description.
	 * @return String A string description of a SpacecraftState
	 */
	public static String toString(String message, SpacecraftState state) {
		String s = message + "\n"
				+ "Date: " + state.getDate().toString() + "\n"
				+ "Attitude: ["
				+ state.getAttitude().getRotation().getQ0() + ", "
				+ state.getAttitude().getRotation().getQ1() + ", "
				+ state.getAttitude().getRotation().getQ2() + ", "
				+ state.getAttitude().getRotation().getQ3()
				+ "] \n" +
				"Spin    : " + state.getAttitude().getSpin().toString() + "\n" +
				"RotAcc  : " + state.getAttitude().getRotationAcceleration().toString()
				;
		
		return s;
	}


}
