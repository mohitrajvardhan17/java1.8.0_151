package sun.security.krb5;

import javax.security.auth.kerberos.KeyTab;
import sun.misc.Unsafe;

public class KerberosSecrets
{
  private static final Unsafe unsafe = ;
  private static JavaxSecurityAuthKerberosAccess javaxSecurityAuthKerberosAccess;
  
  public KerberosSecrets() {}
  
  public static void setJavaxSecurityAuthKerberosAccess(JavaxSecurityAuthKerberosAccess paramJavaxSecurityAuthKerberosAccess)
  {
    javaxSecurityAuthKerberosAccess = paramJavaxSecurityAuthKerberosAccess;
  }
  
  public static JavaxSecurityAuthKerberosAccess getJavaxSecurityAuthKerberosAccess()
  {
    if (javaxSecurityAuthKerberosAccess == null) {
      unsafe.ensureClassInitialized(KeyTab.class);
    }
    return javaxSecurityAuthKerberosAccess;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\KerberosSecrets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */