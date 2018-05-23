package com.sun.jmx.mbeanserver;

import javax.management.MBeanServer;
import javax.management.MBeanServerBuilder;
import javax.management.MBeanServerDelegate;

public class JmxMBeanServerBuilder
  extends MBeanServerBuilder
{
  public JmxMBeanServerBuilder() {}
  
  public MBeanServerDelegate newMBeanServerDelegate()
  {
    return JmxMBeanServer.newMBeanServerDelegate();
  }
  
  public MBeanServer newMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate)
  {
    return JmxMBeanServer.newMBeanServer(paramString, paramMBeanServer, paramMBeanServerDelegate, true);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\JmxMBeanServerBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */