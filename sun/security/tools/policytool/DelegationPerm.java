package sun.security.tools.policytool;

class DelegationPerm
  extends Perm
{
  public DelegationPerm()
  {
    super("DelegationPermission", "javax.security.auth.kerberos.DelegationPermission", new String[0], null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\DelegationPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */