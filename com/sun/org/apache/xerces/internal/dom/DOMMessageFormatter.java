package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class DOMMessageFormatter
{
  public static final String DOM_DOMAIN = "http://www.w3.org/dom/DOMTR";
  public static final String XML_DOMAIN = "http://www.w3.org/TR/1998/REC-xml-19980210";
  public static final String SERIALIZER_DOMAIN = "http://apache.org/xml/serializer";
  private static ResourceBundle domResourceBundle = null;
  private static ResourceBundle xmlResourceBundle = null;
  private static ResourceBundle serResourceBundle = null;
  private static Locale locale = null;
  
  DOMMessageFormatter()
  {
    locale = Locale.getDefault();
  }
  
  public static String formatMessage(String paramString1, String paramString2, Object[] paramArrayOfObject)
    throws MissingResourceException
  {
    ResourceBundle localResourceBundle = getResourceBundle(paramString1);
    if (localResourceBundle == null)
    {
      init();
      localResourceBundle = getResourceBundle(paramString1);
      if (localResourceBundle == null) {
        throw new MissingResourceException("Unknown domain" + paramString1, null, paramString2);
      }
    }
    String str;
    try
    {
      str = paramString2 + ": " + localResourceBundle.getString(paramString2);
      if (paramArrayOfObject != null) {
        try
        {
          str = MessageFormat.format(str, paramArrayOfObject);
        }
        catch (Exception localException)
        {
          str = localResourceBundle.getString("FormatFailed");
          str = str + " " + localResourceBundle.getString(paramString2);
        }
      }
    }
    catch (MissingResourceException localMissingResourceException)
    {
      str = localResourceBundle.getString("BadMessageKey");
      throw new MissingResourceException(paramString2, str, paramString2);
    }
    if (str == null)
    {
      str = paramString2;
      if (paramArrayOfObject.length > 0)
      {
        StringBuffer localStringBuffer = new StringBuffer(str);
        localStringBuffer.append('?');
        for (int i = 0; i < paramArrayOfObject.length; i++)
        {
          if (i > 0) {
            localStringBuffer.append('&');
          }
          localStringBuffer.append(String.valueOf(paramArrayOfObject[i]));
        }
      }
    }
    return str;
  }
  
  static ResourceBundle getResourceBundle(String paramString)
  {
    if ((paramString == "http://www.w3.org/dom/DOMTR") || (paramString.equals("http://www.w3.org/dom/DOMTR"))) {
      return domResourceBundle;
    }
    if ((paramString == "http://www.w3.org/TR/1998/REC-xml-19980210") || (paramString.equals("http://www.w3.org/TR/1998/REC-xml-19980210"))) {
      return xmlResourceBundle;
    }
    if ((paramString == "http://apache.org/xml/serializer") || (paramString.equals("http://apache.org/xml/serializer"))) {
      return serResourceBundle;
    }
    return null;
  }
  
  public static void init()
  {
    if (locale != null)
    {
      domResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.DOMMessages", locale);
      serResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSerializerMessages", locale);
      xmlResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages", locale);
    }
    else
    {
      domResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.DOMMessages");
      serResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSerializerMessages");
      xmlResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages");
    }
  }
  
  public static void setLocale(Locale paramLocale)
  {
    locale = paramLocale;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMMessageFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */