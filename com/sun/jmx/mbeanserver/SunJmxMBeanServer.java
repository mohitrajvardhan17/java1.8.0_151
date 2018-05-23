package com.sun.jmx.mbeanserver;

import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;

public abstract interface SunJmxMBeanServer
  extends MBeanServer
{
  public abstract MBeanInstantiator getMBeanInstantiator();
  
  public abstract boolean interceptorsEnabled();
  
  public abstract MBeanServer getMBeanServerInterceptor();
  
  public abstract void setMBeanServerInterceptor(MBeanServer paramMBeanServer);
  
  public abstract MBeanServerDelegate getMBeanServerDelegate();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\SunJmxMBeanServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */