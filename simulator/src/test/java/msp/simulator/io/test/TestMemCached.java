/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.io.test;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.NumericalSimulator;
import msp.simulator.user.Dashboard;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class TestMemCached {

	/** Logger of the instance. */
	private static final Logger logger = LoggerFactory.getLogger(TestMemCached.class);

	@Test
	public void testSimpleConnection() {
		/* Set up the simulation. */
		NumericalSimulator simu = new NumericalSimulator();
		Dashboard.setDefaultConfiguration();

		Dashboard.setMemCachedConnection(true, "127.0.0.1:11211");
		Dashboard.setSimulationDuration(1.0);

		try {
			/*
			if (System.getProperty("os.name").toLowerCase().contains("mac")) {
				logger.info("### Launching the MemCached server.");
				IO.mac_os_create_memcached_servor();
			} else {
				System.out.println(
						"##### Be sure the MemCached server is up and running #####");
			}
			 */
			
			simu.initialize();

		} catch (Exception e) {
			e.printStackTrace();
		}

		String message = "I am alive!";
		double data = 1234.5678;

		/* Write the data into the io. */
		simu.getIo().getMemcached().set("status", 0, message);
		simu.getIo().getMemcached().set("sensor", 0, data);

		/* Checking that the key are well stored. */
		Assert.assertEquals(
				message, 
				simu.getIo().getMemcached().get("status"));
		
		logger.info((String) simu.getIo().getMemcached().get("status"));

		Assert.assertArrayEquals(
				new double[] {data}, 
				new double[] {(double) simu.getIo().getMemcached().get("sensor")}, 
				0.);

		simu.getIo().getMemcached().delete("status");
		Assert.assertEquals(
				null, 
				simu.getIo().getMemcached().get("status"));

		/* Ending the simulation. */
		simu.exit();
	}


}
