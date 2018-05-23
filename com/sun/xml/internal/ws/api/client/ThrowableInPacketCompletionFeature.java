package com.sun.xml.internal.ws.api.client;

import javax.xml.ws.WebServiceFeature;

public class ThrowableInPacketCompletionFeature
  extends WebServiceFeature
{
  public ThrowableInPacketCompletionFeature()
  {
    enabled = true;
  }
  
  public String getID()
  {
    return ThrowableInPacketCompletionFeature.class.getName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\client\ThrowableInPacketCompletionFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */