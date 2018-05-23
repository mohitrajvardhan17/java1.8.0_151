package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

class FactoryFinder
{
  private static ClassLoader cl = FactoryFinder.class.getClassLoader();
  
  FactoryFinder() {}
  
  static Object find(String paramString)
    throws ClassNotFoundException, InstantiationException, IllegalAccessException
  {
    String str1 = System.getProperty(paramString);
    if (str1 != null) {
      return newInstance(str1);
    }
    String str2 = findJarServiceProviderName(paramString);
    if ((str2 != null) && (str2.trim().length() > 0)) {
      return newInstance(str2);
    }
    return null;
  }
  
  static Object newInstance(String paramString)
    throws ClassNotFoundException, InstantiationException, IllegalAccessException
  {
    Class localClass = cl.loadClass(paramString);
    Object localObject = localClass.newInstance();
    return localObject;
  }
  
  private static String findJarServiceProviderName(String paramString)
  {
    String str1 = "META-INF/services/" + paramString;
    InputStream localInputStream = cl.getResourceAsStream(str1);
    if (localInputStream == null) {
      return null;
    }
    BufferedReader localBufferedReader = null;
    try
    {
      try
      {
        localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream, "UTF-8"));
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream));
      }
      String str2;
      try
      {
        str2 = localBufferedReader.readLine();
      }
      catch (IOException localIOException1)
      {
        String str3 = null;
        return str3;
      }
      return str2;
    }
    finally
    {
      if (localBufferedReader != null) {
        try
        {
          localBufferedReader.close();
        }
        catch (IOException localIOException4)
        {
          Logger.getLogger(FactoryFinder.class.getName()).log(Level.INFO, null, localIOException4);
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\FactoryFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */