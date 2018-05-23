package sun.management.jdp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class JdpPacketWriter
{
  private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
  private final DataOutputStream pkt = new DataOutputStream(baos);
  
  public JdpPacketWriter()
    throws IOException
  {
    pkt.writeInt(JdpGenericPacket.getMagic());
    pkt.writeShort(JdpGenericPacket.getVersion());
  }
  
  public void addEntry(String paramString)
    throws IOException
  {
    pkt.writeUTF(paramString);
  }
  
  public void addEntry(String paramString1, String paramString2)
    throws IOException
  {
    if (paramString2 != null)
    {
      addEntry(paramString1);
      addEntry(paramString2);
    }
  }
  
  public byte[] getPacketBytes()
  {
    return baos.toByteArray();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\jdp\JdpPacketWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */