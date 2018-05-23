package javax.security.auth.kerberos;

import sun.security.krb5.JavaxSecurityAuthKerberosAccess;

class JavaxSecurityAuthKerberosAccessImpl
  implements JavaxSecurityAuthKerberosAccess
{
  JavaxSecurityAuthKerberosAccessImpl() {}
  
  public sun.security.krb5.internal.ktab.KeyTab keyTabTakeSnapshot(KeyTab paramKeyTab)
  {
    return paramKeyTab.takeSnapshot();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\kerberos\JavaxSecurityAuthKerberosAccessImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */