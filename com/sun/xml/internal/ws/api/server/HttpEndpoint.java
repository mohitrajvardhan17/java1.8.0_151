package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;

public abstract class HttpEndpoint
{
  public HttpEndpoint() {}
  
  public static HttpEndpoint create(@NotNull WSEndpoint paramWSEndpoint)
  {
    return new com.sun.xml.internal.ws.transport.http.server.HttpEndpoint(null, HttpAdapter.createAlone(paramWSEndpoint));
  }
  
  public abstract void publish(@NotNull String paramString);
  
  public abstract void stop();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\HttpEndpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */