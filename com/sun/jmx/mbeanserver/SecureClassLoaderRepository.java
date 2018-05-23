package com.sun.jmx.mbeanserver;

import javax.management.loading.ClassLoaderRepository;

final class SecureClassLoaderRepository
  implements ClassLoaderRepository
{
  private final ClassLoaderRepository clr;
  
  public SecureClassLoaderRepository(ClassLoaderRepository paramClassLoaderRepository)
  {
    clr = paramClassLoaderRepository;
  }
  
  public final Class<?> loadClass(String paramString)
    throws ClassNotFoundException
  {
    return clr.loadClass(paramString);
  }
  
  public final Class<?> loadClassWithout(ClassLoader paramClassLoader, String paramString)
    throws ClassNotFoundException
  {
    return clr.loadClassWithout(paramClassLoader, paramString);
  }
  
  public final Class<?> loadClassBefore(ClassLoader paramClassLoader, String paramString)
    throws ClassNotFoundException
  {
    return clr.loadClassBefore(paramClassLoader, paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\SecureClassLoaderRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */