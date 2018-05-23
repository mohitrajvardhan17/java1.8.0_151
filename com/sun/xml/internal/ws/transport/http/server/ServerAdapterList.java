package com.sun.xml.internal.ws.transport.http.server;

import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;

public class ServerAdapterList
  extends HttpAdapterList<ServerAdapter>
{
  public ServerAdapterList() {}
  
  protected ServerAdapter createHttpAdapter(String paramString1, String paramString2, WSEndpoint<?> paramWSEndpoint)
  {
    return new ServerAdapter(paramString1, paramString2, paramWSEndpoint, this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\server\ServerAdapterList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */