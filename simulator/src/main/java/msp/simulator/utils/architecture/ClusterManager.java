/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.utils.architecture;

import msp.simulator.utils.logs.LogWriter;

/**
 *
 * @author Florian CHAUBEYRE
 */
public abstract class ClusterManager {
	
	protected LogWriter logWriter;
	
	public ClusterManager(LogWriter simulatorLogMsg) {
		this.logWriter = simulatorLogMsg ;
	}

}
