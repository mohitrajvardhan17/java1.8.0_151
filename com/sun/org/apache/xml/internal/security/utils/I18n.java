package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.Init;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18n
{
  public static final String NOT_INITIALIZED_MSG = "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
  private static ResourceBundle resourceBundle;
  private static boolean alreadyInitialized = false;
  
  private I18n() {}
  
  public static String translate(String paramString, Object[] paramArrayOfObject)
  {
    return getExceptionMessage(paramString, paramArrayOfObject);
  }
  
  public static String translate(String paramString)
  {
    return getExceptionMessage(paramString);
  }
  
  public static String getExceptionMessage(String paramString)
  {
    try
    {
      return resourceBundle.getString(paramString);
    }
    catch (Throwable localThrowable)
    {
      if (Init.isInitialized()) {
        return "No message with ID \"" + paramString + "\" found in resource bundle \"" + "com/sun/org/apache/xml/internal/security/resource/xmlsecurity" + "\"";
      }
    }
    return "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
  }
  
  public static String getExceptionMessage(String paramString, Exception paramException)
  {
    try
    {
      Object[] arrayOfObject = { paramException.getMessage() };
      return MessageFormat.format(resourceBundle.getString(paramString), arrayOfObject);
    }
    catch (Throwable localThrowable)
    {
      if (Init.isInitialized()) {
        return "No message with ID \"" + paramString + "\" found in resource bundle \"" + "com/sun/org/apache/xml/internal/security/resource/xmlsecurity" + "\". Original Exception was a " + paramException.getClass().getName() + " and message " + paramException.getMessage();
      }
    }
    return "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
  }
  
  public static String getExceptionMessage(String paramString, Object[] paramArrayOfObject)
  {
    try
    {
      return MessageFormat.format(resourceBundle.getString(paramString), paramArrayOfObject);
    }
    catch (Throwable localThrowable)
    {
      if (Init.isInitialized()) {
        return "No message with ID \"" + paramString + "\" found in resource bundle \"" + "com/sun/org/apache/xml/internal/security/resource/xmlsecurity" + "\"";
      }
    }
    return "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
  }
  
  public static synchronized void init(String paramString1, String paramString2)
  {
    if (alreadyInitialized) {
      return;
    }
    resourceBundle = ResourceBundle.getBundle("com/sun/org/apache/xml/internal/security/resource/xmlsecurity", new Locale(paramString1, paramString2));
    alreadyInitialized = true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\I18n.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */