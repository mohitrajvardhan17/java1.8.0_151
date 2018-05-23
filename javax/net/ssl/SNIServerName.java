package javax.net.ssl;

import java.util.Arrays;

public abstract class SNIServerName
{
  private final int type;
  private final byte[] encoded;
  private static final char[] HEXES = "0123456789ABCDEF".toCharArray();
  
  protected SNIServerName(int paramInt, byte[] paramArrayOfByte)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Server name type cannot be less than zero");
    }
    if (paramInt > 255) {
      throw new IllegalArgumentException("Server name type cannot be greater than 255");
    }
    type = paramInt;
    if (paramArrayOfByte == null) {
      throw new NullPointerException("Server name encoded value cannot be null");
    }
    encoded = ((byte[])paramArrayOfByte.clone());
  }
  
  public final int getType()
  {
    return type;
  }
  
  public final byte[] getEncoded()
  {
    return (byte[])encoded.clone();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    SNIServerName localSNIServerName = (SNIServerName)paramObject;
    return (type == type) && (Arrays.equals(encoded, encoded));
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 31 * i + type;
    i = 31 * i + Arrays.hashCode(encoded);
    return i;
  }
  
  public String toString()
  {
    if (type == 0) {
      return "type=host_name (0), value=" + toHexString(encoded);
    }
    return "type=(" + type + "), value=" + toHexString(encoded);
  }
  
  private static String toHexString(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length == 0) {
      return "(empty)";
    }
    StringBuilder localStringBuilder = new StringBuilder(paramArrayOfByte.length * 3 - 1);
    int i = 1;
    for (int m : paramArrayOfByte)
    {
      if (i != 0) {
        i = 0;
      } else {
        localStringBuilder.append(':');
      }
      int n = m & 0xFF;
      localStringBuilder.append(HEXES[(n >>> 4)]);
      localStringBuilder.append(HEXES[(n & 0xF)]);
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\SNIServerName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */