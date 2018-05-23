package com.sun.jmx.remote.util;

import javax.management.loading.ClassLoaderRepository;

public class ClassLoaderWithRepository
  extends ClassLoader
{
  private ClassLoaderRepository repository;
  private ClassLoader cl2;
  
  public ClassLoaderWithRepository(ClassLoaderRepository paramClassLoaderRepository, ClassLoader paramClassLoader)
  {
    if (paramClassLoaderRepository == null) {
      throw new IllegalArgumentException("Null ClassLoaderRepository object.");
    }
    repository = paramClassLoaderRepository;
    cl2 = paramClassLoader;
  }
  
  protected Class<?> findClass(String paramString)
    throws ClassNotFoundException
  {
    Class localClass;
    try
    {
      localClass = repository.loadClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (cl2 != null) {
        return cl2.loadClass(paramString);
      }
      throw localClassNotFoundException;
    }
    if (!localClass.getName().equals(paramString))
    {
      if (cl2 != null) {
        return cl2.loadClass(paramString);
      }
      throw new ClassNotFoundException(paramString);
    }
    return localClass;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\util\ClassLoaderWithRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */