package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class DatatypeException
  extends Exception
{
  static final long serialVersionUID = 1940805832730465578L;
  protected String key;
  protected Object[] args;
  
  public DatatypeException(String paramString, Object[] paramArrayOfObject)
  {
    super(paramString);
    key = paramString;
    args = paramArrayOfObject;
  }
  
  public String getKey()
  {
    return key;
  }
  
  public Object[] getArgs()
  {
    return args;
  }
  
  public String getMessage()
  {
    ResourceBundle localResourceBundle = null;
    localResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages");
    if (localResourceBundle == null) {
      throw new MissingResourceException("Property file not found!", "com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages", key);
    }
    String str = localResourceBundle.getString(key);
    if (str == null)
    {
      str = localResourceBundle.getString("BadMessageKey");
      throw new MissingResourceException(str, "com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages", key);
    }
    if (args != null) {
      try
      {
        str = MessageFormat.format(str, args);
      }
      catch (Exception localException)
      {
        str = localResourceBundle.getString("FormatFailed");
        str = str + " " + localResourceBundle.getString(key);
      }
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\DatatypeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */