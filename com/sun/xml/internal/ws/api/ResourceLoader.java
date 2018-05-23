package com.sun.xml.internal.ws.api;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class ResourceLoader
{
  public ResourceLoader() {}
  
  public abstract URL getResource(String paramString)
    throws MalformedURLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\ResourceLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */