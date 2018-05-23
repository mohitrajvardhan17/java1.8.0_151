package com.sun.jmx.mbeanserver;

import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public abstract interface DynamicMBean2
  extends DynamicMBean
{
  public abstract Object getResource();
  
  public abstract String getClassName();
  
  public abstract void preRegister2(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception;
  
  public abstract void registerFailed();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\DynamicMBean2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */