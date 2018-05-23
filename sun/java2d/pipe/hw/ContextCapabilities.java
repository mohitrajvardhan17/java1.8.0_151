package sun.java2d.pipe.hw;

public class ContextCapabilities
{
  public static final int CAPS_EMPTY = 0;
  public static final int CAPS_RT_PLAIN_ALPHA = 2;
  public static final int CAPS_RT_TEXTURE_ALPHA = 4;
  public static final int CAPS_RT_TEXTURE_OPAQUE = 8;
  public static final int CAPS_MULTITEXTURE = 16;
  public static final int CAPS_TEXNONPOW2 = 32;
  public static final int CAPS_TEXNONSQUARE = 64;
  public static final int CAPS_PS20 = 128;
  public static final int CAPS_PS30 = 256;
  protected static final int FIRST_PRIVATE_CAP = 65536;
  protected final int caps;
  protected final String adapterId;
  
  protected ContextCapabilities(int paramInt, String paramString)
  {
    caps = paramInt;
    adapterId = (paramString != null ? paramString : "unknown adapter");
  }
  
  public String getAdapterId()
  {
    return adapterId;
  }
  
  public int getCaps()
  {
    return caps;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("ContextCapabilities: adapter=" + adapterId + ", caps=");
    if (caps == 0)
    {
      localStringBuffer.append("CAPS_EMPTY");
    }
    else
    {
      if ((caps & 0x2) != 0) {
        localStringBuffer.append("CAPS_RT_PLAIN_ALPHA|");
      }
      if ((caps & 0x4) != 0) {
        localStringBuffer.append("CAPS_RT_TEXTURE_ALPHA|");
      }
      if ((caps & 0x8) != 0) {
        localStringBuffer.append("CAPS_RT_TEXTURE_OPAQUE|");
      }
      if ((caps & 0x10) != 0) {
        localStringBuffer.append("CAPS_MULTITEXTURE|");
      }
      if ((caps & 0x20) != 0) {
        localStringBuffer.append("CAPS_TEXNONPOW2|");
      }
      if ((caps & 0x40) != 0) {
        localStringBuffer.append("CAPS_TEXNONSQUARE|");
      }
      if ((caps & 0x80) != 0) {
        localStringBuffer.append("CAPS_PS20|");
      }
      if ((caps & 0x100) != 0) {
        localStringBuffer.append("CAPS_PS30|");
      }
    }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\hw\ContextCapabilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */