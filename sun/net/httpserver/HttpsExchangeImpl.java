package sun.net.httpserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpsExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import javax.net.ssl.SSLSession;

class HttpsExchangeImpl
  extends HttpsExchange
{
  ExchangeImpl impl;
  
  HttpsExchangeImpl(ExchangeImpl paramExchangeImpl)
    throws IOException
  {
    impl = paramExchangeImpl;
  }
  
  public Headers getRequestHeaders()
  {
    return impl.getRequestHeaders();
  }
  
  public Headers getResponseHeaders()
  {
    return impl.getResponseHeaders();
  }
  
  public URI getRequestURI()
  {
    return impl.getRequestURI();
  }
  
  public String getRequestMethod()
  {
    return impl.getRequestMethod();
  }
  
  public HttpContextImpl getHttpContext()
  {
    return impl.getHttpContext();
  }
  
  public void close()
  {
    impl.close();
  }
  
  public InputStream getRequestBody()
  {
    return impl.getRequestBody();
  }
  
  public int getResponseCode()
  {
    return impl.getResponseCode();
  }
  
  public OutputStream getResponseBody()
  {
    return impl.getResponseBody();
  }
  
  public void sendResponseHeaders(int paramInt, long paramLong)
    throws IOException
  {
    impl.sendResponseHeaders(paramInt, paramLong);
  }
  
  public InetSocketAddress getRemoteAddress()
  {
    return impl.getRemoteAddress();
  }
  
  public InetSocketAddress getLocalAddress()
  {
    return impl.getLocalAddress();
  }
  
  public String getProtocol()
  {
    return impl.getProtocol();
  }
  
  public SSLSession getSSLSession()
  {
    return impl.getSSLSession();
  }
  
  public Object getAttribute(String paramString)
  {
    return impl.getAttribute(paramString);
  }
  
  public void setAttribute(String paramString, Object paramObject)
  {
    impl.setAttribute(paramString, paramObject);
  }
  
  public void setStreams(InputStream paramInputStream, OutputStream paramOutputStream)
  {
    impl.setStreams(paramInputStream, paramOutputStream);
  }
  
  public HttpPrincipal getPrincipal()
  {
    return impl.getPrincipal();
  }
  
  ExchangeImpl getExchangeImpl()
  {
    return impl;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\HttpsExchangeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */