/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic;

import org.hipparchus.util.FastMath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import msp.simulator.NumericalSimulator;


/**
 *
 * @author Florian CHAUBEYRE
 */
public class AttitudePropagation {

	/**
	 * Process a simple rotation at constant spin to
	 * check the attitude quaternion propagation.
	 */
	@Test 
	public void testSimpleRotation() {
		/* Duration of the rotation. */
		double duration = 100;
		
		/* Constant Spin: 
		 * The rotation of PI should then occur during
		 * exactly te specified time.
		 */
		double spin = FastMath.PI / 100. ; 
		
		
		
	}
	
}
