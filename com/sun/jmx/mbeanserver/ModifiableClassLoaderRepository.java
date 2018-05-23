package com.sun.jmx.mbeanserver;

import javax.management.ObjectName;
import javax.management.loading.ClassLoaderRepository;

public abstract interface ModifiableClassLoaderRepository
  extends ClassLoaderRepository
{
  public abstract void addClassLoader(ClassLoader paramClassLoader);
  
  public abstract void removeClassLoader(ClassLoader paramClassLoader);
  
  public abstract void addClassLoader(ObjectName paramObjectName, ClassLoader paramClassLoader);
  
  public abstract void removeClassLoader(ObjectName paramObjectName);
  
  public abstract ClassLoader getClassLoader(ObjectName paramObjectName);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\ModifiableClassLoaderRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */