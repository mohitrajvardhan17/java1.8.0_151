package sun.security.tools.policytool;

class MBeanPerm
  extends Perm
{
  public MBeanPerm()
  {
    super("MBeanPermission", "javax.management.MBeanPermission", new String[0], new String[] { "addNotificationListener", "getAttribute", "getClassLoader", "getClassLoaderFor", "getClassLoaderRepository", "getDomains", "getMBeanInfo", "getObjectInstance", "instantiate", "invoke", "isInstanceOf", "queryMBeans", "queryNames", "registerMBean", "removeNotificationListener", "setAttribute", "unregisterMBean" });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\MBeanPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */