package sun.security.tools.policytool;

class PropPerm
  extends Perm
{
  public PropPerm()
  {
    super("PropertyPermission", "java.util.PropertyPermission", new String[0], new String[] { "read", "write" });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\PropPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */