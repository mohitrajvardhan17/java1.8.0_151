package com.sun.management.jmx;

import javax.management.MBeanInfo;
import javax.management.NotCompliantMBeanException;

@Deprecated
public class Introspector
{
  public Introspector() {}
  
  @Deprecated
  public static synchronized MBeanInfo testCompliance(Class paramClass)
    throws NotCompliantMBeanException
  {
    return com.sun.jmx.mbeanserver.Introspector.testCompliance(paramClass);
  }
  
  @Deprecated
  public static synchronized Class getMBeanInterface(Class paramClass)
  {
    return com.sun.jmx.mbeanserver.Introspector.getMBeanInterface(paramClass);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\management\jmx\Introspector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */