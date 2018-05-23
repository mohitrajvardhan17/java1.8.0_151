package java.rmi.dgc;

import java.io.Serializable;
import java.rmi.server.UID;
import java.security.SecureRandom;

public final class VMID
  implements Serializable
{
  private static final byte[] randomBytes;
  private byte[] addr = randomBytes;
  private UID uid = new UID();
  private static final long serialVersionUID = -538642295484486218L;
  
  public VMID() {}
  
  @Deprecated
  public static boolean isUnique()
  {
    return true;
  }
  
  public int hashCode()
  {
    return uid.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof VMID))
    {
      VMID localVMID = (VMID)paramObject;
      if (!uid.equals(uid)) {
        return false;
      }
      if (((addr == null ? 1 : 0) ^ (addr == null ? 1 : 0)) != 0) {
        return false;
      }
      if (addr != null)
      {
        if (addr.length != addr.length) {
          return false;
        }
        for (int i = 0; i < addr.length; i++) {
          if (addr[i] != addr[i]) {
            return false;
          }
        }
      }
      return true;
    }
    return false;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (addr != null) {
      for (int i = 0; i < addr.length; i++)
      {
        int j = addr[i] & 0xFF;
        localStringBuffer.append((j < 16 ? "0" : "") + Integer.toString(j, 16));
      }
    }
    localStringBuffer.append(':');
    localStringBuffer.append(uid.toString());
    return localStringBuffer.toString();
  }
  
  static
  {
    SecureRandom localSecureRandom = new SecureRandom();
    byte[] arrayOfByte = new byte[8];
    localSecureRandom.nextBytes(arrayOfByte);
    randomBytes = arrayOfByte;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\dgc\VMID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */