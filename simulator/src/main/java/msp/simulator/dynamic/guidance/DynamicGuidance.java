/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.dynamic.guidance;

import org.hipparchus.RealFieldElement;
import org.hipparchus.complex.Quaternion;
import org.hipparchus.geometry.euclidean.threed.Rotation;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
import org.orekit.attitudes.AttitudeProvider;
import org.orekit.attitudes.FieldAttitude;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.FieldAbsoluteDate;
import org.orekit.utils.AngularCoordinates;
import org.orekit.utils.FieldPVCoordinatesProvider;
import org.orekit.utils.PVCoordinatesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import msp.simulator.dynamic.propagation.Propagation;
import msp.simulator.dynamic.torques.TorqueProvider;
import msp.simulator.satellite.Satellite;
import msp.simulator.satellite.assembly.Assembly;
import msp.simulator.satellite.assembly.SatelliteStates;
import msp.simulator.utils.logs.CustomLoggingTools;

/**
 * This class provides a dynamic guidance engine.
 * The attitude is computed directly through the
 * equation of motion resulting of the torques.
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

	/** Instance of the Torque Provider. */
	private TorqueProvider torqueProvider;


	/**
	 * 
	 * @param satellite
	 */
	public DynamicGuidance (Satellite satellite, TorqueProvider torqueProvider) {
		logger.info(CustomLoggingTools.indentMsg(logger, 
				"Implementing the Dynamic Guidance Engine..."));

		this.satelliteStates = satellite.getAssembly().getStates();
		this.torqueProvider = torqueProvider;
	}


	/**
	 * Compute the Wilcox Algorithm.
	 * <p>
	 * This algorithm allows to propagate an initial quaternion
	 * through a certain rotational speed during a small step
	 * of time.
	 * 
	 * @param Qi Initial quaternion to propagate
	 * @param spin Instant rotational speed
	 * @param dt Step of time
	 * @return Qj The final quaternion after the rotation due 
	 * to the spin during the step of time.
	 */
	private Quaternion wilcox(Quaternion Qi, Vector3D spin, double dt) {

		/* Vector Angle of Rotation: Theta = dt*W */
		Vector3D theta = new Vector3D(dt, spin);

		/* Compute the change-of-frame Quaternion dQ */
		double dQ0 = 1. - 1./8. * theta.getNormSq();
		double dQ1 = theta.getX() / 2. * (1. - 1./24. * theta.getNormSq());
		double dQ2 = theta.getY() / 2. * (1. - 1./24. * theta.getNormSq());
		double dQ3 = theta.getZ() / 2. * (1. - 1./24. * theta.getNormSq());

		Quaternion dQ = new Quaternion(dQ0, dQ1, dQ2, dQ3);

		/* Compute the final state Quaternion. */
		Quaternion Qj = Qi.multiply(dQ);

		return Qj ;
	}


	@Override
	public Attitude getAttitude(
			PVCoordinatesProvider pvProv, 
			AbsoluteDate date, 
			Frame frame) 
					throws OrekitException {

		/* Load the current satellite state */
		Attitude currentAttitude = this.satelliteStates.getCurrentState().getAttitude();

		Quaternion currentQuaternion = new Quaternion(
				currentAttitude.getRotation().getQ0(),
				currentAttitude.getRotation().getQ1(),
				currentAttitude.getRotation().getQ2(),
				currentAttitude.getRotation().getQ3()
				);

		/* The Spin is an additional parameter to integrate and therefore
		 * not present in the Attitude state of the satellite. */
		Vector3D currentSpin = new Vector3D(
				this.satelliteStates.getCurrentState().getAdditionalState("Spin"));

		/* Propagate the attitude quaternion. */
		Quaternion Qj = this.wilcox(
				currentQuaternion,
				currentSpin,
				Propagation.integrationTimeStep
				);

		/* Compute the acceleration rate. */
		Vector3D accRate = new Vector3D(
				this.torqueProvider.getTorque().getX() / Assembly.cs1_inertia,
				this.torqueProvider.getTorque().getY() / Assembly.cs1_inertia,
				this.torqueProvider.getTorque().getZ() / Assembly.cs1_inertia
				);

		/* Wrap a new attitude from the final quaternion. */
		Attitude finalAttitude = new Attitude(
				date,
				frame,
				new AngularCoordinates(
						new Rotation(
								Qj.getQ0(),
								Qj.getQ1(),
								Qj.getQ2(),
								Qj.getQ3(),
								true
								),
						currentSpin,
						accRate
						)
				);

		//			System.out.println("     - Providing Attitude:   \n" 
		//			+ finalAttitude.getRotation().getAngle() + "\n       "
		//			+ finalAttitude.getSpin() + "\n       "
		//					);

		return finalAttitude;
	}

	@Override
	public <T extends RealFieldElement<T>> FieldAttitude<T> getAttitude(
			FieldPVCoordinatesProvider<T> pvProv,
			FieldAbsoluteDate<T> date,
			Frame frame) 
					throws OrekitException {
		// TODO Auto-generated method stub
		return null;
	}

}
