package javax.xml.xpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;

class SecuritySupport
{
  SecuritySupport() {}
  
  ClassLoader getContextClassLoader()
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
  
  String getSystemProperty(final String paramString)
  {
    (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return System.getProperty(paramString);
      }
    });
  }
  
  FileInputStream getFileInputStream(final File paramFile)
    throws FileNotFoundException
  {
    try
    {
      (FileInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws FileNotFoundException
        {
          return new FileInputStream(paramFile);
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((FileNotFoundException)localPrivilegedActionException.getException());
    }
  }
  
  InputStream getURLInputStream(final URL paramURL)
    throws IOException
  {
    try
    {
      (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws IOException
        {
          return paramURL.openStream();
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }
  
  URL getResourceAsURL(final ClassLoader paramClassLoader, final String paramString)
  {
    (URL)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        URL localURL;
        if (paramClassLoader == null) {
          localURL = Object.class.getResource(paramString);
        } else {
          localURL = paramClassLoader.getResource(paramString);
        }
        return localURL;
      }
    });
  }
  
  Enumeration getResources(final ClassLoader paramClassLoader, final String paramString)
    throws IOException
  {
    try
    {
      (Enumeration)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws IOException
        {
          Enumeration localEnumeration;
          if (paramClassLoader == null) {
            localEnumeration = ClassLoader.getSystemResources(paramString);
          } else {
            localEnumeration = paramClassLoader.getResources(paramString);
          }
          return localEnumeration;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }
  
  InputStream getResourceAsStream(final ClassLoader paramClassLoader, final String paramString)
  {
    (InputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        InputStream localInputStream;
        if (paramClassLoader == null) {
          localInputStream = Object.class.getResourceAsStream(paramString);
        } else {
          localInputStream = paramClassLoader.getResourceAsStream(paramString);
        }
        return localInputStream;
      }
    });
  }
  
  boolean doesFileExist(final File paramFile)
  {
    ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return new Boolean(paramFile.exists());
      }
    })).booleanValue();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\xpath\SecuritySupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */