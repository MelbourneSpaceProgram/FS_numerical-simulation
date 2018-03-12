/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.satellite.io;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.utils.logs.CustomLoggingTools;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class IO {

	/* ******* Public Static Attributes ******* */

	/** Flag to activate the connection to the MemCached server. */
	public static boolean connectMemCached = true;
	
	/** Address of the MemCached server.
	 * The syntax is as follow: "add.add.add.add:port" (a split
	 * is done regarding the ":" character.) <p>
	 * Default value is 127.0.0.1:11211
	 */
	public static String memcachedSocketAddress = "127.0.0.1:11211";

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
	
	/* ***** Client Connection Instances *****/ 
	
	/** MemCached client in use by the simulation. */
	private MemcachedClient memcached;
	

	/**
	 * Create the instance of IO manager.
	 */
	public IO() {
		logger.info(CustomLoggingTools.indentMsg(logger,
				"Launching the IO..."));
		
		this.connectToMemCached = IO.connectMemCached;
		this.memcachedHostAddress = IO.memcachedSocketAddress;
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
					"Shutting Down MemCached..."));
			this.memcached.shutdown((long) 300.0, TimeUnit.MILLISECONDS);
			this.connectToMemCached = false;
		}
	}

	/**
	 * @return The MemCached client of the simulator.
	 */
	public MemcachedClient getMemcached() {
		return memcached;
	}
}



