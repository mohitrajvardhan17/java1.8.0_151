package com.sun.rowset;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class JdbcRowSetResourceBundle
  implements Serializable
{
  private static String fileName;
  private transient PropertyResourceBundle propResBundle;
  private static volatile JdbcRowSetResourceBundle jpResBundle;
  private static final String PROPERTIES = "properties";
  private static final String UNDERSCORE = "_";
  private static final String DOT = ".";
  private static final String SLASH = "/";
  private static final String PATH = "com/sun/rowset/RowSetResourceBundle";
  static final long serialVersionUID = 436199386225359954L;
  
  private JdbcRowSetResourceBundle()
    throws IOException
  {
    Locale localLocale = Locale.getDefault();
    propResBundle = ((PropertyResourceBundle)ResourceBundle.getBundle("com/sun/rowset/RowSetResourceBundle", localLocale, Thread.currentThread().getContextClassLoader()));
  }
  
  public static JdbcRowSetResourceBundle getJdbcRowSetResourceBundle()
    throws IOException
  {
    if (jpResBundle == null) {
      synchronized (JdbcRowSetResourceBundle.class)
      {
        if (jpResBundle == null) {
          jpResBundle = new JdbcRowSetResourceBundle();
        }
      }
    }
    return jpResBundle;
  }
  
  public Enumeration getKeys()
  {
    return propResBundle.getKeys();
  }
  
  public Object handleGetObject(String paramString)
  {
    return propResBundle.handleGetObject(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\JdbcRowSetResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */