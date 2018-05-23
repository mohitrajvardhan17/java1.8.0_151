package com.sun.org.apache.xml.internal.serializer.utils;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.ListResourceBundle;
import java.util.Locale;

public final class Messages
{
  private final Locale m_locale = Locale.getDefault();
  private ListResourceBundle m_resourceBundle;
  private String m_resourceBundleName;
  
  Messages(String paramString)
  {
    m_resourceBundleName = paramString;
  }
  
  private Locale getLocale()
  {
    return m_locale;
  }
  
  public final String createMessage(String paramString, Object[] paramArrayOfObject)
  {
    if (m_resourceBundle == null) {
      m_resourceBundle = SecuritySupport.getResourceBundle(m_resourceBundleName);
    }
    if (m_resourceBundle != null) {
      return createMsg(m_resourceBundle, paramString, paramArrayOfObject);
    }
    return "Could not load the resource bundles: " + m_resourceBundleName;
  }
  
  private final String createMsg(ListResourceBundle paramListResourceBundle, String paramString, Object[] paramArrayOfObject)
  {
    Object localObject = null;
    int i = 0;
    String str = null;
    if (paramString != null) {
      str = paramListResourceBundle.getString(paramString);
    } else {
      paramString = "";
    }
    if (str == null)
    {
      i = 1;
      try
      {
        str = MessageFormat.format("BAD_MSGKEY", new Object[] { paramString, m_resourceBundleName });
      }
      catch (Exception localException1)
      {
        str = "The message key '" + paramString + "' is not in the message class '" + m_resourceBundleName + "'";
      }
    }
    else if (paramArrayOfObject != null)
    {
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
      catch (Exception localException2)
      {
        i = 1;
        try
        {
          localObject = MessageFormat.format("BAD_MSGFORMAT", new Object[] { paramString, m_resourceBundleName });
          localObject = (String)localObject + " " + str;
        }
        catch (Exception localException3)
        {
          localObject = "The format of message '" + paramString + "' in message class '" + m_resourceBundleName + "' failed.";
        }
      }
    }
    else
    {
      localObject = str;
    }
    if (i != 0) {
      throw new RuntimeException((String)localObject);
    }
    return (String)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\utils\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */