package com.sun.org.apache.xerces.internal.xpointer;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

class XPointerMessageFormatter
  implements MessageFormatter
{
  public static final String XPOINTER_DOMAIN = "http://www.w3.org/TR/XPTR";
  private Locale fLocale = null;
  private ResourceBundle fResourceBundle = null;
  
  XPointerMessageFormatter() {}
  
  public String formatMessage(Locale paramLocale, String paramString, Object[] paramArrayOfObject)
    throws MissingResourceException
  {
    if ((fResourceBundle == null) || (paramLocale != fLocale))
    {
      if (paramLocale != null)
      {
        fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XPointerMessages", paramLocale);
        fLocale = paramLocale;
      }
      if (fResourceBundle == null) {
        fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XPointerMessages");
      }
    }
    String str = fResourceBundle.getString(paramString);
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
    if (str == null)
    {
      str = fResourceBundle.getString("BadMessageKey");
      throw new MissingResourceException(str, "com.sun.org.apache.xerces.internal.impl.msg.XPointerMessages", paramString);
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xpointer\XPointerMessageFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */