package com.sun.jmx.remote.util;

import sun.reflect.misc.ReflectUtil;

public class OrderClassLoaders
  extends ClassLoader
{
  private ClassLoader cl2;
  
  public OrderClassLoaders(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
  {
    super(paramClassLoader1);
    cl2 = paramClassLoader2;
  }
  
  protected Class<?> loadClass(String paramString, boolean paramBoolean)
    throws ClassNotFoundException
  {
    ReflectUtil.checkPackageAccess(paramString);
    try
    {
      return super.loadClass(paramString, paramBoolean);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (cl2 != null) {
        return cl2.loadClass(paramString);
      }
      throw localClassNotFoundException;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\util\OrderClassLoaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */