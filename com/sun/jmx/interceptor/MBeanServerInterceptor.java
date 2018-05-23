package com.sun.jmx.interceptor;

import java.io.ObjectInputStream;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.ReflectionException;
import javax.management.loading.ClassLoaderRepository;

public abstract interface MBeanServerInterceptor
  extends MBeanServer
{
  public abstract Object instantiate(String paramString)
    throws ReflectionException, MBeanException;
  
  public abstract Object instantiate(String paramString, ObjectName paramObjectName)
    throws ReflectionException, MBeanException, InstanceNotFoundException;
  
  public abstract Object instantiate(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws ReflectionException, MBeanException;
  
  public abstract Object instantiate(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws ReflectionException, MBeanException, InstanceNotFoundException;
  
  @Deprecated
  public abstract ObjectInputStream deserialize(ObjectName paramObjectName, byte[] paramArrayOfByte)
    throws InstanceNotFoundException, OperationsException;
  
  @Deprecated
  public abstract ObjectInputStream deserialize(String paramString, byte[] paramArrayOfByte)
    throws OperationsException, ReflectionException;
  
  @Deprecated
  public abstract ObjectInputStream deserialize(String paramString, ObjectName paramObjectName, byte[] paramArrayOfByte)
    throws InstanceNotFoundException, OperationsException, ReflectionException;
  
  public abstract ClassLoaderRepository getClassLoaderRepository();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\interceptor\MBeanServerInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */