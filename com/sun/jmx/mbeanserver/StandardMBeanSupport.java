package com.sun.jmx.mbeanserver;

import java.lang.reflect.Method;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class StandardMBeanSupport
  extends MBeanSupport<Method>
{
  public <T> StandardMBeanSupport(T paramT, Class<T> paramClass)
    throws NotCompliantMBeanException
  {
    super(paramT, paramClass);
  }
  
  MBeanIntrospector<Method> getMBeanIntrospector()
  {
    return StandardMBeanIntrospector.getInstance();
  }
  
  Object getCookie()
  {
    return null;
  }
  
  public void register(MBeanServer paramMBeanServer, ObjectName paramObjectName) {}
  
  public void unregister() {}
  
  public MBeanInfo getMBeanInfo()
  {
    MBeanInfo localMBeanInfo = super.getMBeanInfo();
    Class localClass = getResource().getClass();
    if (StandardMBeanIntrospector.isDefinitelyImmutableInfo(localClass)) {
      return localMBeanInfo;
    }
    return new MBeanInfo(localMBeanInfo.getClassName(), localMBeanInfo.getDescription(), localMBeanInfo.getAttributes(), localMBeanInfo.getConstructors(), localMBeanInfo.getOperations(), MBeanIntrospector.findNotifications(getResource()), localMBeanInfo.getDescriptor());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\StandardMBeanSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */