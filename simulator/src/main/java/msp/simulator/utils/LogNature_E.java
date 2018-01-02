/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.utils;

/**
 * 
 * Enumeration of the different possibilities of a log.<p>
 * STD_OUT: 0 <p>
 * FILE: 1 <p>
 * 
 * @author Florian CHAUBEYRE
 *
 */
public enum LogNature_E {
	STD_OUT(0),
	FILE(1);
	
	/* !! Do not forget to update the cardinal. */
	protected static final int cardinal = 2;
	
	protected int index;
	
	private LogNature_E(int index) {
		this.index = index;
	}
}
