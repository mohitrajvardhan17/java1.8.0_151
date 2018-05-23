package com.sun.org.apache.xml.internal.res;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.ListResourceBundle;
import java.util.Locale;

public class XMLMessages
{
  protected Locale fLocale = Locale.getDefault();
  private static ListResourceBundle XMLBundle = null;
  private static final String XML_ERROR_RESOURCES = "com.sun.org.apache.xml.internal.res.XMLErrorResources";
  protected static final String BAD_CODE = "BAD_CODE";
  protected static final String FORMAT_FAILED = "FORMAT_FAILED";
  
  public XMLMessages() {}
  
  public void setLocale(Locale paramLocale)
  {
    fLocale = paramLocale;
  }
  
  public Locale getLocale()
  {
    return fLocale;
  }
  
  public static final String createXMLMessage(String paramString, Object[] paramArrayOfObject)
  {
    if (XMLBundle == null) {
      XMLBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xml.internal.res.XMLErrorResources");
    }
    if (XMLBundle != null) {
      return createMsg(XMLBundle, paramString, paramArrayOfObject);
    }
    return "Could not load any resource bundles.";
  }
  
  public static final String createMsg(ListResourceBundle paramListResourceBundle, String paramString, Object[] paramArrayOfObject)
  {
    Object localObject = null;
    int i = 0;
    String str = null;
    if (paramString != null) {
      str = paramListResourceBundle.getString(paramString);
    }
    if (str == null)
    {
      str = paramListResourceBundle.getString("BAD_CODE");
      i = 1;
    }
    if (paramArrayOfObject != null) {
      try
      {
        int j = paramArrayOfObject.length;
        for (int k = 0; k < j; k++) {
          if (null == paramArrayOfObject[k]) {
            paramArrayOfObject[k] = "";
          }
        }
        localObject = MessageFormat.format(str, paramArrayOfObject);
      }
      catch (Exception localException)
      {
        localObject = paramListResourceBundle.getString("FORMAT_FAILED");
        localObject = (String)localObject + " " + str;
      }
    } else {
      localObject = str;
    }
    if (i != 0) {
      throw new RuntimeException((String)localObject);
    }
    return (String)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\res\XMLMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */