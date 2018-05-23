package com.sun.xml.internal.ws.api.model;

public enum MEP
{
  REQUEST_RESPONSE(false),  ONE_WAY(false),  ASYNC_POLL(true),  ASYNC_CALLBACK(true);
  
  public final boolean isAsync;
  
  private MEP(boolean paramBoolean)
  {
    isAsync = paramBoolean;
  }
  
  public final boolean isOneWay()
  {
    return this == ONE_WAY;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\MEP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */