package sun.security.tools.policytool;

class ServicePerm
  extends Perm
{
  public ServicePerm()
  {
    super("ServicePermission", "javax.security.auth.kerberos.ServicePermission", new String[0], new String[] { "initiate", "accept" });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\ServicePerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */