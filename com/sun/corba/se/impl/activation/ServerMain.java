package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Properties;
import org.omg.CORBA.ORB;

public class ServerMain
{
  public static final int OK = 0;
  public static final int MAIN_CLASS_NOT_FOUND = 1;
  public static final int NO_MAIN_METHOD = 2;
  public static final int APPLICATION_ERROR = 3;
  public static final int UNKNOWN_ERROR = 4;
  public static final int NO_SERVER_ID = 5;
  public static final int REGISTRATION_FAILED = 6;
  private static final boolean debug = false;
  
  public ServerMain() {}
  
  public static String printResult(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return "Server terminated normally";
    case 1: 
      return "main class not found";
    case 2: 
      return "no main method";
    case 3: 
      return "application error";
    case 5: 
      return "server ID not defined";
    case 6: 
      return "server registration failed";
    }
    return "unknown error";
  }
  
  private void redirectIOStreams()
  {
    try
    {
      String str1 = System.getProperty("com.sun.CORBA.activation.DbDir") + System.getProperty("file.separator") + "logs" + System.getProperty("file.separator");
      File localFile = new File(str1);
      String str2 = System.getProperty("com.sun.CORBA.POA.ORBServerId");
      FileOutputStream localFileOutputStream1 = new FileOutputStream(str1 + str2 + ".out", true);
      FileOutputStream localFileOutputStream2 = new FileOutputStream(str1 + str2 + ".err", true);
      PrintStream localPrintStream1 = new PrintStream(localFileOutputStream1, true);
      PrintStream localPrintStream2 = new PrintStream(localFileOutputStream2, true);
      System.setOut(localPrintStream1);
      System.setErr(localPrintStream2);
      logInformation("Server started");
    }
    catch (Exception localException) {}
  }
  
  private static void writeLogMessage(PrintStream paramPrintStream, String paramString)
  {
    Date localDate = new Date();
    paramPrintStream.print("[" + localDate.toString() + "] " + paramString + "\n");
  }
  
  public static void logInformation(String paramString)
  {
    writeLogMessage(System.out, "        " + paramString);
  }
  
  public static void logError(String paramString)
  {
    writeLogMessage(System.out, "ERROR:  " + paramString);
    writeLogMessage(System.err, "ERROR:  " + paramString);
  }
  
  public static void logTerminal(String paramString, int paramInt)
  {
    if (paramInt == 0)
    {
      writeLogMessage(System.out, "        " + paramString);
    }
    else
    {
      writeLogMessage(System.out, "FATAL:  " + printResult(paramInt) + ": " + paramString);
      writeLogMessage(System.err, "FATAL:  " + printResult(paramInt) + ": " + paramString);
    }
    System.exit(paramInt);
  }
  
  private Method getMainMethod(Class paramClass)
  {
    Class[] arrayOfClass = { String[].class };
    Method localMethod = null;
    try
    {
      localMethod = paramClass.getDeclaredMethod("main", arrayOfClass);
    }
    catch (Exception localException)
    {
      logTerminal(localException.getMessage(), 2);
    }
    if (!isPublicStaticVoid(localMethod)) {
      logTerminal("", 2);
    }
    return localMethod;
  }
  
  private boolean isPublicStaticVoid(Method paramMethod)
  {
    int i = paramMethod.getModifiers();
    if ((!Modifier.isPublic(i)) || (!Modifier.isStatic(i)))
    {
      logError(paramMethod.getName() + " is not public static");
      return false;
    }
    if (paramMethod.getExceptionTypes().length != 0)
    {
      logError(paramMethod.getName() + " declares exceptions");
      return false;
    }
    if (!paramMethod.getReturnType().equals(Void.TYPE))
    {
      logError(paramMethod.getName() + " does not have a void return type");
      return false;
    }
    return true;
  }
  
  private Method getNamedMethod(Class paramClass, String paramString)
  {
    Class[] arrayOfClass = { ORB.class };
    Method localMethod = null;
    try
    {
      localMethod = paramClass.getDeclaredMethod(paramString, arrayOfClass);
    }
    catch (Exception localException)
    {
      return null;
    }
    if (!isPublicStaticVoid(localMethod)) {
      return null;
    }
    return localMethod;
  }
  
  private void run(String[] paramArrayOfString)
  {
    try
    {
      redirectIOStreams();
      String str = System.getProperty("com.sun.CORBA.POA.ORBServerName");
      ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
      if (localClassLoader == null) {
        localClassLoader = ClassLoader.getSystemClassLoader();
      }
      Class localClass = null;
      try
      {
        localClass = Class.forName(str);
      }
      catch (ClassNotFoundException localClassNotFoundException2)
      {
        localClass = Class.forName(str, true, localClassLoader);
      }
      Method localMethod = getMainMethod(localClass);
      boolean bool = Boolean.getBoolean("com.sun.CORBA.activation.ORBServerVerify");
      if (bool) {
        if (localMethod == null) {
          logTerminal("", 2);
        } else {
          logTerminal("", 0);
        }
      }
      registerCallback(localClass);
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramArrayOfString;
      localMethod.invoke(null, arrayOfObject);
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      logTerminal("ClassNotFound exception: " + localClassNotFoundException1.getMessage(), 1);
    }
    catch (Exception localException)
    {
      logTerminal("Exception: " + localException.getMessage(), 3);
    }
  }
  
  public static void main(String[] paramArrayOfString)
  {
    ServerMain localServerMain = new ServerMain();
    localServerMain.run(paramArrayOfString);
  }
  
  private int getServerId()
  {
    Integer localInteger = Integer.getInteger("com.sun.CORBA.POA.ORBServerId");
    if (localInteger == null) {
      logTerminal("", 5);
    }
    return localInteger.intValue();
  }
  
  private void registerCallback(Class paramClass)
  {
    Method localMethod1 = getNamedMethod(paramClass, "install");
    Method localMethod2 = getNamedMethod(paramClass, "uninstall");
    Method localMethod3 = getNamedMethod(paramClass, "shutdown");
    Properties localProperties = new Properties();
    localProperties.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
    localProperties.put("com.sun.CORBA.POA.ORBActivated", "false");
    String[] arrayOfString = null;
    ORB localORB = ORB.init(arrayOfString, localProperties);
    ServerCallback localServerCallback = new ServerCallback(localORB, localMethod1, localMethod2, localMethod3);
    int i = getServerId();
    try
    {
      Activator localActivator = ActivatorHelper.narrow(localORB.resolve_initial_references("ServerActivator"));
      localActivator.active(i, localServerCallback);
    }
    catch (Exception localException)
    {
      logTerminal("exception " + localException.getMessage(), 6);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\activation\ServerMain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */