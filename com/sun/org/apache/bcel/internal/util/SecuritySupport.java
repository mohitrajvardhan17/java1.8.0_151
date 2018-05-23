package com.sun.org.apache.bcel.internal.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class SecuritySupport
{
  private static final SecuritySupport securitySupport = new SecuritySupport();
  
  public static SecuritySupport getInstance()
  {
    return securitySupport;
  }
  
  static ClassLoader getContextClassLoader()
  {
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        ClassLoader localClassLoader = null;
        try
        {
          localClassLoader = Thread.currentThread().getContextClassLoader();
        }
        catch (SecurityException localSecurityException) {}
        return localClassLoader;
      }
    });
  }
  
  static ClassLoader getSystemClassLoader()
  {
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        ClassLoader localClassLoader = null;
        try
        {
          localClassLoader = ClassLoader.getSystemClassLoader();
        }
        catch (SecurityException localSecurityException) {}
        return localClassLoader;
      }
    });
  }
  
  static ClassLoader getParentClassLoader(ClassLoader paramClassLoader)
  {
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        ClassLoader localClassLoader = null;
        try
        {
          localClassLoader = val$cl.getParent();
        }
        catch (SecurityException localSecurityException) {}
        return localClassLoader == val$cl ? null : localClassLoader;
      }
    });
  }
  
  public static String getSystemProperty(String paramString)
  {
    (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return System.getProperty(val$propName);
      }
    });
  }
  
  static FileInputStream getFileInputStream(File paramFile)
    throws FileNotFoundException
  {
    try
    {
      (FileInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws FileNotFoundException
        {
          return new FileInputStream(val$file);
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((FileNotFoundException)localPrivilegedActionException.getException());
    }
  }
  
  public static InputStream getResourceAsStream(String paramString)
  {
    if (System.getSecurityManager() != null) {
      return getResourceAsStream(null, paramString);
    }
    return getResourceAsStream(findClassLoader(), paramString);
  }
  
  public static InputStream getResourceAsStream(ClassLoader paramClassLoader, final String paramString)
  {
    (InputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        InputStream localInputStream;
        if (val$cl == null) {
          localInputStream = Object.class.getResourceAsStream("/" + paramString);
        } else {
          localInputStream = val$cl.getResourceAsStream(paramString);
        }
        return localInputStream;
      }
    });
  }
  
  public static ListResourceBundle getResourceBundle(String paramString)
  {
    return getResourceBundle(paramString, Locale.getDefault());
  }
  
  public static ListResourceBundle getResourceBundle(String paramString, final Locale paramLocale)
  {
    (ListResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ListResourceBundle run()
      {
        try
        {
          return (ListResourceBundle)ResourceBundle.getBundle(val$bundle, paramLocale);
        }
        catch (MissingResourceException localMissingResourceException1)
        {
          try
          {
            return (ListResourceBundle)ResourceBundle.getBundle(val$bundle, new Locale("en", "US"));
          }
          catch (MissingResourceException localMissingResourceException2)
          {
            throw new MissingResourceException("Could not load any resource bundle by " + val$bundle, val$bundle, "");
          }
        }
      }
    });
  }
  
  public static String[] getFileList(File paramFile, final FilenameFilter paramFilenameFilter)
  {
    (String[])AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return val$f.list(paramFilenameFilter);
      }
    });
  }
  
  public static boolean getFileExists(File paramFile)
  {
    ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return val$f.exists() ? Boolean.TRUE : Boolean.FALSE;
      }
    })).booleanValue();
  }
  
  static long getLastModified(File paramFile)
  {
    ((Long)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return new Long(val$f.lastModified());
      }
    })).longValue();
  }
  
  public static ClassLoader findClassLoader()
  {
    if (System.getSecurityManager() != null) {
      return null;
    }
    return SecuritySupport.class.getClassLoader();
  }
  
  private SecuritySupport() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\SecuritySupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */