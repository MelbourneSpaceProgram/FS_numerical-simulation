/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.utils.architecture;

import msp.simulator.utils.logs.LogWriter;

/**
 *
 * @author Florian CHAUBEYRE
 */
public abstract class ClusterFactory {
	
	protected LogWriter logWriter;
	
	public ClusterFactory(LogWriter simulatorLogMsg) {
		this.logWriter = simulatorLogMsg ;
	}

}
