package sun.security.tools.policytool;

class MBeanSvrPerm
  extends Perm
{
  public MBeanSvrPerm()
  {
    super("MBeanServerPermission", "javax.management.MBeanServerPermission", new String[] { "createMBeanServer", "findMBeanServer", "newMBeanServer", "releaseMBeanServer" }, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\MBeanSvrPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */