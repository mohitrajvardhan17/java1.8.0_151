package com.sun.xml.internal.ws.api;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.ws.Service;

public abstract class WSDLLocator
{
  public WSDLLocator() {}
  
  public abstract URL locateWSDL(Class<Service> paramClass, String paramString)
    throws MalformedURLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\WSDLLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */