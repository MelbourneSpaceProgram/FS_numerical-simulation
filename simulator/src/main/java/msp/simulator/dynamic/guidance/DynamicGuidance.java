/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.guidance;

import org.hipparchus.RealFieldElement;
import org.orekit.attitudes.Attitude;
import org.orekit.attitudes.AttitudeProvider;
import org.orekit.attitudes.FieldAttitude;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.FieldAbsoluteDate;
import org.orekit.utils.FieldPVCoordinatesProvider;
import org.orekit.utils.PVCoordinatesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.satellite.Satellite;
import msp.simulator.satellite.assembly.SatelliteStates;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class provides a dynamic guidance engine.
 * It means that it doesn't exit an attitude law
 * over time but the attitude is actually computed
 * dynamically through the torque data received at
 * each new step. This class only forward the already
 * computed attitude to any client that need it.
 * <p>
 * <i>Implements AttitudeProvider</i>
 *
 * @author Florian CHAUBEYRE
 */
public class DynamicGuidance implements AttitudeProvider {

	/** Serial Version UID */
	private static final long serialVersionUID = 9146812237119966975L;

	/** Instance of the Logger of the class. */
	private static final Logger logger = LoggerFactory.getLogger(
			DynamicGuidance.class);

	/** Link the instance of the satellite states. */
	private SatelliteStates satelliteStates;

	/**
	 * Create the instance of dynamic guidance, i.e. torque driven,
	 * in the simulation.
	 * @param satellite Instance of the Simulation
	 * @param torqueProvider The "torque-law-over-time" provider
	 */
	public DynamicGuidance (Satellite satellite) {
		logger.info(CustomLoggingTools.indentMsg(logger, 
				"Implementing the Dynamic Guidance Engine..."));

		this.satelliteStates = satellite.getAssembly().getStates();
	}


	/**
	 * Simply return the current attitude of the satellite.
	 * The attitude is actually computed dynamicaly by the 
	 * propagator through the equations of motion.
	 * 
	 * This method then allows the attitude-dependent functions,
	 * e.g. gravity torque etc., to compute their interactions
	 * with the current updated satellite attitude.
	 * <p>
	 * During the integration, we need to access the attitude for
	 * intermediate time step. Then this method returns a linearly
	 * extrapolated attitude.
	 * <p>
	 * Be aware this returned attitude is NOT the real attitude
	 * of the satellite but only an image used for linear forces
	 * computation.
	 */
	@Override
	public Attitude getAttitude(
			PVCoordinatesProvider pvProv, 
			AbsoluteDate date, 
			Frame frame) 
					throws OrekitException {
		

		double shift = date.durationFrom(
				this.satelliteStates.getCurrentState().getAttitude().getDate());
		return this.satelliteStates.getCurrentState().getAttitude().shiftedBy(shift);
	}

	/**
	 * Do nothing and return null.
	 */
	@Override
	public <T extends RealFieldElement<T>> FieldAttitude<T> getAttitude(
			FieldPVCoordinatesProvider<T> pvProv,
			FieldAbsoluteDate<T> date,
			Frame frame) 
					throws OrekitException {
		
		/** TODO: auto-generated method stub. */
		return null;
	}

}
