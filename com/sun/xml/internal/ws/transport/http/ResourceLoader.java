package com.sun.xml.internal.ws.transport.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public abstract interface ResourceLoader
{
  public abstract URL getResource(String paramString)
    throws MalformedURLException;
  
  public abstract URL getCatalogFile()
    throws MalformedURLException;
  
  public abstract Set<String> getResourcePaths(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\ResourceLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */