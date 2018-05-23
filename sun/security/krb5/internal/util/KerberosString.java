package sun.security.krb5.internal.util;

import java.io.IOException;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import sun.security.util.DerValue;

public final class KerberosString
{
  public static final boolean MSNAME = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.krb5.msinterop.kstring"))).booleanValue();
  private final String s;
  
  public KerberosString(String paramString)
  {
    s = paramString;
  }
  
  public KerberosString(DerValue paramDerValue)
    throws IOException
  {
    if (tag != 27) {
      throw new IOException("KerberosString's tag is incorrect: " + tag);
    }
    s = new String(paramDerValue.getDataBytes(), MSNAME ? "UTF8" : "ASCII");
  }
  
  public String toString()
  {
    return s;
  }
  
  public DerValue toDerValue()
    throws IOException
  {
    return new DerValue((byte)27, s.getBytes(MSNAME ? "UTF8" : "ASCII"));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\util\KerberosString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */