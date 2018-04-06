package msp.simulator.satellite.io;

import net.spy.memcached.transcoders.Transcoder;
import net.spy.memcached.CachedData;


/**
 * This transcoder is use in the simulation by the MemCached client
 * to retrieve only the raw data from the common memory as array of 
 * bytes. Thus it avoids some serialization problem between several
 * clients.
 * 
 * @author Rowan SKEWES
 * @author Florian CHAUBEYRE
 */
public class MemcachedRawTranscoder implements Transcoder<byte[]>{
	
	/* ********** Public static field ********** */
	
	/**
	 * Convert a double into the corresponding raw byte array.
	 * @param input Raw double value
	 * @return The associated raw byte array.
	 */
	public static byte[] toRawByteArray(double input) {
		byte[] output = new byte[8];
		long lng = Double.doubleToLongBits(input);
		
		for(int i = 0; i < 8; i++) {
			output[i] = (byte)((lng >> ((7 - i) * 8)) & 0xff);
		}
		
		return output;
	}
	

	/** Flag characterizing a "raw data" transcoding. */
	public static final int FLAG_RAW_DATA = 0xE;

	/** 
	 * {@inheritDoc }
	 * <p>
	 * This transcoder retrieve the raw byte array from the stored data
	 * without interference.
	 */
	public byte[] decode(CachedData d){
		return d.getData();
	}

	/** 
	 * {@inheritDoc }
	 * <p>
	 * @param d Cached data to decode.
	 * @return FALSE This transcoder does not involve the need of asynchornous
	 * decoding.
	 */
	public boolean asyncDecode(CachedData d){
		return false;
	}

	/** 
	 * {@inheritDoc }
	 * <p>
	 * Encode the raw byte array without as it is with the
	 * flag relative to raw data processing.
	 */
	public CachedData encode(byte[] o) {
		return new CachedData(
				FLAG_RAW_DATA,
				o, 
				this.getMaxSize()
				);
	}

	/** 
	 * {@inheritDoc }
	 * <p>
	 * Note that as this transcoder does not have any size limit,
	 * the maximum value returned is the default "infinite" value
	 * of the library.
	 */
	public int getMaxSize(){
		return CachedData.MAX_SIZE;
	}
}
