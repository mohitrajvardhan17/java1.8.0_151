package com.sun.org.apache.xml.internal.resolver.helpers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public abstract class FileURL
{
  protected FileURL() {}
  
  public static URL makeURL(String paramString)
    throws MalformedURLException
  {
    File localFile = new File(paramString);
    return localFile.toURI().toURL();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\helpers\FileURL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */