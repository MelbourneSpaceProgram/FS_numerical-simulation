/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.propagation.integration;

/**
 * Defines the additional states leaded by a differential
 * equation (ODE) that need to be integrated along the propagation.
 *
 * @author Florian CHAUBEYRE
 */
public enum SecondaryStates {
	SPIN (0, 3),
	THETA (3, 3);

	private int index;
	private int size;
	
	/**
	 * Constructor of the enumeration.
	 * @param index in the secondary array
	 * @param size of the state vector
	 */
	private SecondaryStates (int index, int size) { 
		this.index = index; 
		this.size = size;
	}
	
	 /** @return The index of the state in the secondary array. */
	public int getIndex() {return this.index;}
	
	/** @return The size of the state in the scondary array. */
	public int getSize() {return this.size;}
	
	
	/* ******* Public Static Attributes ******* */
	/* These are FINAL: they are not meant to change. */

	/** Name of the additional states and equation. This field cannot be change. */
	public static final String key = "SecondaryStates";

	/** @return The number of additional states. */
	public static final int getCardinal() {return values().length;}
	
	/**
	 * @return The size of the full secondary states array.
	 */
	public static final int getFullArraySize() {
		int fullArraySize = 0;
		for ( SecondaryStates state : SecondaryStates.values()) {
			fullArraySize += state.getSize();
		}
		return fullArraySize;
	}
	
	/**
	 * Extract the state array from the full secondary array.
	 * @param source Secondary array
	 * @param state state to extract
	 * @return Extracted state values only
	 */
	public static final double[] extractState(double[] source, SecondaryStates state) {
		double[] extraction = new double[state.getSize()];
		System.arraycopy(
				source, 
				state.getIndex(), 
				extraction, 
				0, 
				state.getSize());
		return extraction;
	}

	/* ***** End Public Static Attributes ***** */
	/* **************************************** */	
	
}
