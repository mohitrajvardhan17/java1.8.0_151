package com.sun.org.apache.xml.internal.security.algorithms;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

final class ClassLoaderUtils
{
  private static final Logger log = Logger.getLogger(ClassLoaderUtils.class.getName());
  
  private ClassLoaderUtils() {}
  
  static URL getResource(String paramString, Class<?> paramClass)
  {
    URL localURL = Thread.currentThread().getContextClassLoader().getResource(paramString);
    if ((localURL == null) && (paramString.startsWith("/"))) {
      localURL = Thread.currentThread().getContextClassLoader().getResource(paramString.substring(1));
    }
    ClassLoader localClassLoader1 = ClassLoaderUtils.class.getClassLoader();
    if (localClassLoader1 == null) {
      localClassLoader1 = ClassLoader.getSystemClassLoader();
    }
    if (localURL == null) {
      localURL = localClassLoader1.getResource(paramString);
    }
    if ((localURL == null) && (paramString.startsWith("/"))) {
      localURL = localClassLoader1.getResource(paramString.substring(1));
    }
    if (localURL == null)
    {
      ClassLoader localClassLoader2 = paramClass.getClassLoader();
      if (localClassLoader2 != null) {
        localURL = localClassLoader2.getResource(paramString);
      }
    }
    if (localURL == null) {
      localURL = paramClass.getResource(paramString);
    }
    if ((localURL == null) && (paramString != null) && (paramString.charAt(0) != '/')) {
      return getResource('/' + paramString, paramClass);
    }
    return localURL;
  }
  
  static List<URL> getResources(String paramString, Class<?> paramClass)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = new Enumeration()
    {
      public boolean hasMoreElements()
      {
        return false;
      }
      
      public URL nextElement()
      {
        return null;
      }
    };
    try
    {
      localObject1 = Thread.currentThread().getContextClassLoader().getResources(paramString);
    }
    catch (IOException localIOException1)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localIOException1.getMessage(), localIOException1);
      }
    }
    if ((!((Enumeration)localObject1).hasMoreElements()) && (paramString.startsWith("/"))) {
      try
      {
        localObject1 = Thread.currentThread().getContextClassLoader().getResources(paramString.substring(1));
      }
      catch (IOException localIOException2)
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, localIOException2.getMessage(), localIOException2);
        }
      }
    }
    ClassLoader localClassLoader = ClassLoaderUtils.class.getClassLoader();
    if (localClassLoader == null) {
      localClassLoader = ClassLoader.getSystemClassLoader();
    }
    if (!((Enumeration)localObject1).hasMoreElements()) {
      try
      {
        localObject1 = localClassLoader.getResources(paramString);
      }
      catch (IOException localIOException3)
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, localIOException3.getMessage(), localIOException3);
        }
      }
    }
    if ((!((Enumeration)localObject1).hasMoreElements()) && (paramString.startsWith("/"))) {
      try
      {
        localObject1 = localClassLoader.getResources(paramString.substring(1));
      }
      catch (IOException localIOException4)
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, localIOException4.getMessage(), localIOException4);
        }
      }
    }
    Object localObject2;
    if (!((Enumeration)localObject1).hasMoreElements())
    {
      localObject2 = paramClass.getClassLoader();
      if (localObject2 != null) {
        try
        {
          localObject1 = ((ClassLoader)localObject2).getResources(paramString);
        }
        catch (IOException localIOException5)
        {
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, localIOException5.getMessage(), localIOException5);
          }
        }
      }
    }
    if (!((Enumeration)localObject1).hasMoreElements())
    {
      localObject2 = paramClass.getResource(paramString);
      if (localObject2 != null) {
        localArrayList.add(localObject2);
      }
    }
    while (((Enumeration)localObject1).hasMoreElements()) {
      localArrayList.add(((Enumeration)localObject1).nextElement());
    }
    if ((localArrayList.isEmpty()) && (paramString != null) && (paramString.charAt(0) != '/')) {
      return getResources('/' + paramString, paramClass);
    }
    return localArrayList;
  }
  
  static InputStream getResourceAsStream(String paramString, Class<?> paramClass)
  {
    URL localURL = getResource(paramString, paramClass);
    try
    {
      return localURL != null ? localURL.openStream() : null;
    }
    catch (IOException localIOException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localIOException.getMessage(), localIOException);
      }
    }
    return null;
  }
  
  static Class<?> loadClass(String paramString, Class<?> paramClass)
    throws ClassNotFoundException
  {
    try
    {
      ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
      if (localClassLoader != null) {
        return localClassLoader.loadClass(paramString);
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localClassNotFoundException.getMessage(), localClassNotFoundException);
      }
    }
    return loadClass2(paramString, paramClass);
  }
  
  private static Class<?> loadClass2(String paramString, Class<?> paramClass)
    throws ClassNotFoundException
  {
    try
    {
      return Class.forName(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      try
      {
        if (ClassLoaderUtils.class.getClassLoader() != null) {
          return ClassLoaderUtils.class.getClassLoader().loadClass(paramString);
        }
      }
      catch (ClassNotFoundException localClassNotFoundException2)
      {
        if ((paramClass != null) && (paramClass.getClassLoader() != null)) {
          return paramClass.getClassLoader().loadClass(paramString);
        }
      }
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localClassNotFoundException1.getMessage(), localClassNotFoundException1);
      }
      throw localClassNotFoundException1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\algorithms\ClassLoaderUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */