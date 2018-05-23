package com.sun.org.apache.bcel.internal.util;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class JavaWrapper
{
  private ClassLoader loader;
  
  private static ClassLoader getClassLoader()
  {
    String str = SecuritySupport.getSystemProperty("bcel.classloader");
    if ((str == null) || ("".equals(str))) {
      str = "com.sun.org.apache.bcel.internal.util.ClassLoader";
    }
    try
    {
      return (ClassLoader)Class.forName(str).newInstance();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException.toString());
    }
  }
  
  public JavaWrapper(ClassLoader paramClassLoader)
  {
    loader = paramClassLoader;
  }
  
  public JavaWrapper()
  {
    this(getClassLoader());
  }
  
  public void runMain(String paramString, String[] paramArrayOfString)
    throws ClassNotFoundException
  {
    Class localClass1 = loader.loadClass(paramString);
    Method localMethod = null;
    try
    {
      localMethod = localClass1.getMethod("_main", new Class[] { paramArrayOfString.getClass() });
      int i = localMethod.getModifiers();
      Class localClass2 = localMethod.getReturnType();
      if ((!Modifier.isPublic(i)) || (!Modifier.isStatic(i)) || (Modifier.isAbstract(i)) || (localClass2 != Void.TYPE)) {
        throw new NoSuchMethodException();
      }
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      System.out.println("In class " + paramString + ": public static void _main(String[] argv) is not defined");
      return;
    }
    try
    {
      localMethod.invoke(null, new Object[] { paramArrayOfString });
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public static void _main(String[] paramArrayOfString)
    throws Exception
  {
    if (paramArrayOfString.length == 0)
    {
      System.out.println("Missing class name.");
      return;
    }
    String str = paramArrayOfString[0];
    String[] arrayOfString = new String[paramArrayOfString.length - 1];
    System.arraycopy(paramArrayOfString, 1, arrayOfString, 0, arrayOfString.length);
    JavaWrapper localJavaWrapper = new JavaWrapper();
    localJavaWrapper.runMain(str, arrayOfString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\JavaWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */