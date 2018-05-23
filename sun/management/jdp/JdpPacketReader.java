package sun.management.jdp;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JdpPacketReader
{
  private final DataInputStream pkt;
  private Map<String, String> pmap = null;
  
  public JdpPacketReader(byte[] paramArrayOfByte)
    throws JdpException
  {
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    pkt = new DataInputStream(localByteArrayInputStream);
    try
    {
      int i = pkt.readInt();
      JdpGenericPacket.checkMagic(i);
    }
    catch (IOException localIOException1)
    {
      throw new JdpException("Invalid JDP packet received, bad magic");
    }
    try
    {
      short s = pkt.readShort();
      JdpGenericPacket.checkVersion(s);
    }
    catch (IOException localIOException2)
    {
      throw new JdpException("Invalid JDP packet received, bad protocol version");
    }
  }
  
  public String getEntry()
    throws EOFException, JdpException
  {
    try
    {
      int i = pkt.readShort();
      if ((i < 1) && (i > pkt.available())) {
        throw new JdpException("Broken JDP packet. Invalid entry length field.");
      }
      byte[] arrayOfByte = new byte[i];
      if (pkt.read(arrayOfByte) != i) {
        throw new JdpException("Broken JDP packet. Unable to read entry.");
      }
      return new String(arrayOfByte, "UTF-8");
    }
    catch (EOFException localEOFException)
    {
      throw localEOFException;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new JdpException("Broken JDP packet. Unable to decode entry.");
    }
    catch (IOException localIOException)
    {
      throw new JdpException("Broken JDP packet. Unable to read entry.");
    }
  }
  
  public Map<String, String> getDiscoveryDataAsMap()
    throws JdpException
  {
    if (pmap != null) {
      return pmap;
    }
    String str1 = null;
    String str2 = null;
    HashMap localHashMap = new HashMap();
    try
    {
      for (;;)
      {
        str1 = getEntry();
        str2 = getEntry();
        localHashMap.put(str1, str2);
      }
    }
    catch (EOFException localEOFException)
    {
      if (str2 == null) {
        throw new JdpException("Broken JDP packet. Key without value." + str1);
      }
      pmap = Collections.unmodifiableMap(localHashMap);
    }
    return pmap;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\jdp\JdpPacketReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */