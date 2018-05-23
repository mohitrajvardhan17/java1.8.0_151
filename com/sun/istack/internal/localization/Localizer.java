package com.sun.istack.internal.localization;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Localizer
{
  private final Locale _locale;
  private final HashMap _resourceBundles;
  
  public Localizer()
  {
    this(Locale.getDefault());
  }
  
  public Localizer(Locale paramLocale)
  {
    _locale = paramLocale;
    _resourceBundles = new HashMap();
  }
  
  public Locale getLocale()
  {
    return _locale;
  }
  
  public String localize(Localizable paramLocalizable)
  {
    String str1 = paramLocalizable.getKey();
    if (str1 == "\000") {
      return (String)paramLocalizable.getArguments()[0];
    }
    String str2 = paramLocalizable.getResourceBundleName();
    try
    {
      ResourceBundle localResourceBundle = (ResourceBundle)_resourceBundles.get(str2);
      if (localResourceBundle == null)
      {
        try
        {
          localResourceBundle = ResourceBundle.getBundle(str2, _locale);
        }
        catch (MissingResourceException localMissingResourceException2)
        {
          int i = str2.lastIndexOf('.');
          if (i != -1)
          {
            String str4 = str2.substring(i + 1);
            try
            {
              localResourceBundle = ResourceBundle.getBundle(str4, _locale);
            }
            catch (MissingResourceException localMissingResourceException4)
            {
              try
              {
                localResourceBundle = ResourceBundle.getBundle(str2, _locale, Thread.currentThread().getContextClassLoader());
              }
              catch (MissingResourceException localMissingResourceException5)
              {
                return getDefaultMessage(paramLocalizable);
              }
            }
          }
        }
        _resourceBundles.put(str2, localResourceBundle);
      }
      if (localResourceBundle == null) {
        return getDefaultMessage(paramLocalizable);
      }
      if (str1 == null) {
        str1 = "undefined";
      }
      String str3;
      try
      {
        str3 = localResourceBundle.getString(str1);
      }
      catch (MissingResourceException localMissingResourceException3)
      {
        str3 = localResourceBundle.getString("undefined");
      }
      Object[] arrayOfObject = paramLocalizable.getArguments();
      for (int j = 0; j < arrayOfObject.length; j++) {
        if ((arrayOfObject[j] instanceof Localizable)) {
          arrayOfObject[j] = localize((Localizable)arrayOfObject[j]);
        }
      }
      String str5 = MessageFormat.format(str3, arrayOfObject);
      return str5;
    }
    catch (MissingResourceException localMissingResourceException1) {}
    return getDefaultMessage(paramLocalizable);
  }
  
  private String getDefaultMessage(Localizable paramLocalizable)
  {
    String str = paramLocalizable.getKey();
    Object[] arrayOfObject = paramLocalizable.getArguments();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[failed to localize] ");
    localStringBuilder.append(str);
    if (arrayOfObject != null)
    {
      localStringBuilder.append('(');
      for (int i = 0; i < arrayOfObject.length; i++)
      {
        if (i != 0) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append(String.valueOf(arrayOfObject[i]));
      }
      localStringBuilder.append(')');
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\istack\internal\localization\Localizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */