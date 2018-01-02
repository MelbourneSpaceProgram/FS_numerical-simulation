/* Copyright 2017-2018 Melbourne Space Program */

package msp.simulator.environment.orbit;

import org.orekit.frames.Frame;
import org.orekit.orbits.CircularOrbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.PVCoordinates;
import org.orekit.utils.TimeStampedPVCoordinates;

/**
 *
 * @author Florian CHAUBEYRE
 */
public class Orbit extends CircularOrbit {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4569681840218655673L;

	/**
	 * @param a
	 * @param ex
	 * @param ey
	 * @param i
	 * @param raan
	 * @param alpha
	 * @param type
	 * @param frame
	 * @param date
	 * @param mu
	 * @throws IllegalArgumentException
	 */
	public Orbit(double a, double ex, double ey, double i, double raan, double alpha, PositionAngle type, Frame frame,
			AbsoluteDate date, double mu) throws IllegalArgumentException {
		super(a, ex, ey, i, raan, alpha, type, frame, date, mu);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param a
	 * @param ex
	 * @param ey
	 * @param i
	 * @param raan
	 * @param alpha
	 * @param aDot
	 * @param exDot
	 * @param eyDot
	 * @param iDot
	 * @param raanDot
	 * @param alphaDot
	 * @param type
	 * @param frame
	 * @param date
	 * @param mu
	 * @throws IllegalArgumentException
	 */
	public Orbit(double a, double ex, double ey, double i, double raan, double alpha, double aDot, double exDot,
			double eyDot, double iDot, double raanDot, double alphaDot, PositionAngle type, Frame frame,
			AbsoluteDate date, double mu) throws IllegalArgumentException {
		super(a, ex, ey, i, raan, alpha, aDot, exDot, eyDot, iDot, raanDot, alphaDot, type, frame, date, mu);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pvCoordinates
	 * @param frame
	 * @param mu
	 * @throws IllegalArgumentException
	 */
	public Orbit(TimeStampedPVCoordinates pvCoordinates, Frame frame, double mu) throws IllegalArgumentException {
		super(pvCoordinates, frame, mu);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pvCoordinates
	 * @param frame
	 * @param date
	 * @param mu
	 * @throws IllegalArgumentException
	 */
	public Orbit(PVCoordinates pvCoordinates, Frame frame, AbsoluteDate date, double mu)
			throws IllegalArgumentException {
		super(pvCoordinates, frame, date, mu);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param op
	 */
	public Orbit(org.orekit.orbits.Orbit op) {
		super(op);
		// TODO Auto-generated constructor stub
	}

}
