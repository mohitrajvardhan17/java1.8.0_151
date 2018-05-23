package com.sun.xml.internal.bind;

import java.util.logging.Logger;

public final class Util
{
  private Util() {}
  
  public static Logger getClassLogger()
  {
    try
    {
      StackTraceElement[] arrayOfStackTraceElement = new Exception().getStackTrace();
      return Logger.getLogger(arrayOfStackTraceElement[1].getClassName());
    }
    catch (SecurityException localSecurityException) {}
    return Logger.getLogger("com.sun.xml.internal.bind");
  }
  
  public static String getSystemProperty(String paramString)
  {
    try
    {
      return System.getProperty(paramString);
    }
    catch (SecurityException localSecurityException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */