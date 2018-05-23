package javax.management;

import com.sun.jmx.mbeanserver.JmxMBeanServer;

public class MBeanServerBuilder
{
  public MBeanServerBuilder() {}
  
  public MBeanServerDelegate newMBeanServerDelegate()
  {
    return JmxMBeanServer.newMBeanServerDelegate();
  }
  
  public MBeanServer newMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate)
  {
    return JmxMBeanServer.newMBeanServer(paramString, paramMBeanServer, paramMBeanServerDelegate, false);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\MBeanServerBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */