package com.sun.xml.internal.ws.api.message;

import javax.xml.ws.WebServiceFeature;

public class SuppressAutomaticWSARequestHeadersFeature
  extends WebServiceFeature
{
  public SuppressAutomaticWSARequestHeadersFeature()
  {
    enabled = true;
  }
  
  public String getID()
  {
    return SuppressAutomaticWSARequestHeadersFeature.class.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\SuppressAutomaticWSARequestHeadersFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */