package com.sun.net.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import jdk.Exported;

@Exported
public abstract class HttpExchange
{
  protected HttpExchange() {}
  
  public abstract Headers getRequestHeaders();
  
  public abstract Headers getResponseHeaders();
  
  public abstract URI getRequestURI();
  
  public abstract String getRequestMethod();
  
  public abstract HttpContext getHttpContext();
  
  public abstract void close();
  
  public abstract InputStream getRequestBody();
  
  public abstract OutputStream getResponseBody();
  
  public abstract void sendResponseHeaders(int paramInt, long paramLong)
    throws IOException;
  
  public abstract InetSocketAddress getRemoteAddress();
  
  public abstract int getResponseCode();
  
  public abstract InetSocketAddress getLocalAddress();
  
  public abstract String getProtocol();
  
  public abstract Object getAttribute(String paramString);
  
  public abstract void setAttribute(String paramString, Object paramObject);
  
  public abstract void setStreams(InputStream paramInputStream, OutputStream paramOutputStream);
  
  public abstract HttpPrincipal getPrincipal();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\httpserver\HttpExchange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */