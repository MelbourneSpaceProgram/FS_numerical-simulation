/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite.io;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;

/**
 * Satellite IO manager of the simulation.
 * 
 * @author Florian CHAUBEYRE
 */
public class IO {

	/* ******* Public Static Attributes ******* */

	/** Flag to activate the connection to the MemCached server. */
	public static boolean connectMemCached = false;

	/** Address of the MemCached server.
	 * The syntax is as follow: "add.add.add.add:port" (a split
	 * is done regarding the ":" character.) <p>
	 * Default value is 127.0.0.1:11211
	 */
	public static String memcachedSocketAddress = "127.0.0.1:11211";

	/** Flag to activate the connection to the VTS visualization software. */
	public static boolean connectVts = false;

	/* **************************************** */

	/** Logger of the class */
	private static final Logger logger = LoggerFactory.getLogger(IO.class);

	/** Flag to activate the connection to the MemCached server. */
	private boolean connectToMemCached;

	/** Address of the MemCached server.
	 * The syntax is as follow: "add.add.add.add:port" (a split
	 * is done regarding the ":" character.) <p>
	 * Default value is 127.0.0.1:11211
	 */
	private String memcachedHostAddress;

	/** MemCached client in use by the simulation. */
	private MemcachedClient memcached;

	/** Raw transcoder to deserialize Memcached data. */
	private MemcachedRawTranscoder rawTranscoder;

	/** Flag to activate the connection to the VTS visualization software. */
	private boolean connectToVts = false;

	/** VTS client in use for real time vizualisation. */
	private Socket vts;

	/** VTS Socket Output stream. */
	private PrintWriter vtsOut;

	/**
	 * Create the instance of IO manager.
	 */
	public IO() {
		logger.info(CustomLoggingTools.indentMsg(logger,
				"Launching the IO..."));

		this.connectToMemCached = IO.connectMemCached;
		this.memcachedHostAddress = IO.memcachedSocketAddress;
		this.rawTranscoder = new MemcachedRawTranscoder();

		this.connectToVts = IO.connectVts;
	}

	/**
	 * Launch the MemCached server from a specific OS X environment.
	 * @deprecated For test purpose only: depends on the OS and the machine.
	 */
	public static void mac_os_create_memcached_servor() {
		Process memcached;
		try {
			memcached = Runtime.getRuntime().exec(
					"/usr/local/bin/memcached -d "
							+ memcachedSocketAddress.split(":")[0]
									+ "-p " + memcachedSocketAddress.split(":")[1]
					);
			memcached.waitFor();
		} catch (IOException | InterruptedException e) {
			logger.error("MemCached server setup failed.");
		}

	}

	public void start() {
		/* Connecting the MemCached Server. */
		if (this.connectToMemCached) {

			/* ******* For advanced connection settings. ******** */
			/* Initializing Authentication protocol. */
			@SuppressWarnings("unused")
			AuthDescriptor auth = new AuthDescriptor(
					new String[] {"PLAIN"}, 
					new PlainCallbackHandler(
							"user",
							"password"
							)
					);

			/* Initializing the connection settings. */
			@SuppressWarnings("unused")
			ConnectionFactory connection = new ConnectionFactoryBuilder()
			.setDaemon(true)
			//.setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
			//.setAuthDescriptor(auth)
			.build();
			/* ************************************************** */

			InetSocketAddress host = new InetSocketAddress(
					this.memcachedHostAddress.split(":")[0], 
					Integer.valueOf(this.memcachedHostAddress.split(":")[1]));

			if (!host.isUnresolved()) {
				try {
					this.memcached = new MemcachedClient(host);

				} catch (IOException ex) {
					logger.error("Connection to the MemCached server failed.");
				}
			} else {
				logger.error("MemCached Host Address is unresolved: connection aborted.");
			}
		}

		/* Connecting to the VTS server. */
		if (this.connectToVts) {
			try {
				this.vts = new Socket("localhost", 8888);
				this.vtsOut = new PrintWriter(this.vts.getOutputStream(), true);
				
				/* Hard-coded because he will stay in history as the first 
				 * leader of the MSP simulation facilities! */
				String n = "N" + "a" + "p" + "o" + "l" + "e" + "o" + "n";
				this.vtsOut.println("INIT " + n + " REGULATING");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/* Other Connections...	*/
		/* ......				*/
		/* *********************	*/
	}

	/**
	 * Shut down all of the active connections.
	 */
	public void stop() {
		/* Shut down MemCached connection. */
		if (this.connectToMemCached) {
			logger.info(CustomLoggingTools.indentMsg(logger, 
					"Shutting Down MemCached Client..."));
			this.memcached.shutdown((long) 300.0, TimeUnit.MILLISECONDS);
			this.connectToMemCached = false;
		}
		/* Shut down VTS connection. */
		if (this.connectToVts) {
			logger.info(CustomLoggingTools.indentMsg(logger, 
					"Shutting VTS Client..."));
			try {
				this.vts.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Compute and send the satellite state to real-time VTS as a command for visualization.
	 * @param currentState of the satellite
	 */
	public void exportToVts(SpacecraftState currentState) {
		if (    this.vts.isConnected()
				&&
				!this.vts.isClosed()
				) {
			try {
				/* Convert the J2000 date into a JD1950 Julian Day (CNES) in use in real-time VTS. */
				AbsoluteDate currentDate = currentState.getDate();

				AbsoluteDate referenceDate = AbsoluteDate.FIFTIES_EPOCH;

				double seconds = currentDate.offsetFrom(
						referenceDate,
						TimeScalesFactory.getUTC()
						);

				int days = (int) (seconds / Constants.JULIAN_DAY);
				seconds = seconds - days * Constants.JULIAN_DAY;
				int hours = (int) (seconds / 3600);
				seconds = seconds - hours * 3600;
				int minutes = (int) (seconds / 60);
				seconds = seconds - minutes * 60;

				/* Fraction of days of the Julian Day. */
				double f = 
						(hours - 12) / 24 +
						minutes / 1440 +
						seconds / Constants.JULIAN_DAY
						;

				/* JD1950 (CNES) Julian day offset. */
				double cnesJulianDayOffset = days + f;

				/* Example of VTS data command:
				 * DATA 123 20447.000174 pos "-6538.3475061863419 2703.5361504162843 197.30707005759857"
				 */

				/* OEM Data to stream out to VTS. Note the conversion to KM. */
				String cmdOem = 
						"DATA " +
								cnesJulianDayOffset + " " +
								"pos \"" +
								+ currentState.getPVCoordinates().getPosition().getX() * 1e-3 +
								" " +
								+ currentState.getPVCoordinates().getPosition().getY() * 1e-3 +
								" " +
								+ currentState.getPVCoordinates().getPosition().getZ() * 1e-3 +
								"\""
								;
				/* AEM Data to stream out to VTS. */
				String cmdAem = 
						"DATA " +
								cnesJulianDayOffset + " " +
								"att \"" +
								+ currentState.getAttitude().getRotation().getQ0() +
								" " +
								+ currentState.getAttitude().getRotation().getQ1() +
								" " +
								+ currentState.getAttitude().getRotation().getQ2() +
								" " +
								+ currentState.getAttitude().getRotation().getQ3() +
								"\""
								;

				/* Each command should be associated with the time imposed by the simulation. */
				this.getVtsOutputStream().println("TIME " + cnesJulianDayOffset);
				this.getVtsOutputStream().println(cmdOem);
				this.getVtsOutputStream().println(cmdAem);

			} catch(OrekitException e) {
				e.printStackTrace();
			}
		} else {
			logger.error("VTS Socket is not operating.");
		}
	}

	/**
	 * @return the connectToMemCached
	 */
	public boolean isConnectToMemCached() {
		return connectToMemCached;
	}

	/**
	 * @return The MemCached client of the simulator.
	 */
	public MemcachedClient getMemcached() {
		return memcached;
	}

	/**
	 * @return VTS Client output stream.
	 */
	public PrintWriter getVtsOutputStream() {
		return vtsOut;
	}

	/**
	 * @return Connection to VTS socket flag.
	 */
	public boolean isConnectToVts() {
		return connectToVts;
	}

	/**
	 * @return the rawTranscoder
	 */
	public MemcachedRawTranscoder getRawTranscoder() {
		return rawTranscoder;
	}

}
