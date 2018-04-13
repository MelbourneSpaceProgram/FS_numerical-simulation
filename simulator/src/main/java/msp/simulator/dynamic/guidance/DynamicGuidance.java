/* Copyright 20017-2018 Melbourne Space Program
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * This class is responsible to provide the attitude at any time
 * of the simulation.
 * The processing is dynamic. It means there is not an actual
 * attitude law over time, predefined and deterministic, but 
 * the attitude is directly extracted from the current state 
 * of the satellite instance in the simulation. This is where
 * the attitude is actually computed. Thus this class is only
 * a mirror.
 * <p>
 * Note that a proper propagation needs to access some intermediate
 * states, that's why in the case the date is not synchronized with
 * the time discretisation of the simulation, the returned attitude
 * is linearly interpolated for a very small time step through the
 * current spin.
 * <p>
 * <i>Implements AttitudeProvider</i>
 *
 * @author Florian CHAUBEYRE <chaubeyre.f@gmail.com>
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
	 */
	public DynamicGuidance (Satellite satellite) {
		logger.info(CustomLoggingTools.indentMsg(logger, 
				"Implementing the Dynamic Guidance Engine..."));

		this.satelliteStates = satellite.getAssembly().getStates();
	}


	/**
	 * Simply return the current attitude of the satellite.
	 * <p>
	 * Note that the attitude is actually computed dynamically 
	 * elsewhere through the propagation services through the 
	 * equations of motion.
	 * <p>
	 * This method then allows the attitude-dependent functions,
	 * e.g. gravity torque etc., to compute their interactions
	 * with the current updated satellite attitude.
	 * <p>
	 * During the integration, we need to access the attitude for
	 * intermediate time step. Then this method returns a linearly
	 * extrapolated attitude in case of intermediate date.
	 */
	@Override
	public Attitude getAttitude(
			PVCoordinatesProvider pvProv, 
			AbsoluteDate date, 
			Frame frame) 
					throws OrekitException {
		
		/* Small time shift between the intermediary integration step (cf. Runge Kutta 4) */
		double shift = date.durationFrom(
				this.satelliteStates.getCurrentState().getAttitude().getDate());

		/* Shift slightly the attitude so the date match with the integration intermediary step. */
		return this.satelliteStates.getCurrentState().getAttitude().shiftedBy(shift);
	}

	/**
	 * Do nothing and return null.
	 * @deprecated
	 */
	@Override
	@Deprecated
	public <T extends RealFieldElement<T>> FieldAttitude<T> getAttitude(
			FieldPVCoordinatesProvider<T> pvProv,
			FieldAbsoluteDate<T> date,
			Frame frame) 
					throws OrekitException {

		return null;
	}

}
