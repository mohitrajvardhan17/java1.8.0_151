package com.sun.jmx.mbeanserver;

import java.util.Iterator;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import sun.reflect.misc.ReflectUtil;

public abstract class MBeanSupport<M>
  implements DynamicMBean2, MBeanRegistration
{
  private final MBeanInfo mbeanInfo;
  private final Object resource;
  private final PerInterface<M> perInterface;
  
  <T> MBeanSupport(T paramT, Class<T> paramClass)
    throws NotCompliantMBeanException
  {
    if (paramClass == null) {
      throw new NotCompliantMBeanException("Null MBean interface");
    }
    if (!paramClass.isInstance(paramT))
    {
      localObject = "Resource class " + paramT.getClass().getName() + " is not an instance of " + paramClass.getName();
      throw new NotCompliantMBeanException((String)localObject);
    }
    ReflectUtil.checkPackageAccess(paramClass);
    resource = paramT;
    Object localObject = getMBeanIntrospector();
    perInterface = ((MBeanIntrospector)localObject).getPerInterface(paramClass);
    mbeanInfo = ((MBeanIntrospector)localObject).getMBeanInfo(paramT, perInterface);
  }
  
  abstract MBeanIntrospector<M> getMBeanIntrospector();
  
  abstract Object getCookie();
  
  public final boolean isMXBean()
  {
    return perInterface.isMXBean();
  }
  
  public abstract void register(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception;
  
  public abstract void unregister();
  
  public final ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    if ((resource instanceof MBeanRegistration)) {
      paramObjectName = ((MBeanRegistration)resource).preRegister(paramMBeanServer, paramObjectName);
    }
    return paramObjectName;
  }
  
  public final void preRegister2(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    register(paramMBeanServer, paramObjectName);
  }
  
  public final void registerFailed()
  {
    unregister();
  }
  
  public final void postRegister(Boolean paramBoolean)
  {
    if ((resource instanceof MBeanRegistration)) {
      ((MBeanRegistration)resource).postRegister(paramBoolean);
    }
  }
  
  public final void preDeregister()
    throws Exception
  {
    if ((resource instanceof MBeanRegistration)) {
      ((MBeanRegistration)resource).preDeregister();
    }
  }
  
  /* Error */
  public final void postDeregister()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 168	com/sun/jmx/mbeanserver/MBeanSupport:unregister	()V
    //   4: aload_0
    //   5: getfield 164	com/sun/jmx/mbeanserver/MBeanSupport:resource	Ljava/lang/Object;
    //   8: instanceof 96
    //   11: ifeq +43 -> 54
    //   14: aload_0
    //   15: getfield 164	com/sun/jmx/mbeanserver/MBeanSupport:resource	Ljava/lang/Object;
    //   18: checkcast 96	javax/management/MBeanRegistration
    //   21: invokeinterface 197 1 0
    //   26: goto +28 -> 54
    //   29: astore_1
    //   30: aload_0
    //   31: getfield 164	com/sun/jmx/mbeanserver/MBeanSupport:resource	Ljava/lang/Object;
    //   34: instanceof 96
    //   37: ifeq +15 -> 52
    //   40: aload_0
    //   41: getfield 164	com/sun/jmx/mbeanserver/MBeanSupport:resource	Ljava/lang/Object;
    //   44: checkcast 96	javax/management/MBeanRegistration
    //   47: invokeinterface 197 1 0
    //   52: aload_1
    //   53: athrow
    //   54: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	MBeanSupport
    //   29	24	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	4	29	finally
  }
  
  public final Object getAttribute(String paramString)
    throws AttributeNotFoundException, MBeanException, ReflectionException
  {
    return perInterface.getAttribute(resource, paramString, getCookie());
  }
  
  public final AttributeList getAttributes(String[] paramArrayOfString)
  {
    AttributeList localAttributeList = new AttributeList(paramArrayOfString.length);
    for (String str : paramArrayOfString) {
      try
      {
        Object localObject = getAttribute(str);
        localAttributeList.add(new Attribute(str, localObject));
      }
      catch (Exception localException) {}
    }
    return localAttributeList;
  }
  
  public final void setAttribute(Attribute paramAttribute)
    throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
  {
    String str = paramAttribute.getName();
    Object localObject = paramAttribute.getValue();
    perInterface.setAttribute(resource, str, localObject, getCookie());
  }
  
  public final AttributeList setAttributes(AttributeList paramAttributeList)
  {
    AttributeList localAttributeList = new AttributeList(paramAttributeList.size());
    Iterator localIterator = paramAttributeList.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      Attribute localAttribute = (Attribute)localObject;
      try
      {
        setAttribute(localAttribute);
        localAttributeList.add(new Attribute(localAttribute.getName(), localAttribute.getValue()));
      }
      catch (Exception localException) {}
    }
    return localAttributeList;
  }
  
  public final Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws MBeanException, ReflectionException
  {
    return perInterface.invoke(resource, paramString, paramArrayOfObject, paramArrayOfString, getCookie());
  }
  
  public MBeanInfo getMBeanInfo()
  {
    return mbeanInfo;
  }
  
  public final String getClassName()
  {
    return resource.getClass().getName();
  }
  
  public final Object getResource()
  {
    return resource;
  }
  
  public final Class<?> getMBeanInterface()
  {
    return perInterface.getMBeanInterface();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\MBeanSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */