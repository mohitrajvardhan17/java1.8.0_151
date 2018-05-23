package com.sun.jmx.mbeanserver;

import com.sun.jmx.defaults.JmxProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.MBeanPermission;
import javax.management.ObjectName;
import javax.management.loading.PrivateClassLoader;
import sun.reflect.misc.ReflectUtil;

final class ClassLoaderRepositorySupport
  implements ModifiableClassLoaderRepository
{
  private static final LoaderEntry[] EMPTY_LOADER_ARRAY = new LoaderEntry[0];
  private LoaderEntry[] loaders = EMPTY_LOADER_ARRAY;
  private final Map<String, List<ClassLoader>> search = new Hashtable(10);
  private final Map<ObjectName, ClassLoader> loadersWithNames = new Hashtable(10);
  
  ClassLoaderRepositorySupport() {}
  
  private synchronized boolean add(ObjectName paramObjectName, ClassLoader paramClassLoader)
  {
    ArrayList localArrayList = new ArrayList(Arrays.asList(loaders));
    localArrayList.add(new LoaderEntry(paramObjectName, paramClassLoader));
    loaders = ((LoaderEntry[])localArrayList.toArray(EMPTY_LOADER_ARRAY));
    return true;
  }
  
  private synchronized boolean remove(ObjectName paramObjectName, ClassLoader paramClassLoader)
  {
    int i = loaders.length;
    for (int j = 0; j < i; j++)
    {
      LoaderEntry localLoaderEntry = loaders[j];
      boolean bool = paramObjectName == null ? false : paramClassLoader == loader ? true : paramObjectName.equals(name);
      if (bool)
      {
        LoaderEntry[] arrayOfLoaderEntry = new LoaderEntry[i - 1];
        System.arraycopy(loaders, 0, arrayOfLoaderEntry, 0, j);
        System.arraycopy(loaders, j + 1, arrayOfLoaderEntry, j, i - 1 - j);
        loaders = arrayOfLoaderEntry;
        return true;
      }
    }
    return false;
  }
  
  public final Class<?> loadClass(String paramString)
    throws ClassNotFoundException
  {
    return loadClass(loaders, paramString, null, null);
  }
  
  public final Class<?> loadClassWithout(ClassLoader paramClassLoader, String paramString)
    throws ClassNotFoundException
  {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "loadClassWithout", paramString + " without " + paramClassLoader);
    }
    if (paramClassLoader == null) {
      return loadClass(loaders, paramString, null, null);
    }
    startValidSearch(paramClassLoader, paramString);
    try
    {
      Class localClass = loadClass(loaders, paramString, paramClassLoader, null);
      return localClass;
    }
    finally
    {
      stopValidSearch(paramClassLoader, paramString);
    }
  }
  
  public final Class<?> loadClassBefore(ClassLoader paramClassLoader, String paramString)
    throws ClassNotFoundException
  {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "loadClassBefore", paramString + " before " + paramClassLoader);
    }
    if (paramClassLoader == null) {
      return loadClass(loaders, paramString, null, null);
    }
    startValidSearch(paramClassLoader, paramString);
    try
    {
      Class localClass = loadClass(loaders, paramString, null, paramClassLoader);
      return localClass;
    }
    finally
    {
      stopValidSearch(paramClassLoader, paramString);
    }
  }
  
  private Class<?> loadClass(LoaderEntry[] paramArrayOfLoaderEntry, String paramString, ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
    throws ClassNotFoundException
  {
    ReflectUtil.checkPackageAccess(paramString);
    int i = paramArrayOfLoaderEntry.length;
    int j = 0;
    while (j < i) {
      try
      {
        ClassLoader localClassLoader = loader;
        if (localClassLoader == null) {
          return Class.forName(paramString, false, null);
        }
        if (localClassLoader != paramClassLoader1)
        {
          if (localClassLoader == paramClassLoader2) {
            break;
          }
          if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "loadClass", "Trying loader = " + localClassLoader);
          }
          return Class.forName(paramString, false, localClassLoader);
        }
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        j++;
      }
    }
    throw new ClassNotFoundException(paramString);
  }
  
  private synchronized void startValidSearch(ClassLoader paramClassLoader, String paramString)
    throws ClassNotFoundException
  {
    Object localObject = (List)search.get(paramString);
    if ((localObject != null) && (((List)localObject).contains(paramClassLoader)))
    {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "startValidSearch", "Already requested loader = " + paramClassLoader + " class = " + paramString);
      }
      throw new ClassNotFoundException(paramString);
    }
    if (localObject == null)
    {
      localObject = new ArrayList(1);
      search.put(paramString, localObject);
    }
    ((List)localObject).add(paramClassLoader);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "startValidSearch", "loader = " + paramClassLoader + " class = " + paramString);
    }
  }
  
  private synchronized void stopValidSearch(ClassLoader paramClassLoader, String paramString)
  {
    List localList = (List)search.get(paramString);
    if (localList != null)
    {
      localList.remove(paramClassLoader);
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "stopValidSearch", "loader = " + paramClassLoader + " class = " + paramString);
      }
    }
  }
  
  public final void addClassLoader(ClassLoader paramClassLoader)
  {
    add(null, paramClassLoader);
  }
  
  public final void removeClassLoader(ClassLoader paramClassLoader)
  {
    remove(null, paramClassLoader);
  }
  
  public final synchronized void addClassLoader(ObjectName paramObjectName, ClassLoader paramClassLoader)
  {
    loadersWithNames.put(paramObjectName, paramClassLoader);
    if (!(paramClassLoader instanceof PrivateClassLoader)) {
      add(paramObjectName, paramClassLoader);
    }
  }
  
  public final synchronized void removeClassLoader(ObjectName paramObjectName)
  {
    ClassLoader localClassLoader = (ClassLoader)loadersWithNames.remove(paramObjectName);
    if (!(localClassLoader instanceof PrivateClassLoader)) {
      remove(paramObjectName, localClassLoader);
    }
  }
  
  public final ClassLoader getClassLoader(ObjectName paramObjectName)
  {
    ClassLoader localClassLoader = (ClassLoader)loadersWithNames.get(paramObjectName);
    if (localClassLoader != null)
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null)
      {
        MBeanPermission localMBeanPermission = new MBeanPermission(localClassLoader.getClass().getName(), null, paramObjectName, "getClassLoader");
        localSecurityManager.checkPermission(localMBeanPermission);
      }
    }
    return localClassLoader;
  }
  
  private static class LoaderEntry
  {
    ObjectName name;
    ClassLoader loader;
    
    LoaderEntry(ObjectName paramObjectName, ClassLoader paramClassLoader)
    {
      name = paramObjectName;
      loader = paramClassLoader;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\ClassLoaderRepositorySupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */