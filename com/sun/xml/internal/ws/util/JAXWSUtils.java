package com.sun.xml.internal.ws.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;

public final class JAXWSUtils
{
  public JAXWSUtils() {}
  
  public static String getUUID()
  {
    return UUID.randomUUID().toString();
  }
  
  public static String getFileOrURLName(String paramString)
  {
    try
    {
      return escapeSpace(new URL(paramString).toExternalForm());
    }
    catch (MalformedURLException localMalformedURLException)
    {
      return new File(paramString).getCanonicalFile().toURL().toExternalForm();
    }
    catch (Exception localException) {}
    return paramString;
  }
  
  public static URL getFileOrURL(String paramString)
    throws IOException
  {
    try
    {
      URL localURL = new URL(paramString);
      String str = String.valueOf(localURL.getProtocol()).toLowerCase();
      if ((str.equals("http")) || (str.equals("https"))) {
        return new URL(localURL.toURI().toASCIIString());
      }
      return localURL;
    }
    catch (URISyntaxException localURISyntaxException)
    {
      return new File(paramString).toURL();
    }
    catch (MalformedURLException localMalformedURLException) {}
    return new File(paramString).toURL();
  }
  
  public static URL getEncodedURL(String paramString)
    throws MalformedURLException
  {
    URL localURL = new URL(paramString);
    String str = String.valueOf(localURL.getProtocol()).toLowerCase();
    if ((str.equals("http")) || (str.equals("https"))) {
      try
      {
        return new URL(localURL.toURI().toASCIIString());
      }
      catch (URISyntaxException localURISyntaxException)
      {
        MalformedURLException localMalformedURLException = new MalformedURLException(localURISyntaxException.getMessage());
        localMalformedURLException.initCause(localURISyntaxException);
        throw localMalformedURLException;
      }
    }
    return localURL;
  }
  
  private static String escapeSpace(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramString.length(); i++) {
      if (paramString.charAt(i) == ' ') {
        localStringBuilder.append("%20");
      } else {
        localStringBuilder.append(paramString.charAt(i));
      }
    }
    return localStringBuilder.toString();
  }
  
  public static String absolutize(String paramString)
  {
    try
    {
      URL localURL = new File(".").getCanonicalFile().toURL();
      return new URL(localURL, paramString).toExternalForm();
    }
    catch (IOException localIOException) {}
    return paramString;
  }
  
  public static void checkAbsoluteness(String paramString)
  {
    try
    {
      new URL(paramString);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      try
      {
        new URI(paramString);
      }
      catch (URISyntaxException localURISyntaxException)
      {
        throw new IllegalArgumentException("system ID '" + paramString + "' isn't absolute", localURISyntaxException);
      }
    }
  }
  
  public static boolean matchQNames(QName paramQName1, QName paramQName2)
  {
    if ((paramQName1 == null) || (paramQName2 == null)) {
      return false;
    }
    if (paramQName2.getNamespaceURI().equals(paramQName1.getNamespaceURI()))
    {
      String str = paramQName2.getLocalPart().replaceAll("\\*", ".*");
      return Pattern.matches(str, paramQName1.getLocalPart());
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\JAXWSUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */