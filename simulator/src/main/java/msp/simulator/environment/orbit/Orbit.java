/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment.orbit;

import org.orekit.frames.FramesFactory;
import org.orekit.frames.LocalOrbitalFrame;
import org.orekit.frames.LOFType;
import org.orekit.orbits.CircularOrbit;

/**
 * This class represents an instance of an orbit for the MSP
 * project.<p>
 * This class is directly related to OREKIT but for a better
 * understanding of the modules of the simulator, it is extended
 * here.<p>
 * It also provides some other methods and tools to the simulator.
 * 
 * @see org.orekit.orbits.Orbit
 *
 * @author Florian CHAUBEYRE
 */
public class Orbit extends CircularOrbit {

	/**
	 * Generated Serial Version UID.
	 */
	private static final long serialVersionUID = 8798538622702226489L;
	
	/**
	 * Local Orbital Frame related to the instance of the orbit.
	 */
	private LocalOrbitalFrame localOrbitalFrame;

	public Orbit(org.orekit.orbits.Orbit orbit) {
		super(orbit);
		this.createLOF(this);
	}
	
	
	
	/**
	 * Get the Local Orbital Frame related to the orbit.
	 * The convention used is VNC:<p>
	 * - Z completes the frame.<p>
	 * - X aligned with velocity vector.<p>
	 * - Y aligned with orbit momentum.<p>
	 * @return
	 */
	public LocalOrbitalFrame getLof() {
		return this.localOrbitalFrame;
	}
	
	/**
	 * This method creates an instance of the local orbital
	 * frame related to the instance of the orbit.
	 * The convention used is VNC:<p>
	 * - X aligned with velocity vector.<p>
	 * - Y aligned with orbit momentum.<p>
	 * - Z completes the frame.<p>
	 * 
	 * @see org.orekit.frames.LOFType.VNC
	 * @param orbit 
	 */
	private void createLOF(Orbit orbit) {
		this.localOrbitalFrame = new LocalOrbitalFrame (
				FramesFactory.getEME2000(),
				LOFType.VNC,
				orbit,
				"LOF");
	}
	
	
	
	
	
}