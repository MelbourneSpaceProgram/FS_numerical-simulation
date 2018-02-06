/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.utils.logs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class CustomFileHandler extends FileHandler {

	/**
	 * @throws IOException
	 * @throws SecurityException
	 */
	public CustomFileHandler() throws IOException, SecurityException {
		
		super( "src/main/resources/logs/log-" + new SimpleDateFormat().format(new Date()),
				10000,
				10,
				false
			);
	}

	/**
	 * @param pattern
	 * @throws IOException
	 * @throws SecurityException
	 */
	public CustomFileHandler(String pattern) throws IOException, SecurityException {
		super(pattern);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pattern
	 * @param append
	 * @throws IOException
	 * @throws SecurityException
	 */
	public CustomFileHandler(String pattern, boolean append) throws IOException, SecurityException {
		super(pattern, append);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pattern
	 * @param limit
	 * @param count
	 * @throws IOException
	 * @throws SecurityException
	 */
	public CustomFileHandler(String pattern, int limit, int count) throws IOException, SecurityException {
		super(pattern, limit, count);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pattern
	 * @param limit
	 * @param count
	 * @param append
	 * @throws IOException
	 * @throws SecurityException
	 */
	public CustomFileHandler(String pattern, int limit, int count, boolean append)
			throws IOException, SecurityException {
		super(pattern, limit, count, append);
		// TODO Auto-generated constructor stub
	}

}
