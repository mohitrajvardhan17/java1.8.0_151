package com.sun.net.httpserver;

import java.util.List;
import java.util.Map;
import jdk.Exported;

@Exported
public abstract class HttpContext
{
  protected HttpContext() {}
  
  public abstract HttpHandler getHandler();
  
  public abstract void setHandler(HttpHandler paramHttpHandler);
  
  public abstract String getPath();
  
  public abstract HttpServer getServer();
  
  public abstract Map<String, Object> getAttributes();
  
  public abstract List<Filter> getFilters();
  
  public abstract Authenticator setAuthenticator(Authenticator paramAuthenticator);
  
  public abstract Authenticator getAuthenticator();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\httpserver\HttpContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */