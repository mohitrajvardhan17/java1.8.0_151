package com.sun.net.httpserver;

import com.sun.net.httpserver.spi.HttpServerProvider;
import java.io.IOException;
import java.net.InetSocketAddress;
import jdk.Exported;

@Exported
public abstract class HttpsServer
  extends HttpServer
{
  protected HttpsServer() {}
  
  public static HttpsServer create()
    throws IOException
  {
    return create(null, 0);
  }
  
  public static HttpsServer create(InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException
  {
    HttpServerProvider localHttpServerProvider = HttpServerProvider.provider();
    return localHttpServerProvider.createHttpsServer(paramInetSocketAddress, paramInt);
  }
  
  public abstract void setHttpsConfigurator(HttpsConfigurator paramHttpsConfigurator);
  
  public abstract HttpsConfigurator getHttpsConfigurator();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\httpserver\HttpsServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */