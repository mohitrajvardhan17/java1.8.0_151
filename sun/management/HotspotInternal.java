package sun.management;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class HotspotInternal
  implements HotspotInternalMBean, MBeanRegistration
{
  private static final String HOTSPOT_INTERNAL_MBEAN_NAME = "sun.management:type=HotspotInternal";
  private static ObjectName objName = Util.newObjectName("sun.management:type=HotspotInternal");
  private MBeanServer server = null;
  
  public HotspotInternal() {}
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    ManagementFactoryHelper.registerInternalMBeans(paramMBeanServer);
    server = paramMBeanServer;
    return objName;
  }
  
  public void postRegister(Boolean paramBoolean) {}
  
  public void preDeregister()
    throws Exception
  {
    ManagementFactoryHelper.unregisterInternalMBeans(server);
  }
  
  public void postDeregister() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\HotspotInternal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */