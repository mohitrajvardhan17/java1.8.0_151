package com.sun.xml.internal.bind.util;

import java.net.URL;

public class Which
{
  public Which() {}
  
  public static String which(Class paramClass)
  {
    return which(paramClass.getName(), SecureLoader.getClassClassLoader(paramClass));
  }
  
  public static String which(String paramString, ClassLoader paramClassLoader)
  {
    String str = paramString.replace('.', '/') + ".class";
    if (paramClassLoader == null) {
      paramClassLoader = SecureLoader.getSystemClassLoader();
    }
    URL localURL = paramClassLoader.getResource(str);
    if (localURL != null) {
      return localURL.toString();
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\util\Which.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */