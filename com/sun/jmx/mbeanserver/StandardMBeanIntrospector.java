package com.sun.jmx.mbeanserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.WeakHashMap;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationBroadcasterSupport;
import sun.reflect.misc.MethodUtil;

class StandardMBeanIntrospector
  extends MBeanIntrospector<Method>
{
  private static final StandardMBeanIntrospector instance = new StandardMBeanIntrospector();
  private static final WeakHashMap<Class<?>, Boolean> definitelyImmutable = new WeakHashMap();
  private static final MBeanIntrospector.PerInterfaceMap<Method> perInterfaceMap = new MBeanIntrospector.PerInterfaceMap();
  private static final MBeanIntrospector.MBeanInfoMap mbeanInfoMap = new MBeanIntrospector.MBeanInfoMap();
  
  StandardMBeanIntrospector() {}
  
  static StandardMBeanIntrospector getInstance()
  {
    return instance;
  }
  
  MBeanIntrospector.PerInterfaceMap<Method> getPerInterfaceMap()
  {
    return perInterfaceMap;
  }
  
  MBeanIntrospector.MBeanInfoMap getMBeanInfoMap()
  {
    return mbeanInfoMap;
  }
  
  MBeanAnalyzer<Method> getAnalyzer(Class<?> paramClass)
    throws NotCompliantMBeanException
  {
    return MBeanAnalyzer.analyzer(paramClass, this);
  }
  
  boolean isMXBean()
  {
    return false;
  }
  
  Method mFrom(Method paramMethod)
  {
    return paramMethod;
  }
  
  String getName(Method paramMethod)
  {
    return paramMethod.getName();
  }
  
  Type getGenericReturnType(Method paramMethod)
  {
    return paramMethod.getGenericReturnType();
  }
  
  Type[] getGenericParameterTypes(Method paramMethod)
  {
    return paramMethod.getGenericParameterTypes();
  }
  
  String[] getSignature(Method paramMethod)
  {
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    String[] arrayOfString = new String[arrayOfClass.length];
    for (int i = 0; i < arrayOfClass.length; i++) {
      arrayOfString[i] = arrayOfClass[i].getName();
    }
    return arrayOfString;
  }
  
  void checkMethod(Method paramMethod) {}
  
  Object invokeM2(Method paramMethod, Object paramObject1, Object[] paramArrayOfObject, Object paramObject2)
    throws InvocationTargetException, IllegalAccessException, MBeanException
  {
    return MethodUtil.invoke(paramMethod, paramObject1, paramArrayOfObject);
  }
  
  boolean validParameter(Method paramMethod, Object paramObject1, int paramInt, Object paramObject2)
  {
    return isValidParameter(paramMethod, paramObject1, paramInt);
  }
  
  MBeanAttributeInfo getMBeanAttributeInfo(String paramString, Method paramMethod1, Method paramMethod2)
  {
    try
    {
      return new MBeanAttributeInfo(paramString, "Attribute exposed for management", paramMethod1, paramMethod2);
    }
    catch (IntrospectionException localIntrospectionException)
    {
      throw new RuntimeException(localIntrospectionException);
    }
  }
  
  MBeanOperationInfo getMBeanOperationInfo(String paramString, Method paramMethod)
  {
    return new MBeanOperationInfo("Operation exposed for management", paramMethod);
  }
  
  Descriptor getBasicMBeanDescriptor()
  {
    return ImmutableDescriptor.EMPTY_DESCRIPTOR;
  }
  
  Descriptor getMBeanDescriptor(Class<?> paramClass)
  {
    boolean bool = isDefinitelyImmutableInfo(paramClass);
    return new ImmutableDescriptor(new String[] { "mxbean=false", "immutableInfo=" + bool });
  }
  
  static boolean isDefinitelyImmutableInfo(Class<?> paramClass)
  {
    if (!NotificationBroadcaster.class.isAssignableFrom(paramClass)) {
      return true;
    }
    synchronized (definitelyImmutable)
    {
      Boolean localBoolean = (Boolean)definitelyImmutable.get(paramClass);
      if (localBoolean == null)
      {
        Class localClass = NotificationBroadcasterSupport.class;
        if (localClass.isAssignableFrom(paramClass)) {
          try
          {
            Method localMethod = paramClass.getMethod("getNotificationInfo", new Class[0]);
            localBoolean = Boolean.valueOf(localMethod.getDeclaringClass() == localClass);
          }
          catch (Exception localException)
          {
            return false;
          }
        } else {
          localBoolean = Boolean.valueOf(false);
        }
        definitelyImmutable.put(paramClass, localBoolean);
      }
      return localBoolean.booleanValue();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\StandardMBeanIntrospector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */