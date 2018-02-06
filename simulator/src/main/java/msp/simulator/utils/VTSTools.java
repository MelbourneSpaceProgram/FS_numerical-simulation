package msp.simulator.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.hipparchus.geometry.euclidean.threed.Rotation;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.Propagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

/**
 * This class enables to generate easily OEM and AEM files
 * to represent the data in VTS.
 */
public final class VTSTools {

    /** Column separator */
    private static final String SEP = " ";
    /** Useful strings */
    private static final String NL = "\n";
    /** Useful strings */
    private static final String FS = File.separator;
    /** Default directory */
    private static String DEFAULT_DIR = "";
    /** OEM file name */
    private static final String OEM_FILE = "MspOEM";
    /** AEM file name */
    private static final String AEM_FILE = "MspAEM";
    /** Extension */
    private static final String EXT = ".txt";
    
    /** Unit conversion */
    private static final double M_TO_KM = 1e-3;
    /** Default step */
    private static final double STEP = 60.;
    /** Reference date */
    private static final AbsoluteDate reference = AbsoluteDate.MODIFIED_JULIAN_EPOCH;
    
    public static void main (String[] args) {
        System.out.println(DEFAULT_DIR);
    }
    
    static {
        final File oem = new File(DEFAULT_DIR);
        if (!oem.exists()) {
            oem.mkdir();
        }

        // directory
        String dir = VTSTools.class.getClassLoader().getResource("").getPath();
        String[] res = dir.split("/");
        
        // look for formation
        int index = 0;
        int index1 = 0;
        for (String r : res) {
            index1++;
            if (r.contains("formation") && index == 0) {
                index = index1;
            }
        }
        
        // construct vts dir
        @SuppressWarnings("unused")
		String str = "";
        for (int i = 0; i < index - 2; i++) {
            str += res[i] + FS;
        }
        str += "vts" + FS + "Data" + FS + "CubeSat" + FS + "Data";
        
        DEFAULT_DIR = "/Users/florianchaubeyre/Desktop/MSP/simulator/workspace/simulator/src/main/resources/logs";

        System.out.println(DEFAULT_DIR);
    }
    
    /**
     * Create a position ephemeris file
     * @param start start date
     * @param end end date
     * @param propagator the propagator
     * @param tag to add to filename
     */
    public static void generateOEMFile(final AbsoluteDate start, final AbsoluteDate end,
            final Propagator propagator, final String tag) {
        
        // Make sur the DEFAULT_DIR points to the classes
        // If you are running test classes, make sure it points to test-classes!
        // See static { ... }
        
        // create a new file
        final File file = new File(DEFAULT_DIR + FS + OEM_FILE + tag.replaceAll("\\s", "_") + EXT);
        file.delete();

        try {
            
            // utc timescale
            final TimeScale utc = TimeScalesFactory.getUTC();
            final Frame eme2000 = FramesFactory.getEME2000();
            
            // new file writer
            final FileWriter fw = new FileWriter(file);
            
            // header
            fw.append("CIC_OEM_VERS = 2.0");
            fw.append(NL);
            fw.append("CREATION_DATE = " + getCurrentDate(utc).toString(TimeScalesFactory.getUTC()));
            fw.append(NL);
            fw.append("ORIGINATOR = MSP_SIM_0.1");
            fw.append(NL);
            fw.append("     ");
            fw.append(NL);
            fw.append("META_START");
            fw.append(NL);
            fw.append("");
            fw.append(NL);
            fw.append("OBJECT_NAME = MSP_CubeSat_1");
            fw.append(NL);
            fw.append("OBJECT_ID = MSP001");
            fw.append(NL);
            fw.append("CENTER_NAME = EARTH");
            fw.append(NL);
            fw.append("REF_FRAME = EME2000");
            fw.append(NL);
            fw.append("TIME_SYSTEM = UTC");
            fw.append(NL);
            fw.append("");
            fw.append(NL);
            fw.append("META_STOP");
            fw.append(NL);

            // ephemeris
            AbsoluteDate current = start;
            Vector3D position;
            double seconds;
            int days;
            StringBuffer bf;
            while (current.offsetFrom(end, utc) < 0) {
                // elapsed time
                seconds = current.offsetFrom(reference, utc);
                days = (int) (seconds / Constants.JULIAN_DAY);
                seconds = seconds - days * Constants.JULIAN_DAY;
                
                // position at time in EM2000
                position = propagator.getPVCoordinates(current, eme2000).getPosition();
                
                // write to file
                bf = new StringBuffer();
                bf.append(days);
                bf.append(SEP);
                bf.append(seconds);
                bf.append(SEP);
                bf.append(position.getX() * M_TO_KM);
                bf.append(SEP);
                bf.append(position.getY() * M_TO_KM);
                bf.append(SEP);
                bf.append(position.getZ() * M_TO_KM);
                
                fw.append(bf.toString());
                fw.append(NL);
                
                // move forward
                current = current.shiftedBy(STEP);
            }
            
            // close file writer
            fw.close();
            
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OrekitException e) {
            e.printStackTrace();
        }
        System.out.println("VTS : Done...");
    }
    
    /**
     * Create an attitude ephemeris file
     * @param start start date
     * @param end end date
     * @param propagator the propagator
     * @param tag tag to add to filename
     */
    public static void generateAEMFile(final AbsoluteDate start, final AbsoluteDate end,
    		final Propagator propagator, final String tag) {
    	
    	// create a new file
    	final File file = new File(DEFAULT_DIR + FS + AEM_FILE + tag.replaceAll("\\s", "_") + EXT);
    	file.delete();
    	
    	try {
    		
    		// utc timescale
    		final TimeScale utc = TimeScalesFactory.getUTC();
    		
    		// new file writer
    		final FileWriter fw = new FileWriter(file);
    		
    		// header
    		fw.append("CIC_AEM_VERS = 1.0");
    		fw.append(NL);
    		fw.append("CREATION_DATE = " + getCurrentDate(utc).toString(TimeScalesFactory.getUTC()));
    		fw.append(NL);
    		fw.append("ORIGINATOR = MSP_SIM_0.1");
    		fw.append(NL);
    		fw.append("     ");
    		fw.append(NL);
    		fw.append("META_START");
    		fw.append(NL);
    		fw.append("");
    		fw.append(NL);
    		fw.append("OBJECT_NAME = MSP_CubeSat_1");
    		fw.append(NL);
    		fw.append("OBJECT_ID = MSP001");
    		fw.append(NL);
    		fw.append("CENTER_NAME = EARTH");
    		fw.append(NL);
    		fw.append("REF_FRAME_A = EME2000");
    		fw.append(NL);
    		fw.append("REF_FRAME_B = SC_BODY_1");
    		fw.append(NL);
    		fw.append("ATTITUDE_DIR = A2B");
    		fw.append(NL);
    		fw.append("TIME_SYSTEM = UTC");
    		fw.append(NL);
    		fw.append("ATTITUDE_TYPE = QUATERNION");
    		fw.append(NL);
    		fw.append("");
    		fw.append(NL);
    		fw.append("META_STOP");
    		fw.append(NL);
    		
    		// ephemeris
    		AbsoluteDate current = start;
    		Attitude attitude;
    		Rotation rot;
    		double seconds;
    		int days;
    		StringBuffer bf;
    		while (current.offsetFrom(end, utc) < 0) {
    			// elapsed time
    			seconds = current.offsetFrom(reference, utc);
    			days = (int) (seconds / Constants.JULIAN_DAY);
    			seconds = seconds - days * Constants.JULIAN_DAY;
    			
    			// position at time in EM2000
    			attitude = propagator.propagate(current).getAttitude();
    			rot = attitude.getRotation().revert();
    			
    			// write to file
    			bf = new StringBuffer();
    			bf.append(days);
    			bf.append(SEP);
    			bf.append(seconds);
    			bf.append(SEP);
    			bf.append(rot.getQ0());
    			bf.append(SEP);
    			bf.append(rot.getQ1());
    			bf.append(SEP);
    			bf.append(rot.getQ2());
    			bf.append(SEP);
    			bf.append(rot.getQ3());
    			
    			fw.append(bf.toString());
    			fw.append(NL);
    			
    			// move forward
    			current = current.shiftedBy(STEP);
    		}
    		
    		// close file writer
    		fw.close();
    		
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	} catch (OrekitException e) {
    		e.printStackTrace();
    	}
    	System.out.println("VTS : Done...");
    }
    
    /** 
     * Get the current date
     * @return the current date - AbsoluteDate
     * @throws OrekitException
     */
    private static AbsoluteDate getCurrentDate(final TimeScale scale) {
        final GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+00"));
        return new AbsoluteDate(cal.getTime(), scale);
    }

}
