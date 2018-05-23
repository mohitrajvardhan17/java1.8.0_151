package sun.management.jdp;

public abstract class JdpGenericPacket
  implements JdpPacket
{
  private static final int MAGIC = -1056969150;
  private static final short PROTOCOL_VERSION = 1;
  
  protected JdpGenericPacket() {}
  
  public static void checkMagic(int paramInt)
    throws JdpException
  {
    if (paramInt != -1056969150) {
      throw new JdpException("Invalid JDP magic header: " + paramInt);
    }
  }
  
  public static void checkVersion(short paramShort)
    throws JdpException
  {
    if (paramShort > 1) {
      throw new JdpException("Unsupported protocol version: " + paramShort);
    }
  }
  
  public static int getMagic()
  {
    return -1056969150;
  }
  
  public static short getVersion()
  {
    return 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\jdp\JdpGenericPacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */