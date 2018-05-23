package sun.security.tools.policytool;

class MgmtPerm
  extends Perm
{
  public MgmtPerm()
  {
    super("ManagementPermission", "java.lang.management.ManagementPermission", new String[] { "control", "monitor" }, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\MgmtPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */