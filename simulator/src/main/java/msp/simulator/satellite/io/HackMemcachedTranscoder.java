package msp.simulator.satellite.io;

import net.spy.memcached.transcoders.Transcoder;
import net.spy.memcached.CachedData;
/*
  Begin hack transcoder for spymemcached which wont just give me my data
*/
public class HackMemcachedTranscoder implements Transcoder<byte[]>{

    public byte[] decode(CachedData d){return d.getData();}

    public boolean asyncDecode(CachedData d){return false;}

    public CachedData encode(byte[] o){return null;}

    public int getMaxSize(){return 8;}
}
