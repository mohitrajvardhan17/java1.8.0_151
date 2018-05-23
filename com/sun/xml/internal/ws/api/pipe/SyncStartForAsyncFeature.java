package com.sun.xml.internal.ws.api.pipe;

import javax.xml.ws.WebServiceFeature;

public class SyncStartForAsyncFeature
  extends WebServiceFeature
{
  public SyncStartForAsyncFeature()
  {
    enabled = true;
  }
  
  public String getID()
  {
    return SyncStartForAsyncFeature.class.getSimpleName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\SyncStartForAsyncFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */