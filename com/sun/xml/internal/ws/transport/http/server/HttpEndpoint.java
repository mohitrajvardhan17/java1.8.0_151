package com.sun.xml.internal.ws.transport.http.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import javax.xml.ws.EndpointReference;
import org.w3c.dom.Element;

public final class HttpEndpoint
  extends com.sun.xml.internal.ws.api.server.HttpEndpoint
{
  private String address;
  private com.sun.net.httpserver.HttpContext httpContext;
  private final HttpAdapter adapter;
  private final Executor executor;
  
  public HttpEndpoint(Executor paramExecutor, HttpAdapter paramHttpAdapter)
  {
    executor = paramExecutor;
    adapter = paramHttpAdapter;
  }
  
  public void publish(String paramString)
  {
    address = paramString;
    httpContext = ServerMgr.getInstance().createContext(paramString);
    publish(httpContext);
  }
  
  public void publish(Object paramObject)
  {
    if ((paramObject instanceof javax.xml.ws.spi.http.HttpContext))
    {
      setHandler((javax.xml.ws.spi.http.HttpContext)paramObject);
      return;
    }
    if ((paramObject instanceof com.sun.net.httpserver.HttpContext))
    {
      httpContext = ((com.sun.net.httpserver.HttpContext)paramObject);
      setHandler(httpContext);
      return;
    }
    throw new ServerRtException(ServerMessages.NOT_KNOW_HTTP_CONTEXT_TYPE(paramObject.getClass(), com.sun.net.httpserver.HttpContext.class, javax.xml.ws.spi.http.HttpContext.class), new Object[0]);
  }
  
  HttpAdapterList getAdapterOwner()
  {
    return adapter.owner;
  }
  
  private String getEPRAddress()
  {
    if (address == null) {
      return httpContext.getServer().getAddress().toString();
    }
    try
    {
      URL localURL = new URL(address);
      if (localURL.getPort() == 0) {
        return new URL(localURL.getProtocol(), localURL.getHost(), httpContext.getServer().getAddress().getPort(), localURL.getFile()).toString();
      }
    }
    catch (MalformedURLException localMalformedURLException) {}
    return address;
  }
  
  public void stop()
  {
    if (httpContext != null) {
      if (address == null) {
        httpContext.getServer().removeContext(httpContext);
      } else {
        ServerMgr.getInstance().removeContext(httpContext);
      }
    }
    adapter.getEndpoint().dispose();
  }
  
  private void setHandler(com.sun.net.httpserver.HttpContext paramHttpContext)
  {
    paramHttpContext.setHandler(new WSHttpHandler(adapter, executor));
  }
  
  private void setHandler(javax.xml.ws.spi.http.HttpContext paramHttpContext)
  {
    paramHttpContext.setHandler(new PortableHttpHandler(adapter, executor));
  }
  
  public <T extends EndpointReference> T getEndpointReference(Class<T> paramClass, Element... paramVarArgs)
  {
    String str = getEPRAddress();
    return (EndpointReference)paramClass.cast(adapter.getEndpoint().getEndpointReference(paramClass, str, str + "?wsdl", paramVarArgs));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\server\HttpEndpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */