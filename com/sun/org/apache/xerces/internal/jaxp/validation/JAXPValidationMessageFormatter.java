package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

final class JAXPValidationMessageFormatter
{
  JAXPValidationMessageFormatter() {}
  
  public static String formatMessage(Locale paramLocale, String paramString, Object[] paramArrayOfObject)
    throws MissingResourceException
  {
    ResourceBundle localResourceBundle = null;
    if (paramLocale != null) {
      localResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.JAXPValidationMessages", paramLocale);
    } else {
      localResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.JAXPValidationMessages");
    }
    String str;
    try
    {
      str = localResourceBundle.getString(paramString);
      if (paramArrayOfObject != null) {
        try
        {
          str = MessageFormat.format(str, paramArrayOfObject);
        }
        catch (Exception localException)
        {
          str = localResourceBundle.getString("FormatFailed");
          str = str + " " + localResourceBundle.getString(paramString);
        }
      }
    }
    catch (MissingResourceException localMissingResourceException)
    {
      str = localResourceBundle.getString("BadMessageKey");
      throw new MissingResourceException(paramString, str, paramString);
    }
    if (str == null)
    {
      str = paramString;
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\JAXPValidationMessageFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */