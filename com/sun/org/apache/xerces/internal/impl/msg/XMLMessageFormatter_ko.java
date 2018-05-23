package com.sun.org.apache.xerces.internal.impl.msg;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class XMLMessageFormatter_ko
  implements MessageFormatter
{
  public static final String XML_DOMAIN = "http://www.w3.org/TR/1998/REC-xml-19980210";
  public static final String XMLNS_DOMAIN = "http://www.w3.org/TR/1999/REC-xml-names-19990114";
  private Locale fLocale = null;
  private ResourceBundle fResourceBundle = null;
  
  public XMLMessageFormatter_ko() {}
  
  public String formatMessage(Locale paramLocale, String paramString, Object[] paramArrayOfObject)
    throws MissingResourceException
  {
    if ((fResourceBundle == null) || (paramLocale != fLocale))
    {
      if (paramLocale != null)
      {
        fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages", paramLocale);
        fLocale = paramLocale;
      }
      if (fResourceBundle == null) {
        fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages");
      }
    }
    String str;
    try
    {
      str = fResourceBundle.getString(paramString);
      if (paramArrayOfObject != null) {
        try
        {
          str = MessageFormat.format(str, paramArrayOfObject);
        }
        catch (Exception localException)
        {
          str = fResourceBundle.getString("FormatFailed");
          str = str + " " + fResourceBundle.getString(paramString);
        }
      }
    }
    catch (MissingResourceException localMissingResourceException)
    {
      str = fResourceBundle.getString("BadMessageKey");
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\msg\XMLMessageFormatter_ko.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */