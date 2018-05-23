package com.sun.xml.internal.messaging.saaj.util;

import java.security.AccessControlException;

public final class SAAJUtil
{
  public SAAJUtil() {}
  
  public static boolean getSystemBoolean(String paramString)
  {
    try
    {
      return Boolean.getBoolean(paramString);
    }
    catch (AccessControlException localAccessControlException) {}
    return false;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\util\SAAJUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */