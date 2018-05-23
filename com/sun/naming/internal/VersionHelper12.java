package com.sun.naming.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Properties;
import javax.naming.NamingEnumeration;

final class VersionHelper12
  extends VersionHelper
{
  VersionHelper12() {}
  
  public Class<?> loadClass(String paramString)
    throws ClassNotFoundException
  {
    return loadClass(paramString, getContextClassLoader());
  }
  
  Class<?> loadClass(String paramString, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    Class localClass = Class.forName(paramString, true, paramClassLoader);
    return localClass;
  }
  
  public Class<?> loadClass(String paramString1, String paramString2)
    throws ClassNotFoundException, MalformedURLException
  {
    ClassLoader localClassLoader = getContextClassLoader();
    URLClassLoader localURLClassLoader = URLClassLoader.newInstance(getUrlArray(paramString2), localClassLoader);
    return loadClass(paramString1, localURLClassLoader);
  }
  
  String getJndiProperty(final int paramInt)
  {
    (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        try
        {
          return System.getProperty(VersionHelper.PROPS[paramInt]);
        }
        catch (SecurityException localSecurityException) {}
        return null;
      }
    });
  }
  
  String[] getJndiProperties()
  {
    Properties localProperties = (Properties)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Properties run()
      {
        try
        {
          return System.getProperties();
        }
        catch (SecurityException localSecurityException) {}
        return null;
      }
    });
    if (localProperties == null) {
      return null;
    }
    String[] arrayOfString = new String[PROPS.length];
    for (int i = 0; i < PROPS.length; i++) {
      arrayOfString[i] = localProperties.getProperty(PROPS[i]);
    }
    return arrayOfString;
  }
  
  InputStream getResourceAsStream(final Class<?> paramClass, final String paramString)
  {
    (InputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public InputStream run()
      {
        return paramClass.getResourceAsStream(paramString);
      }
    });
  }
  
  InputStream getJavaHomeLibStream(final String paramString)
  {
    (InputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public InputStream run()
      {
        try
        {
          String str1 = System.getProperty("java.home");
          if (str1 == null) {
            return null;
          }
          String str2 = str1 + File.separator + "lib" + File.separator + paramString;
          return new FileInputStream(str2);
        }
        catch (Exception localException) {}
        return null;
      }
    });
  }
  
  NamingEnumeration<InputStream> getResources(final ClassLoader paramClassLoader, final String paramString)
    throws IOException
  {
    Enumeration localEnumeration;
    try
    {
      localEnumeration = (Enumeration)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Enumeration<URL> run()
          throws IOException
        {
          return paramClassLoader == null ? ClassLoader.getSystemResources(paramString) : paramClassLoader.getResources(paramString);
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
    return new InputStreamEnumeration(localEnumeration);
  }
  
  ClassLoader getContextClassLoader()
  {
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ClassLoader run()
      {
        ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
        if (localClassLoader == null) {
          localClassLoader = ClassLoader.getSystemClassLoader();
        }
        return localClassLoader;
      }
    });
  }
  
  class InputStreamEnumeration
    implements NamingEnumeration<InputStream>
  {
    private final Enumeration<URL> urls;
    private InputStream nextElement = null;
    
    InputStreamEnumeration()
    {
      Enumeration localEnumeration;
      urls = localEnumeration;
    }
    
    private InputStream getNextElement()
    {
      (InputStream)AccessController.doPrivileged(new PrivilegedAction()
      {
        public InputStream run()
        {
          while (urls.hasMoreElements()) {
            try
            {
              return ((URL)urls.nextElement()).openStream();
            }
            catch (IOException localIOException) {}
          }
          return null;
        }
      });
    }
    
    public boolean hasMore()
    {
      if (nextElement != null) {
        return true;
      }
      nextElement = getNextElement();
      return nextElement != null;
    }
    
    public boolean hasMoreElements()
    {
      return hasMore();
    }
    
    public InputStream next()
    {
      if (hasMore())
      {
        InputStream localInputStream = nextElement;
        nextElement = null;
        return localInputStream;
      }
      throw new NoSuchElementException();
    }
    
    public InputStream nextElement()
    {
      return next();
    }
    
    public void close() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\naming\internal\VersionHelper12.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */