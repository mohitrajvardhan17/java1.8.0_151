package com.sun.naming.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.NamingEnumeration;

public abstract class VersionHelper
{
  private static VersionHelper helper = new VersionHelper12();
  static final String[] PROPS = { "java.naming.factory.initial", "java.naming.factory.object", "java.naming.factory.url.pkgs", "java.naming.factory.state", "java.naming.provider.url", "java.naming.dns.url", "java.naming.factory.control" };
  public static final int INITIAL_CONTEXT_FACTORY = 0;
  public static final int OBJECT_FACTORIES = 1;
  public static final int URL_PKG_PREFIXES = 2;
  public static final int STATE_FACTORIES = 3;
  public static final int PROVIDER_URL = 4;
  public static final int DNS_URL = 5;
  public static final int CONTROL_FACTORIES = 6;
  
  VersionHelper() {}
  
  public static VersionHelper getVersionHelper()
  {
    return helper;
  }
  
  public abstract Class<?> loadClass(String paramString)
    throws ClassNotFoundException;
  
  abstract Class<?> loadClass(String paramString, ClassLoader paramClassLoader)
    throws ClassNotFoundException;
  
  public abstract Class<?> loadClass(String paramString1, String paramString2)
    throws ClassNotFoundException, MalformedURLException;
  
  abstract String getJndiProperty(int paramInt);
  
  abstract String[] getJndiProperties();
  
  abstract InputStream getResourceAsStream(Class<?> paramClass, String paramString);
  
  abstract InputStream getJavaHomeLibStream(String paramString);
  
  abstract NamingEnumeration<InputStream> getResources(ClassLoader paramClassLoader, String paramString)
    throws IOException;
  
  abstract ClassLoader getContextClassLoader();
  
  protected static URL[] getUrlArray(String paramString)
    throws MalformedURLException
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
    Vector localVector = new Vector(10);
    while (localStringTokenizer.hasMoreTokens()) {
      localVector.addElement(localStringTokenizer.nextToken());
    }
    String[] arrayOfString = new String[localVector.size()];
    for (int i = 0; i < arrayOfString.length; i++) {
      arrayOfString[i] = ((String)localVector.elementAt(i));
    }
    URL[] arrayOfURL = new URL[arrayOfString.length];
    for (int j = 0; j < arrayOfURL.length; j++) {
      arrayOfURL[j] = new URL(arrayOfString[j]);
    }
    return arrayOfURL;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\naming\internal\VersionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */