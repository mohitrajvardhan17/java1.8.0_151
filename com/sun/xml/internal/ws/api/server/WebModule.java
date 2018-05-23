package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;

public abstract class WebModule
  extends Module
{
  public WebModule() {}
  
  @NotNull
  public abstract String getContextPath();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\WebModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */