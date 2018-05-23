package sun.security.tools.policytool;

class MBeanTrustPerm
  extends Perm
{
  public MBeanTrustPerm()
  {
    super("MBeanTrustPermission", "javax.management.MBeanTrustPermission", new String[] { "register" }, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\MBeanTrustPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */