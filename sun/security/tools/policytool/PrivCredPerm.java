package sun.security.tools.policytool;

class PrivCredPerm
  extends Perm
{
  public PrivCredPerm()
  {
    super("PrivateCredentialPermission", "javax.security.auth.PrivateCredentialPermission", new String[0], new String[] { "read" });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\PrivCredPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */