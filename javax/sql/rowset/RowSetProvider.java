package javax.sql.rowset;

import java.io.PrintStream;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.PropertyPermission;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import sun.reflect.misc.ReflectUtil;

public class RowSetProvider
{
  private static final String ROWSET_DEBUG_PROPERTY = "javax.sql.rowset.RowSetProvider.debug";
  private static final String ROWSET_FACTORY_IMPL = "com.sun.rowset.RowSetFactoryImpl";
  private static final String ROWSET_FACTORY_NAME = "javax.sql.rowset.RowSetFactory";
  private static boolean debug = true;
  
  protected RowSetProvider() {}
  
  public static RowSetFactory newFactory()
    throws SQLException
  {
    RowSetFactory localRowSetFactory = null;
    String str = null;
    try
    {
      trace("Checking for Rowset System Property...");
      str = getSystemProperty("javax.sql.rowset.RowSetFactory");
      if (str != null)
      {
        trace("Found system property, value=" + str);
        localRowSetFactory = (RowSetFactory)ReflectUtil.newInstance(getFactoryClass(str, null, true));
      }
    }
    catch (Exception localException)
    {
      throw new SQLException("RowSetFactory: " + str + " could not be instantiated: ", localException);
    }
    if (localRowSetFactory == null)
    {
      localRowSetFactory = loadViaServiceLoader();
      localRowSetFactory = localRowSetFactory == null ? newFactory("com.sun.rowset.RowSetFactoryImpl", null) : localRowSetFactory;
    }
    return localRowSetFactory;
  }
  
  public static RowSetFactory newFactory(String paramString, ClassLoader paramClassLoader)
    throws SQLException
  {
    trace("***In newInstance()");
    if (paramString == null) {
      throw new SQLException("Error: factoryClassName cannot be null");
    }
    try
    {
      ReflectUtil.checkPackageAccess(paramString);
    }
    catch (AccessControlException localAccessControlException)
    {
      throw new SQLException("Access Exception", localAccessControlException);
    }
    try
    {
      Class localClass = getFactoryClass(paramString, paramClassLoader, false);
      RowSetFactory localRowSetFactory = (RowSetFactory)localClass.newInstance();
      if (debug) {
        trace("Created new instance of " + localClass + " using ClassLoader: " + paramClassLoader);
      }
      return localRowSetFactory;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new SQLException("Provider " + paramString + " not found", localClassNotFoundException);
    }
    catch (Exception localException)
    {
      throw new SQLException("Provider " + paramString + " could not be instantiated: " + localException, localException);
    }
  }
  
  private static ClassLoader getContextClassLoader()
    throws SecurityException
  {
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ClassLoader run()
      {
        ClassLoader localClassLoader = null;
        localClassLoader = Thread.currentThread().getContextClassLoader();
        if (localClassLoader == null) {
          localClassLoader = ClassLoader.getSystemClassLoader();
        }
        return localClassLoader;
      }
    });
  }
  
  private static Class<?> getFactoryClass(String paramString, ClassLoader paramClassLoader, boolean paramBoolean)
    throws ClassNotFoundException
  {
    try
    {
      if (paramClassLoader == null)
      {
        paramClassLoader = getContextClassLoader();
        if (paramClassLoader == null) {
          throw new ClassNotFoundException();
        }
        return paramClassLoader.loadClass(paramString);
      }
      return paramClassLoader.loadClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (paramBoolean) {
        return Class.forName(paramString, true, RowSetFactory.class.getClassLoader());
      }
      throw localClassNotFoundException;
    }
  }
  
  private static RowSetFactory loadViaServiceLoader()
    throws SQLException
  {
    Object localObject = null;
    try
    {
      trace("***in loadViaServiceLoader():");
      Iterator localIterator = ServiceLoader.load(RowSetFactory.class).iterator();
      if (localIterator.hasNext())
      {
        RowSetFactory localRowSetFactory = (RowSetFactory)localIterator.next();
        trace(" Loading done by the java.util.ServiceLoader :" + localRowSetFactory.getClass().getName());
        localObject = localRowSetFactory;
      }
    }
    catch (ServiceConfigurationError localServiceConfigurationError)
    {
      throw new SQLException("RowSetFactory: Error locating RowSetFactory using Service Loader API: " + localServiceConfigurationError, localServiceConfigurationError);
    }
    return (RowSetFactory)localObject;
  }
  
  private static String getSystemProperty(String paramString)
  {
    String str = null;
    try
    {
      str = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
        public String run()
        {
          return System.getProperty(val$propName);
        }
      }, null, new Permission[] { new PropertyPermission(paramString, "read") });
    }
    catch (SecurityException localSecurityException)
    {
      trace("error getting " + paramString + ":  " + localSecurityException);
      if (debug) {
        localSecurityException.printStackTrace();
      }
    }
    return str;
  }
  
  private static void trace(String paramString)
  {
    if (debug) {
      System.err.println("###RowSets: " + paramString);
    }
  }
  
  static
  {
    String str = getSystemProperty("javax.sql.rowset.RowSetProvider.debug");
    debug = (str != null) && (!"false".equals(str));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\RowSetProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */