package com.sun.xml.internal.fastinfoset;

import java.util.Locale;
import java.util.ResourceBundle;

public class CommonResourceBundle
  extends AbstractResourceBundle
{
  public static final String BASE_NAME = "com.sun.xml.internal.fastinfoset.resources.ResourceBundle";
  private static volatile CommonResourceBundle instance = null;
  private static Locale locale = null;
  private ResourceBundle bundle = null;
  
  protected CommonResourceBundle()
  {
    bundle = ResourceBundle.getBundle("com.sun.xml.internal.fastinfoset.resources.ResourceBundle");
  }
  
  protected CommonResourceBundle(Locale paramLocale)
  {
    bundle = ResourceBundle.getBundle("com.sun.xml.internal.fastinfoset.resources.ResourceBundle", paramLocale);
  }
  
  public static CommonResourceBundle getInstance()
  {
    if (instance == null) {
      synchronized (CommonResourceBundle.class)
      {
        instance = new CommonResourceBundle();
        locale = parseLocale(null);
      }
    }
    return instance;
  }
  
  public static CommonResourceBundle getInstance(Locale paramLocale)
  {
    if (instance == null) {
      synchronized (CommonResourceBundle.class)
      {
        instance = new CommonResourceBundle(paramLocale);
      }
    } else {
      synchronized (CommonResourceBundle.class)
      {
        if (locale != paramLocale) {
          instance = new CommonResourceBundle(paramLocale);
        }
      }
    }
    return instance;
  }
  
  public ResourceBundle getBundle()
  {
    return bundle;
  }
  
  public ResourceBundle getBundle(Locale paramLocale)
  {
    return ResourceBundle.getBundle("com.sun.xml.internal.fastinfoset.resources.ResourceBundle", paramLocale);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\CommonResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */