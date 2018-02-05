///* Copyright 2017-2018 Melbourne Space Program */
//
//package msp.simulator.dynamic.torques;
//
//import org.hipparchus.geometry.euclidean.threed.Vector3D;
//import org.orekit.frames.Frame;
//import org.orekit.time.AbsoluteDate;
//import org.orekit.time.TimeInterpolable;
//import org.orekit.time.TimeShiftable;
//import org.orekit.time.TimeStamped;
//
///**
// * This class represents any torque interaction on the
// * satellite at a given date.
// * <p>
// * This instance is guaranteed to be immutable.
// * 
// * @see TimeStamped
// * @see TimeShiftable
// * @see TimeInterpolable
// *
// * @author Florian CHAUBEYRE
// */
//public class TimeStampedTorque implements TimeStamped {
//	
//	private AbsoluteDate date;
//	private Vector3D torque;	
//	private Frame frame;
//	
//	/**
//	 * Create an instance of torque.
//	 * @param date The date
//	 * @param frame The Reference Frame (Satellite Frame)
//	 * @param torque The intensity vector of the interaction
//	 */
//	public TimeStampedTorque(AbsoluteDate date, Frame frame, Vector3D torque) {
//		this.date = date;
//		this.frame = frame;
//		this.torque = torque;
//	}
//	
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public AbsoluteDate getDate() {
//		return this.date;
//	}
//
//}
