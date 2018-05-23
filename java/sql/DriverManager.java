package java.sql;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public class DriverManager
{
  private static final CopyOnWriteArrayList<DriverInfo> registeredDrivers = new CopyOnWriteArrayList();
  private static volatile int loginTimeout = 0;
  private static volatile PrintWriter logWriter = null;
  private static volatile PrintStream logStream = null;
  private static final Object logSync = new Object();
  static final SQLPermission SET_LOG_PERMISSION = new SQLPermission("setLog");
  static final SQLPermission DEREGISTER_DRIVER_PERMISSION = new SQLPermission("deregisterDriver");
  
  private DriverManager() {}
  
  public static PrintWriter getLogWriter()
  {
    return logWriter;
  }
  
  public static void setLogWriter(PrintWriter paramPrintWriter)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SET_LOG_PERMISSION);
    }
    logStream = null;
    logWriter = paramPrintWriter;
  }
  
  @CallerSensitive
  public static Connection getConnection(String paramString, Properties paramProperties)
    throws SQLException
  {
    return getConnection(paramString, paramProperties, Reflection.getCallerClass());
  }
  
  @CallerSensitive
  public static Connection getConnection(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    Properties localProperties = new Properties();
    if (paramString2 != null) {
      localProperties.put("user", paramString2);
    }
    if (paramString3 != null) {
      localProperties.put("password", paramString3);
    }
    return getConnection(paramString1, localProperties, Reflection.getCallerClass());
  }
  
  @CallerSensitive
  public static Connection getConnection(String paramString)
    throws SQLException
  {
    Properties localProperties = new Properties();
    return getConnection(paramString, localProperties, Reflection.getCallerClass());
  }
  
  @CallerSensitive
  public static Driver getDriver(String paramString)
    throws SQLException
  {
    println("DriverManager.getDriver(\"" + paramString + "\")");
    Class localClass = Reflection.getCallerClass();
    Iterator localIterator = registeredDrivers.iterator();
    while (localIterator.hasNext())
    {
      DriverInfo localDriverInfo = (DriverInfo)localIterator.next();
      if (isDriverAllowed(driver, localClass)) {
        try
        {
          if (driver.acceptsURL(paramString))
          {
            println("getDriver returning " + driver.getClass().getName());
            return driver;
          }
        }
        catch (SQLException localSQLException) {}
      }
      println("    skipping: " + driver.getClass().getName());
    }
    println("getDriver: no suitable driver");
    throw new SQLException("No suitable driver", "08001");
  }
  
  public static synchronized void registerDriver(Driver paramDriver)
    throws SQLException
  {
    registerDriver(paramDriver, null);
  }
  
  public static synchronized void registerDriver(Driver paramDriver, DriverAction paramDriverAction)
    throws SQLException
  {
    if (paramDriver != null) {
      registeredDrivers.addIfAbsent(new DriverInfo(paramDriver, paramDriverAction));
    } else {
      throw new NullPointerException();
    }
    println("registerDriver: " + paramDriver);
  }
  
  @CallerSensitive
  public static synchronized void deregisterDriver(Driver paramDriver)
    throws SQLException
  {
    if (paramDriver == null) {
      return;
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(DEREGISTER_DRIVER_PERMISSION);
    }
    println("DriverManager.deregisterDriver: " + paramDriver);
    DriverInfo localDriverInfo1 = new DriverInfo(paramDriver, null);
    if (registeredDrivers.contains(localDriverInfo1))
    {
      if (isDriverAllowed(paramDriver, Reflection.getCallerClass()))
      {
        DriverInfo localDriverInfo2 = (DriverInfo)registeredDrivers.get(registeredDrivers.indexOf(localDriverInfo1));
        if (localDriverInfo2.action() != null) {
          localDriverInfo2.action().deregister();
        }
        registeredDrivers.remove(localDriverInfo1);
      }
      else
      {
        throw new SecurityException();
      }
    }
    else {
      println("    couldn't find driver to unload");
    }
  }
  
  @CallerSensitive
  public static Enumeration<Driver> getDrivers()
  {
    Vector localVector = new Vector();
    Class localClass = Reflection.getCallerClass();
    Iterator localIterator = registeredDrivers.iterator();
    while (localIterator.hasNext())
    {
      DriverInfo localDriverInfo = (DriverInfo)localIterator.next();
      if (isDriverAllowed(driver, localClass)) {
        localVector.addElement(driver);
      } else {
        println("    skipping: " + localDriverInfo.getClass().getName());
      }
    }
    return localVector.elements();
  }
  
  public static void setLoginTimeout(int paramInt)
  {
    loginTimeout = paramInt;
  }
  
  public static int getLoginTimeout()
  {
    return loginTimeout;
  }
  
  @Deprecated
  public static void setLogStream(PrintStream paramPrintStream)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SET_LOG_PERMISSION);
    }
    logStream = paramPrintStream;
    if (paramPrintStream != null) {
      logWriter = new PrintWriter(paramPrintStream);
    } else {
      logWriter = null;
    }
  }
  
  @Deprecated
  public static PrintStream getLogStream()
  {
    return logStream;
  }
  
  public static void println(String paramString)
  {
    synchronized (logSync)
    {
      if (logWriter != null)
      {
        logWriter.println(paramString);
        logWriter.flush();
      }
    }
  }
  
  private static boolean isDriverAllowed(Driver paramDriver, Class<?> paramClass)
  {
    ClassLoader localClassLoader = paramClass != null ? paramClass.getClassLoader() : null;
    return isDriverAllowed(paramDriver, localClassLoader);
  }
  
  private static boolean isDriverAllowed(Driver paramDriver, ClassLoader paramClassLoader)
  {
    boolean bool = false;
    if (paramDriver != null)
    {
      Class localClass = null;
      try
      {
        localClass = Class.forName(paramDriver.getClass().getName(), true, paramClassLoader);
      }
      catch (Exception localException)
      {
        bool = false;
      }
      bool = localClass == paramDriver.getClass();
    }
    return bool;
  }
  
  private static void loadInitialDrivers()
  {
    String str1;
    try
    {
      str1 = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
        public String run()
        {
          return System.getProperty("jdbc.drivers");
        }
      });
    }
    catch (Exception localException1)
    {
      str1 = null;
    }
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        ServiceLoader localServiceLoader = ServiceLoader.load(Driver.class);
        Iterator localIterator = localServiceLoader.iterator();
        try
        {
          while (localIterator.hasNext()) {
            localIterator.next();
          }
        }
        catch (Throwable localThrowable) {}
        return null;
      }
    });
    println("DriverManager.initialize: jdbc.drivers = " + str1);
    if ((str1 == null) || (str1.equals(""))) {
      return;
    }
    String[] arrayOfString1 = str1.split(":");
    println("number of Drivers:" + arrayOfString1.length);
    for (String str2 : arrayOfString1) {
      try
      {
        println("DriverManager.Initialize: loading " + str2);
        Class.forName(str2, true, ClassLoader.getSystemClassLoader());
      }
      catch (Exception localException2)
      {
        println("DriverManager.Initialize: load failed: " + localException2);
      }
    }
  }
  
  private static Connection getConnection(String paramString, Properties paramProperties, Class<?> paramClass)
    throws SQLException
  {
    ClassLoader localClassLoader = paramClass != null ? paramClass.getClassLoader() : null;
    synchronized (DriverManager.class)
    {
      if (localClassLoader == null) {
        localClassLoader = Thread.currentThread().getContextClassLoader();
      }
    }
    if (paramString == null) {
      throw new SQLException("The url cannot be null", "08001");
    }
    println("DriverManager.getConnection(\"" + paramString + "\")");
    ??? = null;
    Iterator localIterator = registeredDrivers.iterator();
    while (localIterator.hasNext())
    {
      DriverInfo localDriverInfo = (DriverInfo)localIterator.next();
      if (isDriverAllowed(driver, localClassLoader)) {
        try
        {
          println("    trying " + driver.getClass().getName());
          Connection localConnection = driver.connect(paramString, paramProperties);
          if (localConnection != null)
          {
            println("getConnection returning " + driver.getClass().getName());
            return localConnection;
          }
        }
        catch (SQLException localSQLException)
        {
          if (??? == null) {
            ??? = localSQLException;
          }
        }
      }
      println("    skipping: " + localDriverInfo.getClass().getName());
    }
    if (??? != null)
    {
      println("getConnection failed: " + ???);
      throw ((Throwable)???);
    }
    println("getConnection: no suitable driver found for " + paramString);
    throw new SQLException("No suitable driver found for " + paramString, "08001");
  }
  
  static
  {
    loadInitialDrivers();
    println("JDBC DriverManager initialized");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\DriverManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */