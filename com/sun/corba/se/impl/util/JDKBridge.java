package com.sun.corba.se.impl.util;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;

public class JDKBridge
{
  private static final String LOCAL_CODEBASE_KEY = "java.rmi.server.codebase";
  private static final String USE_CODEBASE_ONLY_KEY = "java.rmi.server.useCodebaseOnly";
  private static String localCodebase = null;
  private static boolean useCodebaseOnly;
  
  public JDKBridge() {}
  
  public static String getLocalCodebase()
  {
    return localCodebase;
  }
  
  public static boolean useCodebaseOnly()
  {
    return useCodebaseOnly;
  }
  
  public static Class loadClass(String paramString1, String paramString2, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    if (paramClassLoader == null) {
      return loadClassM(paramString1, paramString2, useCodebaseOnly);
    }
    try
    {
      return loadClassM(paramString1, paramString2, useCodebaseOnly);
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
    return paramClassLoader.loadClass(paramString1);
  }
  
  public static Class loadClass(String paramString1, String paramString2)
    throws ClassNotFoundException
  {
    return loadClass(paramString1, paramString2, null);
  }
  
  public static Class loadClass(String paramString)
    throws ClassNotFoundException
  {
    return loadClass(paramString, null, null);
  }
  
  public static final void main(String[] paramArrayOfString)
  {
    System.out.println("1.2 VM");
  }
  
  public static synchronized void setCodebaseProperties()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.codebase"));
    if ((str != null) && (str.trim().length() > 0)) {
      localCodebase = str;
    }
    str = (String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.useCodebaseOnly"));
    if ((str != null) && (str.trim().length() > 0)) {
      useCodebaseOnly = Boolean.valueOf(str).booleanValue();
    }
  }
  
  public static synchronized void setLocalCodebase(String paramString)
  {
    localCodebase = paramString;
  }
  
  private static Class loadClassM(String paramString1, String paramString2, boolean paramBoolean)
    throws ClassNotFoundException
  {
    try
    {
      return JDKClassLoader.loadClass(null, paramString1);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      try
      {
        if ((!paramBoolean) && (paramString2 != null)) {
          return RMIClassLoader.loadClass(paramString2, paramString1);
        }
        return RMIClassLoader.loadClass(paramString1);
      }
      catch (MalformedURLException localMalformedURLException)
      {
        paramString1 = paramString1 + ": " + localMalformedURLException.toString();
        throw new ClassNotFoundException(paramString1);
      }
    }
  }
  
  static
  {
    setCodebaseProperties();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\util\JDKBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */