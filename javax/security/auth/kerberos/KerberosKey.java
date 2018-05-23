package javax.security.auth.kerberos;

import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

public class KerberosKey
  implements SecretKey, Destroyable
{
  private static final long serialVersionUID = -4625402278148246993L;
  private KerberosPrincipal principal;
  private int versionNum;
  private KeyImpl key;
  private transient boolean destroyed = false;
  
  public KerberosKey(KerberosPrincipal paramKerberosPrincipal, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    principal = paramKerberosPrincipal;
    versionNum = paramInt2;
    key = new KeyImpl(paramArrayOfByte, paramInt1);
  }
  
  public KerberosKey(KerberosPrincipal paramKerberosPrincipal, char[] paramArrayOfChar, String paramString)
  {
    principal = paramKerberosPrincipal;
    key = new KeyImpl(paramKerberosPrincipal, paramArrayOfChar, paramString);
  }
  
  public final KerberosPrincipal getPrincipal()
  {
    if (destroyed) {
      throw new IllegalStateException("This key is no longer valid");
    }
    return principal;
  }
  
  public final int getVersionNumber()
  {
    if (destroyed) {
      throw new IllegalStateException("This key is no longer valid");
    }
    return versionNum;
  }
  
  public final int getKeyType()
  {
    if (destroyed) {
      throw new IllegalStateException("This key is no longer valid");
    }
    return key.getKeyType();
  }
  
  public final String getAlgorithm()
  {
    if (destroyed) {
      throw new IllegalStateException("This key is no longer valid");
    }
    return key.getAlgorithm();
  }
  
  public final String getFormat()
  {
    if (destroyed) {
      throw new IllegalStateException("This key is no longer valid");
    }
    return key.getFormat();
  }
  
  public final byte[] getEncoded()
  {
    if (destroyed) {
      throw new IllegalStateException("This key is no longer valid");
    }
    return key.getEncoded();
  }
  
  public void destroy()
    throws DestroyFailedException
  {
    if (!destroyed)
    {
      key.destroy();
      principal = null;
      destroyed = true;
    }
  }
  
  public boolean isDestroyed()
  {
    return destroyed;
  }
  
  public String toString()
  {
    if (destroyed) {
      return "Destroyed Principal";
    }
    return "Kerberos Principal " + principal.toString() + "Key Version " + versionNum + "key " + key.toString();
  }
  
  public int hashCode()
  {
    int i = 17;
    if (isDestroyed()) {
      return i;
    }
    i = 37 * i + Arrays.hashCode(getEncoded());
    i = 37 * i + getKeyType();
    if (principal != null) {
      i = 37 * i + principal.hashCode();
    }
    return i * 37 + versionNum;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof KerberosKey)) {
      return false;
    }
    KerberosKey localKerberosKey = (KerberosKey)paramObject;
    if ((isDestroyed()) || (localKerberosKey.isDestroyed())) {
      return false;
    }
    if ((versionNum != localKerberosKey.getVersionNumber()) || (getKeyType() != localKerberosKey.getKeyType()) || (!Arrays.equals(getEncoded(), localKerberosKey.getEncoded()))) {
      return false;
    }
    if (principal == null)
    {
      if (localKerberosKey.getPrincipal() != null) {
        return false;
      }
    }
    else if (!principal.equals(localKerberosKey.getPrincipal())) {
      return false;
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\kerberos\KerberosKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */