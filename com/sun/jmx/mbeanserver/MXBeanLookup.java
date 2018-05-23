package com.sun.jmx.mbeanserver;

import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.util.Map;
import javax.management.InstanceAlreadyExistsException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.openmbean.OpenDataException;

public class MXBeanLookup
{
  private static final ThreadLocal<MXBeanLookup> currentLookup = new ThreadLocal();
  private final MBeanServerConnection mbsc;
  private final WeakIdentityHashMap<Object, ObjectName> mxbeanToObjectName = WeakIdentityHashMap.make();
  private final Map<ObjectName, WeakReference<Object>> objectNameToProxy = Util.newMap();
  private static final WeakIdentityHashMap<MBeanServerConnection, WeakReference<MXBeanLookup>> mbscToLookup = WeakIdentityHashMap.make();
  
  private MXBeanLookup(MBeanServerConnection paramMBeanServerConnection)
  {
    mbsc = paramMBeanServerConnection;
  }
  
  static MXBeanLookup lookupFor(MBeanServerConnection paramMBeanServerConnection)
  {
    synchronized (mbscToLookup)
    {
      WeakReference localWeakReference = (WeakReference)mbscToLookup.get(paramMBeanServerConnection);
      MXBeanLookup localMXBeanLookup = localWeakReference == null ? null : (MXBeanLookup)localWeakReference.get();
      if (localMXBeanLookup == null)
      {
        localMXBeanLookup = new MXBeanLookup(paramMBeanServerConnection);
        mbscToLookup.put(paramMBeanServerConnection, new WeakReference(localMXBeanLookup));
      }
      return localMXBeanLookup;
    }
  }
  
  synchronized <T> T objectNameToMXBean(ObjectName paramObjectName, Class<T> paramClass)
  {
    WeakReference localWeakReference = (WeakReference)objectNameToProxy.get(paramObjectName);
    if (localWeakReference != null)
    {
      localObject = localWeakReference.get();
      if (paramClass.isInstance(localObject)) {
        return (T)paramClass.cast(localObject);
      }
    }
    Object localObject = JMX.newMXBeanProxy(mbsc, paramObjectName, paramClass);
    objectNameToProxy.put(paramObjectName, new WeakReference(localObject));
    return (T)localObject;
  }
  
  synchronized ObjectName mxbeanToObjectName(Object paramObject)
    throws OpenDataException
  {
    String str;
    if ((paramObject instanceof Proxy))
    {
      localObject = Proxy.getInvocationHandler(paramObject);
      if ((localObject instanceof MBeanServerInvocationHandler))
      {
        MBeanServerInvocationHandler localMBeanServerInvocationHandler = (MBeanServerInvocationHandler)localObject;
        if (localMBeanServerInvocationHandler.getMBeanServerConnection().equals(mbsc)) {
          return localMBeanServerInvocationHandler.getObjectName();
        }
        str = "proxy for a different MBeanServer";
      }
      else
      {
        str = "not a JMX proxy";
      }
    }
    else
    {
      localObject = (ObjectName)mxbeanToObjectName.get(paramObject);
      if (localObject != null) {
        return (ObjectName)localObject;
      }
      str = "not an MXBean registered in this MBeanServer";
    }
    Object localObject = "object of type " + paramObject.getClass().getName();
    throw new OpenDataException("Could not convert " + (String)localObject + " to an ObjectName: " + str);
  }
  
  synchronized void addReference(ObjectName paramObjectName, Object paramObject)
    throws InstanceAlreadyExistsException
  {
    ObjectName localObjectName = (ObjectName)mxbeanToObjectName.get(paramObject);
    if (localObjectName != null)
    {
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("jmx.mxbean.multiname"));
      if (!"true".equalsIgnoreCase(str)) {
        throw new InstanceAlreadyExistsException("MXBean already registered with name " + localObjectName);
      }
    }
    mxbeanToObjectName.put(paramObject, paramObjectName);
  }
  
  synchronized boolean removeReference(ObjectName paramObjectName, Object paramObject)
  {
    if (paramObjectName.equals(mxbeanToObjectName.get(paramObject)))
    {
      mxbeanToObjectName.remove(paramObject);
      return true;
    }
    return false;
  }
  
  static MXBeanLookup getLookup()
  {
    return (MXBeanLookup)currentLookup.get();
  }
  
  static void setLookup(MXBeanLookup paramMXBeanLookup)
  {
    currentLookup.set(paramMXBeanLookup);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\MXBeanLookup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */